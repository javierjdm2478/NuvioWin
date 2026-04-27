param(
    [string]$Version = $env:NUVIO_WIN_VERSION
)

$ErrorActionPreference = "Stop"
$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

Push-Location $RepoRoot
try {
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        throw "Java was not found. Install JDK 17+ or 21 and set JAVA_HOME."
    }

    $gradleArgs = @(":composeApp:run", "--stacktrace")
    if (-not [string]::IsNullOrWhiteSpace($Version)) {
        $gradleArgs += "-PnuvioWinVersion=$Version"
    }

    & .\gradlew.bat @gradleArgs
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
} finally {
    Pop-Location
}
