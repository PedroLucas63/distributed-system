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
cd "$PROJECT_ROOT"

echo "=========================================================="
echo " Iniciando build e instalação dos módulos (Maven)..."
echo "=========================================================="

mvn clean install "$@"

echo "=========================================================="
echo " Build concluído com sucesso!"
echo "=========================================================="
