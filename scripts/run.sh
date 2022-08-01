#!/usr/bin/env bash

exec /app/jre/bin/java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom org.springframework.boot.loader.JarLauncher
