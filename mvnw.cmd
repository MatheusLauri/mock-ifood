@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script, Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MVNW_REPOURL=https://repo.maven.apache.org/maven2
set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar

if not exist ".mvn\wrapper" (
  mkdir ".mvn\wrapper" >NUL 2>&1
)

if not exist "%WRAPPER_JAR%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$p='%WRAPPER_JAR%'; $u='%MVNW_REPOURL%/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar';" ^
    "Write-Host 'Downloading Maven Wrapper...';" ^
    "Invoke-WebRequest -UseBasicParsing -Uri $u -OutFile $p"
)

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set JAVA_EXE=java

if "%JAVA_HOME%"=="" goto run
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

:run
"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*

endlocal
