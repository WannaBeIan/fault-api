@ECHO OFF
SETLOCAL
SET WRAPPER_DIR=%~dp0.mvn\wrapper
IF EXIST "%WRAPPER_DIR%\maven-wrapper.jar" (
  "%JAVA_HOME%\bin\java.exe" -cp "%WRAPPER_DIR%\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
) ELSE (
  ECHO "ERROR: Maven Wrapper JAR not found"
  EXIT /B 1
)
ENDLOCAL
