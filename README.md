# NuvioWin

NuvioWin is a Windows 10/11 desktop port of the official Nuvio Kotlin Multiplatform app. It keeps the shared Compose UI and feature architecture from `NuvioMedia/NuvioMobile`, then adds a JVM desktop target, Windows-safe storage, Windows packaging, and GitHub Actions releases.

Upstream source: `NuvioMedia/NuvioMobile`, branch `cmp-rewrite`, commit `7ef0083a71a938cb7629b36ef5968a3c78fb9c0f`. See `UPSTREAM.md` for provenance.

## Architecture

- `composeApp/src/commonMain`: shared Compose UI, repositories, navigation, profiles, add-ons, streams, watch progress, settings, and sync logic.
- `composeApp/src/androidMain` and `composeApp/src/iosMain`: original mobile platform integrations kept from upstream.
- `composeApp/src/desktopMain`: NuvioWin platform implementations for Windows desktop.
- Packaging uses JetBrains Compose Desktop native distributions with `.exe` and `.msi` targets.

## Install

Download the latest Windows artifacts from GitHub Releases:

- `NuvioWin-*.exe`
- `NuvioWin-*.msi`
- `NuvioWin-portable.zip`

For the portable zip, extract it and run `NuvioWin.exe`.

## Use

- App data: `%APPDATA%\NuvioWin`
- Downloads: `%APPDATA%\NuvioWin\Downloads`
- Back navigation: `Escape` or `Alt+Left`
- Player: `Space`/`K` play-pause, `Left`/`J` seek back, `Right`/`L` seek forward

Some streams need headers, separate audio, or codecs JavaFX cannot play. In those cases NuvioWin shows a fallback to open the link externally or copy it.

## Develop

Requirements:

- Windows 10/11
- JDK 17+; JDK 21 recommended
- PowerShell

Run checks:

```powershell
.\scripts\check-windows.ps1
```

Run locally:

```powershell
.\scripts\run-windows.ps1
```

Build Windows installers and portable zip:

```powershell
.\scripts\build-windows.ps1 -Version v0.1.0
```

Artifacts are written to `artifacts/windows`.

## Release

GitHub Actions builds release artifacts on tags matching `v*` and creates a GitHub Release automatically.

```powershell
git tag v0.1.0
git push origin v0.1.0
```

Pull requests run:

```powershell
.\gradlew.bat :composeApp:compileKotlinDesktop --stacktrace
```

## License

NuvioWin is distributed under the GNU General Public License v3.0, matching the official upstream project. See `LICENSE`.
