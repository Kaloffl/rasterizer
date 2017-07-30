@echo off
echo "Building file %1"
if not exist bin md bin
scalac.bat -classpath bin -d bin %1