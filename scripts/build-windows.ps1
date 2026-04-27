param(
    [string]$Version = $env:NUVIO_WIN_VERSION
)

$ErrorActionPreference = "Stop"
$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$ArtifactsDir = Join-Path $RepoRoot "artifacts\windows"

Push-Location $RepoRoot
try {
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        throw "Java was not found. Install JDK 17+ or 21 and set JAVA_HOME."
    }

    if (Test-Path $ArtifactsDir) {
        Remove-Item -LiteralPath $ArtifactsDir -Recurse -Force
    }
    New-Item -ItemType Directory -Force -Path $ArtifactsDir | Out-Null

    $gradleArgs = @(
        ":composeApp:createDistributable",
        ":composeApp:packageExe",
        ":composeApp:packageMsi",
        "--stacktrace"
    )
    if (-not [string]::IsNullOrWhiteSpace($Version)) {
        $gradleArgs += "-PnuvioWinVersion=$Version"
    }

    & .\gradlew.bat @gradleArgs
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }

    Get-ChildItem -Path "composeApp\build\compose\binaries" -Recurse -File -Include *.exe,*.msi |
        Copy-Item -Destination $ArtifactsDir -Force

    $portableDir = Get-ChildItem -Path "composeApp\build\compose\binaries" -Recurse -Directory |
        Where-Object { $_.Name -eq "NuvioWin" -and $_.FullName -match "\\app\\" } |
        Select-Object -First 1

    if ($portableDir -ne $null) {
        $zipPath = Join-Path $ArtifactsDir "NuvioWin-portable.zip"
        if (Test-Path $zipPath) {
            Remove-Item -LiteralPath $zipPath -Force
        }
        Compress-Archive -Path (Join-Path $portableDir.FullName "*") -DestinationPath $zipPath -Force
    } else {
        throw "Portable distributable directory was not found under composeApp\build\compose\binaries."
    }

    Get-ChildItem -Path $ArtifactsDir -File | ForEach-Object {
        Write-Host "Artifact: $($_.FullName)"
    }
} finally {
    Pop-Location
}
