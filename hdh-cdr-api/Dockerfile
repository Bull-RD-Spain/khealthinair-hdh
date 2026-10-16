# Copyright © 2021-2026 Bull SAS
# SPDX-License-Identifier: GPL-3.0-or-later
#
# This file is part of work done in the K-HEALTHinAIR EU funded project.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

# HDH-CDR-API, SpringBoot OpenAPI for K-HEALTHinAIR
# podman build --no-cache --progress=plain --format docker -t khealthinair/hdh-cdr-api:latest .

# -------------------------------
# Build stage
# -------------------------------
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
ARG NEXUS_USERNAME=${NEXUS_USERNAME:-""}
ARG NEXUS_PASSWORD=${NEXUS_PASSWORD:-""}
ARG MAVEN_OPTS=${MAVEN_OPTS:-""}
ARG MAVEN_ARGS=${MAVEN_ARGS:-""}
# MAVEN_ARGS from Maven 3.9, here replaced in RUN
ARG CI_JOB_TOKEN=${CI_JOB_TOKEN:-""}
COPY src /src
COPY pom.xml .
COPY settings.xml /root/.m2/settings.xml
RUN mvn --batch-mode --no-transfer-progress -f pom.xml help:active-profiles clean package

# -------------------------------
# Execution stage minimal JRE
# -------------------------------
FROM alpine:latest AS packager
LABEL version="1.0.0" \
      maintainer="K-HEALTHinAIR HDH Team" \
      description="HDH-CDR-API"
RUN apk --no-cache add openjdk17-jdk openjdk17-jmods curl binutils
ENV JAVA_HOME="/opt/java-minimal"
ENV PATH="$PATH:$JAVA_HOME/bin"
RUN /usr/lib/jvm/java-17-openjdk/bin/jlink --verbose \
--add-modules java.base,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument \
--compress 2 --strip-debug --no-header-files --no-man-pages \
--release-info="add:IMPLEMENTOR=EVIDEN:IMPLEMENTOR_VERSION=OpenJDK17" --output "$JAVA_HOME"
RUN mkdir -p /var/app
COPY --from=builder /target/hdh-cdr-api*.jar /var/app/hdh-cdr-api.jar
#HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 CMD curl -f http://localhost:8081/hdh-cdr-api/actuator/health || echo "unhealthy" && exit 1
EXPOSE 8081
ENTRYPOINT ["java","-jar","var/app/hdh-cdr-api.jar"]
