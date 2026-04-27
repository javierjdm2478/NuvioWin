@echo off
setlocal
powershell.exe -ExecutionPolicy Bypass -File "%~dp0scripts\build-windows.ps1" %*
exit /b %ERRORLEVEL%
