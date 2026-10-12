# K-HEALTHinAIR HDH-CDR

This is a HAPI-FHIR JPA based implementation project to be used in the K-HEALTHinAIR project (https://www.khealthinair.eu) as HL7 FHIR Clinical Data Repository, part of the Health Data Hub.

Note that this project is specifically intended for end users of the HAPI FHIR JPA server module (in other words, it helps you implement HAPI FHIR, it is not the source of the library itself). If you are looking for the main HAPI FHIR project, see here: https://github.com/hapifhir/hapi-fhir

This set up is based on the [Docker Hub](https://hub.docker.com/r/hapiproject/hapi) tagged/released version of `hapi-fhir-jpaserver` is built as a Docker image and published.

Using it as base, some modifications are done in the hdh-cdr project Dockerfile, to customice into the scope of the K-HEALTHinAIR project.

## Requirements and dependencies

The hdh-cdr component in turn depend on a functional running database to connect to.

## Configurations

The default configuration is expecting an PostgreSQL DB (Charset:utf8mb4, collation:utf8mb4_bin), with the following arguments (defined by default in the application.yaml):
```
CREATE USER hdh_cdr WITH ENCRYPTED PASSWORD '?????????????';  
CREATE DATABASE hdh_cdr WITH OWNER=hdh_cdr ENCODING='UTF8' LC_COLLATE='C' LC_CTYPE='C';
GRANT ALL PRIVILEGES ON DATABASE hdh_cdr TO hdh_cdr;
GRANT ALL PRIVILEGES ON SCHEMA public TO hdh_cdr;
```

The CDR container expects also the following enviromental variables and values by default:
```
####### HDH-CDR-DB #######
POSTGRES_PASSWORD=
POSTGRES_USER=hdh_cdr
POSTGRES_DB=hdh_cdr

####### HDH-CDR #######
CDR_LOGGING_MODE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://hdh-cdr-db:5432/hdh_cdr?ssl=false&TimeZone=UTC
SPRING_DATASOURCE_USERNAME=hdh_cdr
SPRING_DATASOURCE_PASSWORD=
SERVER_SERVLET_CONTEXT_PATH=/hdh-cdr/
CDR_HAPI_FHIR_SERVER_ADDRESS=http://localhost:8080/hdh-cdr # the REST endpoint where it will be configured the HAPI-FHIR (base and context path, without /fhir)
```
(This are also the environment variables that can be adjusted via docker-compose file or .env file)

## Test page template

The HAPI-FHIR prjects provides a 'test page' where to visualize the server contents and assist in queriying.

This UI can be customized based on a templating system, so the generation of the HDH-CDR docker image includes a set of them to override the default ones provided.

 - about.html: overrides the content of the about page accessible from the upper right link "About This Server".
 - tmpl-banner.html: overrides the project logo (khealthinair_logo.png) which hapears in the Home page.
 - tmpl-footer.html: overrides the default footer.
 - tmpl-head.html: overrides the default favicon by the one under img/favicon.ico.
 - tmpl-home-welcome.html: overrides the Home text.
 - window-title.html: overrides the title in the 'home', 'resource' and 'result' pages (shown in the browser tab).
