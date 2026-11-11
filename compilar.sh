#!/bin/sh
# Compila o projeto para a pasta out/. Execute a partir da raiz do repositório.
set -e
rm -rf out
javac -encoding UTF-8 -d out $(find src -name "*.java")
echo "Compilado em out/"
