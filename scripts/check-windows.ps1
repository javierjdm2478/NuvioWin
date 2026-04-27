$ErrorActionPreference = "Stop"
$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

Push-Location $RepoRoot
try {
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        throw "Java was not found. Install JDK 17+ or 21 and set JAVA_HOME."
    }

    & .\gradlew.bat :composeApp:compileKotlinDesktop --stacktrace
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
} finally {
    Pop-Location
}
