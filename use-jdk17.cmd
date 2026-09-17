@echo off
set "JAVA_HOME=C:\Users\12345\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.20.1+1"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JAVA_HOME=%JAVA_HOME%
java -version
javac -version
