@echo off
rem Compila o projeto para a pasta out\. Execute a partir de MinhaRefeicao\.
if exist out rmdir /s /q out
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -d out @fontes.txt
if errorlevel 1 (
    del fontes.txt
    echo Erro de compilacao.
    exit /b 1
)
del fontes.txt
echo Compilado em out\
