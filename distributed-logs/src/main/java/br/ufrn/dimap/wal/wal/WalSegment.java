package br.ufrn.dimap.wal.wal;

import br.ufrn.dimap.wal.exceptions.WalCorruptedException;
import br.ufrn.dimap.wal.exceptions.WalException;
import br.ufrn.dimap.wal.exceptions.WalUnknownTypeException;
import br.ufrn.dimap.wal.types.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.CRC32;

public class WalSegment {
    private final static ObjectMapper MAPPER = new ObjectMapper();
    private final static CRC32 CRC = new CRC32();
    private final static int LOG_TYPE_SIZE = Integer.BYTES;
    private final static int ENTRY_HEADER_SIZE = Integer.BYTES * 3;
    private final static int TRANSACTION_HEADER_SIZE = Integer.BYTES * 2;
    private final static int TAG_HEADER_SIZE = Integer.BYTES * 2;
    private final static int CRC_SIZE = Long.BYTES;
    private static final int UUID_SIZE = Long.BYTES * 2;

    private final String filename;
    private boolean loaded = false;
    private boolean disposed;
    private FileChannel channel;
    private final List<ILog> entries = new ArrayList<>();

    public WalSegment(String filename) {
        this.filename = filename;
    }

    public void load(Map<Integer, Class<? extends IWalEntry>> types) {
        throwIfDisposed();
        throwIfLoaded();

        try (var stream = new FileInputStream(filename)) {
            readEntries(stream, types);
            loaded = true;
        } catch (IOException e) {
            throw new WalException("Could not load WAL.", e);
        }
    }

    public <T extends IWalEntry> void append(T entry, int typeId) throws IOException {
        append(entry, entry.getClass(), typeId);
    }

    public void beginTransaction(UUID transactionId) throws IOException {
        throwIfDisposed();
        throwIfNonLoaded();
        throwIfNull(transactionId);
        openChannel();

        channel.position(channel.size());

        ByteBuffer buffer = ByteBuffer.allocate(
                TRANSACTION_HEADER_SIZE + UUID_SIZE
        );

        buffer.putInt(LogType.Begin.getValue());
        buffer.putInt(CommandType.Transaction.getValue());
        buffer.putLong(transactionId.getMostSignificantBits());
        buffer.putLong(transactionId.getLeastSignificantBits());

        buffer.flip();

        while (buffer.hasRemaining()) {
            var _ = channel.write(buffer);
        }

        channel.force(true);
    }

    public void commit(UUID transactionId) throws IOException {
        throwIfDisposed();
        throwIfNonLoaded();
        throwIfNull(transactionId);
        openChannel();

        channel.position(channel.size());

        ByteBuffer buffer = ByteBuffer.allocate(
                TRANSACTION_HEADER_SIZE + UUID_SIZE
        );

        buffer.putInt(LogType.Commit.getValue());
        buffer.putInt(CommandType.Transaction.getValue());
        buffer.putLong(transactionId.getMostSignificantBits());
        buffer.putLong(transactionId.getLeastSignificantBits());

        buffer.flip();

        while (buffer.hasRemaining()) {
            var _ = channel.write(buffer);
        }

        channel.force(true);
    }

    public void tag(String tag) throws IOException {
        throwIfDisposed();
        throwIfNonLoaded();
        throwIfNull(tag);
        openChannel();

        channel.position(channel.size());

        byte[] tagBytes = tag.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(
                TAG_HEADER_SIZE + tagBytes.length
        );

        buffer.putInt(LogType.Tag.getValue());
        buffer.putInt(tagBytes.length);
        buffer.put(tagBytes);

        buffer.flip();

        while (buffer.hasRemaining()) {
            var _ = channel.write(buffer);
        }

        channel.force(true);
    }

    public List<ILog> getEntries() {
        throwIfNonLoaded();

        return entries;
    }

    public void openChannel() throws IOException {
        if (channel != null) {
            return;
        }

        channel = FileChannel.open(
                Path.of(filename),
                StandardOpenOption.CREATE,
                StandardOpenOption.READ,
                StandardOpenOption.WRITE
        );

        disposed = false;
    }

    public String getFilename() {
        return filename;
    }


    private void append(IWalEntry entry, Class<?> clazz, int typeId) throws IOException {
        throwIfDisposed();
        throwIfNonLoaded();
        throwIfNull(entry);
        openChannel();

        var payload = MAPPER
                .writerFor(clazz)
                .writeValueAsBytes(entry);

        appendEntry(payload, typeId);
    }

    private void appendEntry(byte[] payload, int typeId) throws IOException {
        CRC.update(payload);
        long crc = CRC.getValue();

        channel.position(channel.size());

        ByteBuffer buffer = ByteBuffer.allocate(
                ENTRY_HEADER_SIZE + payload.length + CRC_SIZE
        );

        buffer.putInt(LogType.Entry.getValue());
        buffer.putInt(typeId);
        buffer.putInt(payload.length);
        buffer.put(payload);
        buffer.putLong(crc);

        buffer.flip();

        while (buffer.hasRemaining()) {
            var _ = channel.write(buffer);
        }

        channel.force(true);
    }


    private void readEntries(
            FileInputStream stream,
            Map<Integer, Class<? extends IWalEntry>> types
    ) throws IOException {
        long offset = 0;

        while (stream.available() > 0) {
            if (stream.available() < LOG_TYPE_SIZE) {
                throw new WalCorruptedException(
                        "Incomplete WAL record header at offset " + offset + "."
                );
            }

            var channel = stream.getChannel();

            int typeId = readInt(channel);
            LogType type = LogType.fromValue(typeId);

            ILog log = switch (type) {
                case Begin -> readBegin(channel);
                case Entry -> readEntry(channel, types);
                case Commit -> readCommit(channel);
                case Tag -> readTag(channel);
            };

            entries.add(log);

            offset = stream.getChannel().position();
        }
    }

