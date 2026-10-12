# HDH-CDR-API

## Description

K-HEALTHinAIR Clinical Data Repository API, provides a model mapping and HAPI-FHIR wrapper.

Important NOTE, since internal IDs should not be informed to specific clients outside the EdgeNode,
such controllers will not return directly the results of the service, that they do keep returning this information as outcomes of their operations.

Naming Conventions

- create, create resource instance for save or aggregation
- save, means into the CDR (clinical Data Repository)

## Configurations

The configuration is defined in the application.yaml. There are several environment variables that can be adjusted from outside, for example, through Docker or the docker-compose env file.

 - CDR_API_PORT, which defaults to '8081', port for the SpringBoot embedded server
 - SERVER_SERVLET_CONTEXTPATH, which defaults to '/hdh-cdr-api', defines the context path to publish application
 - FHIR_ENDPOINT_URL, the REST endpoint where it will be configured the HAPI-FHIR API
 - CDR_LOGGING_MODE, which defaults to 'dev'
 
```
server:
  port: ${CDR_API_PORT:8081}
  servlet:
    context-path: ${SERVER_SERVLET_CONTEXTPATH:/hdh-cdr-api}

spring:
  profiles:
    active: ${CDR_LOGGING_MODE:dev}
  jackson:
    serialization:
      INDENT_OUTPUT: true
    
fhir:
  endpoint: ${FHIR_ENDPOINT_URL:http://localhost:8080/hdh-cdr/fhir}
```

- [Log configuration](src/main/resources/logback-spring.xml)


## API_Keys

The following API-Keys has been defined and should be provided

 - CDR_API_APIKEY_ADMIN, with access to the Admin grouping endpoints
 - CDR_API_APIKEY_PILOT, with access to the Import and Export grouping endpoints

## Prerequisites

In order to run this component, you should have this project code checked out from this public repository (https://github.com/Bull-RD-Spain/khealthinair-hdh).

The project is a Java-Maven one (pom.xml), and the application based in Spring Boot with an application.yaml configuration file.

### JDK + Maven

 - Java (OpenJDK) installed: Minimum JDK11 or newer.
 - Apache Maven build tool (newest version)


### Docker

A Docker image can be generated through the provided Dockerfile, used also by the CI/CD in both cases GitHub and GitLab.


## Running locally

Running with Spring Boot

```bash
mvn clean spring-boot:run
```
After application started, by default, navigate to <http://localhost:8081/hdh-cdr-api/swagger-ui.html>


## License

Apache 2.0