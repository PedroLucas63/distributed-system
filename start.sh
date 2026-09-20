#!/usr/bin/env bash
set -e

# Valida se JAVA_HOME existe e contém o binário java
if [ -z "$JAVA_HOME" ] || [ ! -f "$JAVA_HOME/bin/java" ]; then
    if [ -f "/home/linuxbrew/.linuxbrew/opt/openjdk/libexec/bin/java" ]; then
        export JAVA_HOME="/home/linuxbrew/.linuxbrew/opt/openjdk/libexec"
    elif [ -f "/var/home/linuxbrew/.linuxbrew/Cellar/openjdk/26.0.2.1/libexec/bin/java" ]; then
        export JAVA_HOME="/var/home/linuxbrew/.linuxbrew/Cellar/openjdk/26.0.2.1/libexec"
    fi
fi

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mkdir -p "$PROJECT_ROOT/logs"

cleanup() {
    echo -e "\n==> Encerrando todos os serviços..."
    trap - SIGINT SIGTERM EXIT
    # Finaliza todos os processos filhos em background
    kill $(jobs -p) 2>/dev/null || true
    wait $(jobs -p) 2>/dev/null || true
    echo "==> Todos os serviços foram finalizados com sucesso."
}

trap cleanup SIGINT SIGTERM EXIT

echo "=========================================================="
echo " Inicializando o Sistema Distribuído"
echo "=========================================================="

echo "==> [1/3] Iniciando Gateway (Portas: HTTP 8080, UDP 9090, gRPC 50051)..."
(cd "$PROJECT_ROOT/gateway" && exec mvn exec:java -Dexec.mainClass="br.ufrn.dimap.Main" > "$PROJECT_ROOT/logs/gateway.log" 2>&1) &

# Aguarda o Gateway subir e abrir a porta HTTP 8080 (máximo 15 segundos)
echo -n "==> Aguardando Gateway ficar pronto"
GATEWAY_READY=false
for i in $(seq 1 30); do
    if (echo > /dev/tcp/127.0.0.1/8080) 2>/dev/null; then
        GATEWAY_READY=true
        echo " [OK]"
        break
    fi
    echo -n "."
    sleep 0.5
done

if [ "$GATEWAY_READY" = false ]; then
    echo " [FALHA]"
    echo "Erro: Gateway não inicializou a tempo. Verifique o arquivo logs/gateway.log"
    exit 1
fi

echo "==> [2/3] Iniciando Node-A (Prefixo: bb, Portas: HTTP 8081, UDP 9091, gRPC 50052)..."
(cd "$PROJECT_ROOT/node-a" && exec mvn exec:java -Dexec.mainClass="br.ufrn.dimap.Main" > "$PROJECT_ROOT/logs/node-a.log" 2>&1) &

sleep 1

echo "==> [3/3] Iniciando Node-B (Prefixo: master, Portas: HTTP 8082, UDP 9092, gRPC 50053)..."
(cd "$PROJECT_ROOT/node-b" && exec mvn exec:java -Dexec.mainClass="br.ufrn.dimap.Main" > "$PROJECT_ROOT/logs/node-b.log" 2>&1) &

sleep 1

echo ""
echo "=========================================================="
echo " Todos os 3 serviços estão em execução!"
echo " - Gateway: HTTP 8080 | UDP 9090 | gRPC 50051"
echo " - Node-A : HTTP 8081 | UDP 9091 | gRPC 50052"
echo " - Node-B : HTTP 8082 | UDP 9092 | gRPC 50053"
echo " Logs sendo gravados em: $PROJECT_ROOT/logs/"
echo " Pressione Ctrl+C para encerrar todos os serviços."
echo "=========================================================="

# Mantém o script ativo aguardando sinal de encerramento
wait