    private BeginLog readBegin(FileChannel stream) throws IOException {
        final int size = Integer.BYTES + UUID_SIZE;

        long entryOffset = stream.position();

        if (stream.size() - entryOffset < size) {
            throw new WalCorruptedException(
                    "Incomplete WAL record header at offset " + entryOffset + "."
            );
        }

        int typeId = readInt(stream);

        UUID id = readUuid(stream);

        return new BeginLog(
            CommandType.fromValue(typeId),
            id
        );
    }

    private CommitLog readCommit(FileChannel stream) throws IOException {
        final int size = Integer.BYTES + UUID_SIZE;

        long entryOffset = stream.position();

        if (stream.size() - entryOffset < size) {
            throw new WalCorruptedException(
                    "Incomplete WAL record header at offset "
                            + entryOffset + "."
            );
        }

        int typeId = readInt(stream);

        UUID id = readUuid(stream);

        return new CommitLog(
                CommandType.fromValue(typeId),
                id
        );
    }

    private TagLog readTag(FileChannel stream) throws IOException {
        int length = readInt(stream);

        if (length < 0) {
            throw new WalCorruptedException(
                "Invalid tag length " + length + "."
            );
        }

        long remaining = stream.size() - stream.position();

        if (remaining < length) {
            throw new WalCorruptedException(
                    "Incomplete WAL tag. Expected "
                            + length
                            + " bytes, but only "
                            + remaining
                            + " bytes remain."
            );
        }

        byte[] bytes = readBytes(stream, length);
        String tag = new String(bytes, StandardCharsets.UTF_8);
        return new TagLog(tag);
    }

    private EntryLog readEntry(
            FileChannel stream,
            Map<Integer, Class<? extends IWalEntry>> types
    ) throws IOException {
        final int headerSize = Integer.BYTES + Integer.BYTES;

        long entryOffset = stream.position();

        if (stream.size() - entryOffset < headerSize) {
            throw new WalCorruptedException(
                    "Incomplete WAL record header at offset "
                            + entryOffset + "."
            );
        }

        int typeId = readInt(stream);
        int length = readInt(stream);

        if (length < 0) {
            throw new WalCorruptedException(
                    "Invalid payload length "
                            + length
                            + " for entry type "
                            + typeId
                            + " at offset "
                            + entryOffset
                            + "."
            );
        }

        long remaining = stream.size() - stream.position();

        if (remaining < (long) length + CRC_SIZE) {
            throw new WalCorruptedException(
                    "Incomplete WAL record for entry type "
                            + typeId
                            + " at offset "
                            + entryOffset
                            + ". Expected "
                            + (length + CRC_SIZE)
                            + " bytes, but only "
                            + remaining
                            + " bytes remain."
            );
        }

        byte[] payload = readBytes(stream, length);

        if (payload.length != length) {
            throw new WalCorruptedException(
                    "Could not read the complete payload for entry type "
                            + typeId
                            + " at offset "
                            + entryOffset
                            + ". Expected "
                            + length
                            + " bytes, got "
                            + payload.length
                            + "."
            );
        }

        long storedCrc = Integer.toUnsignedLong(readInt(stream));

        CRC32 crc32 = new CRC32();
        crc32.update(payload);

        long calculatedCrc = crc32.getValue();

        if (storedCrc != calculatedCrc) {
            throw new WalCorruptedException(
                    String.format(
                            "CRC mismatch for entry type %d at offset %d. "
                                    + "Expected 0x%08X, calculated 0x%08X.",
                            typeId,
                            entryOffset,
                            storedCrc,
                            calculatedCrc
                    )
            );
        }

        Class<? extends IWalEntry> type = types.get(typeId);

        if (type == null) {
            throw new WalUnknownTypeException(typeId);
        }

        IWalEntry entry;

        try {
            entry = MAPPER
                    .readerFor(type)
                    .readValue(payload);
        } catch (JsonProcessingException e) {
            throw new WalCorruptedException(
                    "Invalid JSON payload for entry type "
                            + typeId
                            + " at offset "
                            + entryOffset
                            + ".",
                    e
            );
        }

        if (entry == null) {
            throw new WalCorruptedException(
                    "The WAL entry at offset "
                            + entryOffset
                            + " with type ID "
                            + typeId
                            + " deserialized to null."
            );
        }

        return new EntryLog(entry);
    }

    private void throwIfDisposed() {
        if (disposed) {
            throw new IllegalStateException("Object has been disposed");
        }
    }

    private void throwIfNonLoaded() {
        if (!loaded) {
            throw new IllegalStateException("Object not loaded");
        }
    }

    private void throwIfLoaded() {
        if (loaded) {
            throw new IllegalStateException("Object has been loaded");
        }
    }

    private void throwIfNull(Object value){
        if (value == null) {
            throw new IllegalStateException("Object has been disposed");
        }
    }

    private UUID readUuid(FileChannel stream) throws IOException {
        long most = readLong(stream);
        long least = readLong(stream);

        return new UUID(most, least);
    }

    private int readInt(FileChannel stream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        readFully(stream, buffer);
        buffer.flip();
        return buffer.getInt();
    }

    private long readLong(FileChannel stream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        readFully(stream, buffer);
        buffer.flip();
        return buffer.getLong();
    }

    private byte[] readBytes(FileChannel stream, int length) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        readFully(stream, buffer);
        return buffer.array();
    }

    private void readFully(FileChannel stream, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int read = stream.read(buffer);

            if (read < 0) {
                throw new EOFException();
            }
        }
    }
}
