..\use-jdk17.ps1
$env:MYSQL_ROOT_PASSWORD = "cx775892"
Remove-Item Env:DEBUG -ErrorAction SilentlyContinue
& "C:\Users\12345\.vscode\extensions\oracle.oracle-java-26.0.2\nbcode\java\maven\bin\mvn.cmd" spring-boot:run
