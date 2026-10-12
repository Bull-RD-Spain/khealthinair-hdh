# HDH-CDR, https://hub.docker.com/r/hapiproject/hapi/tags
# podman build --no-cache --progress=plain --format docker -t khealthinair/hdh-cdr:latest .

# -------------------------------
# Build stage
# -------------------------------
FROM maven:3.8.7-openjdk-18-slim AS build-hapi
RUN apt-get update && \
    apt-get install -y --no-install-recommends git && \
    git clone --depth 1 --branch image/v7.6.0 https://github.com/hapifhir/hapi-fhir-jpaserver-starter.git && \
    apt-get purge -y --auto-remove git && \
    rm -rf /var/lib/apt/lists/*
COPY --chown=root:root ./src/main/resources/application7.6.0.yaml /hapi-fhir-jpaserver-starter/src/main/resources/application.yaml
COPY --chown=root:root ./src/main/webapp/ /hapi-fhir-jpaserver-starter/src/main/webapp/
COPY settings.xml /root/.m2/settings.xml
RUN chmod 644 /hapi-fhir-jpaserver-starter/src/main/resources/application.yaml
WORKDIR /hapi-fhir-jpaserver-starter
RUN mvn -ntp dependency:go-offline && \
    mvn clean package spring-boot:repackage -Pboot -DskipTests -Djdk.lang.Process.launchMechanism=vfork -Derrorprone.skip=true

# -------------------------------
# Execution stage minimal JRE
# -------------------------------
FROM alpine:latest AS packager
LABEL version="7.6.0" \
      maintainer="K-HEALTHinAIR HDH Team" \
      description="HDH-CDR Based on HAPI FHIR JPA Server Starter"
RUN apk upgrade --no-cache && \
    apk add --no-cache openjdk17-jre-headless curl busybox
COPY --from=build-hapi --chown=1001:1001 /hapi-fhir-jpaserver-starter/target/ROOT.war /app/hdh-cdr.war
#HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 CMD curl -f http://hdh-cdr-svc:8080/hdh-cdr/actuator/health || echo "unhealthy" && exit 1
EXPOSE 8080
CMD ["java", "-jar", "/app/hdh-cdr.war"]
