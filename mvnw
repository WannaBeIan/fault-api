#!/bin/sh
set -e
WRAPPER_DIR="$(cd "$(dirname "$0")" && pwd)/.mvn/wrapper"
JAVA_EXE="$(which java)"
if [ ! -x "$JAVA_EXE" ]; then
  echo "ERROR: JAVA_HOME is not set and no 'java' command found" >&2
  exit 1
fi
exec "$JAVA_EXE" -cp "$WRAPPER_DIR/maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain "$@"
