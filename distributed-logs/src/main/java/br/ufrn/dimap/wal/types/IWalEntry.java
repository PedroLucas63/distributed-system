package br.ufrn.dimap.wal.types;

public interface IWalEntry {
    long getIndex();
    long getTerm();
    long getTimestamp();
}
