$Jdk17Home = "C:\Users\12345\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.20.1+1"
$Jdk17Bin = Join-Path $Jdk17Home "bin"

if (-not (Test-Path -LiteralPath (Join-Path $Jdk17Bin "java.exe"))) {
    throw "JDK 17 was not found at: $Jdk17Home"
}

$env:JAVA_HOME = $Jdk17Home
$pathParts = $env:Path -split ";" | Where-Object { $_ -and $_ -ne $Jdk17Bin }
$env:Path = (($Jdk17Bin) + ";" + ($pathParts -join ";")).TrimEnd(";")

Write-Host "JAVA_HOME=$env:JAVA_HOME"
java -version
javac -version
