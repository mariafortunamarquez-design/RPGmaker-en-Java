@echo off
rem Creado por María Fortuna
setlocal EnableExtensions EnableDelayedExpansion

set "ROOT=%~dp0"
if "%ROOT:~-1%"=="\" set "ROOT=%ROOT:~0,-1%"
if defined JAVA_HOME (
    set "JDK_BIN=%JAVA_HOME%\bin"
) else (
    for %%J in (jpackage.exe) do set "JPACKAGE_PATH=%%~$PATH:J"
    if not defined JPACKAGE_PATH (
        echo No se encuentra jpackage. Instala JDK 25 y configura JAVA_HOME.
        exit /b 1
    )
    for %%I in ("!JPACKAGE_PATH!") do set "JDK_BIN=%%~dpI"
)
if "%JDK_BIN:~-1%"=="\" set "JDK_BIN=%JDK_BIN:~0,-1%"

if not exist "%JDK_BIN%\javac.exe" (
    echo No se encuentra javac en "%JDK_BIN%". Configura JAVA_HOME con un JDK 25.
    exit /b 1
)
if not exist "%JDK_BIN%\jar.exe" (
    echo No se encuentra jar en "%JDK_BIN%". Configura JAVA_HOME con un JDK 25.
    exit /b 1
)
if not exist "%JDK_BIN%\jpackage.exe" (
    echo No se encuentra jpackage en "%JDK_BIN%". Configura JAVA_HOME con un JDK 25.
    exit /b 1
)

set "PATH=%JDK_BIN%;%PATH%"
if defined WIX set "PATH=%WIX%;%PATH%"
set "BUILD=%ROOT%\build"
set "STAGE=%BUILD%\classes"
set "INPUT=%BUILD%\input"
set "DIST=%ROOT%\dist"

if exist "%BUILD%\classes" rmdir /s /q "%BUILD%\classes"
if exist "%DIST%\RPGMaker-source" rmdir /s /q "%DIST%\RPGMaker-source"
for /r "%ROOT%" %%F in (*.class) do del /q "%%F"
if not exist "%STAGE%" mkdir "%STAGE%"
if not exist "%INPUT%" mkdir "%INPUT%"
if not exist "%DIST%" mkdir "%DIST%"
if exist "%INPUT%\RPGMaker.jar" del /q "%INPUT%\RPGMaker.jar"
if exist "%DIST%\RPGMaker-1.0.0.exe" attrib -R "%DIST%\RPGMaker-1.0.0.exe"
if exist "%DIST%\RPGMaker-1.0.0.exe" del /f /q "%DIST%\RPGMaker-1.0.0.exe"
if exist "%BUILD%\sources.args" del /q "%BUILD%\sources.args"

for /r "%ROOT%" %%F in (*.java) do (
    set "SOURCE=%%~fF"
    echo !SOURCE! | findstr /i /c:"\classes\" /c:"\build\" /c:"\dist\" >nul
    if errorlevel 1 echo "!SOURCE:\=\\!">>"%BUILD%\sources.args"
)

"%JDK_BIN%\javac.exe" -encoding UTF-8 -d "%STAGE%" @"%BUILD%\sources.args"
if errorlevel 1 goto failed

call :copyPackage "com\rpgmaker" "classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\controller" "controller\classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\editor" "editor\classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\editor\node" "editor\node\classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\export" "export\classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\io" "io\classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\model" "model\classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\model\event" "model\event\classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\preview" "preview\classes"
if errorlevel 1 goto failed
call :copyPackage "com\rpgmaker\util" "util\classes"
if errorlevel 1 goto failed

"%JDK_BIN%\jar.exe" --create --file "%INPUT%\RPGMaker.jar" -C "%STAGE%" .
if errorlevel 1 goto failed

mkdir "%DIST%\RPGMaker-source"
robocopy "%ROOT%" "%DIST%\RPGMaker-source" *.java README.md build.bat .gitignore /S /XF *.class /XD build dist
if errorlevel 8 goto failed
powershell.exe -NoProfile -Command "Compress-Archive -Path '%DIST%\RPGMaker-source\*' -DestinationPath '%DIST%\RPGMaker-source.zip' -Force"
if errorlevel 1 goto failed

"%JDK_BIN%\jpackage.exe" --type exe --name RPGMaker --app-version 1.0.0 --vendor "María Fortuna" --description "Editor de juegos RPG en Java" --input "%INPUT%" --main-jar RPGMaker.jar --main-class com.rpgmaker.Main --dest "%DIST%" --win-per-user-install --win-menu --win-menu-group "RPG Maker" --win-shortcut
if errorlevel 1 (
    echo No se pudo crear el instalador. Verifica WiX Toolset 3.14.1 y el error anterior.
    exit /b 1
)

echo.
echo Instalador: "%DIST%\RPGMaker-1.0.0.exe"
echo Codigo fuente: "%DIST%\RPGMaker-source.zip"
exit /b 0

:copyPackage
if not exist "%ROOT%\%~2\%~1" mkdir "%ROOT%\%~2\%~1"
xcopy "%STAGE%\%~1\*.class" "%ROOT%\%~2\%~1" /Y /I >nul
if errorlevel 1 exit /b 1
exit /b 0

:failed
echo Error durante la compilacion o preparacion del instalador.
exit /b 1
