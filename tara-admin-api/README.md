# TARA/GOVSSO admin web application

- [Overview](#overview)
- [Configuration parameters](#configuration)
    * [Integration with TARA/GOVSSO OIDC service](#hydra_integration_conf)
    * [Trusted TLS certificates](#tls_conf)
    * [User authentication with LDAP](#auth_conf)
    * [Database](#db_conf)  
    * [Database schema initialization and migration](#db_conf_updates)
    * [Mail server client](#smtp_conf)
    * [Email content for issuing secrets](#email_conf)
    * [Logging](#logging)
    * [Health endpoint](#health)
- [APPENDIX](#appendix)
    * [API specification](#api_spec)


## Overview

The TARA/GOVSSO admin web application is built on top of Spring Boot backend service with an Angular frontend application which is packaged to a single deployable Java war file.

The frontend application source code is provided in the ´tara-admin-ui´ subproject. See details [here](../tara-admin-ui/README.md).

The backend application is built on top of classes generated from the API specification. For details [here](../tara-admin-api-schema/README.md). 

<a href="configuration"></a>
## Configuration parameters

<a href="oidc_conf"></a>
### Application mode
| Parameter                    | Required | Description                       | Example                                              |
| ---------------------------- | -------- | --------------------------------- | ---------------------------------------------------- |
| tara.admin.sso-mode           | N        | Toggle between GOVSSO mode when true and TARA mode when false, false by default  | false                                                 |

<a href="oidc_conf"></a>
### Integration with TARA/GOVSSO OIDC service
| Parameter                    | Required | Description                       | Example                                              |
| ---------------------------- | -------- | --------------------------------- | ---------------------------------------------------- |
| tara-oidc.url                | Y        | TARA/GOVSSO OIDC service's admin url     | https://example.com:4445              |


<a href="tls_conf"></a>
### Trusted TLS certificates

| Parameter                    | Required | Description                       | Example                                              |
| ---------------------------- | -------- | --------------------------------- | ---------------------------------------------------- |
| auth.tls-truststore-path     | Y        | TLS truststore path used for OIDC | tara-admin-api/src/main/resources/tls-truststore.p12 |
| auth.tls-truststore-password | Y        | TLS truststore password           | changeit                                             |

<a href="auth_conf"></a>
### User authentication with LDAP
 
| Parameter                       | Required | Description                                              | Example                |
| ------------------------------- | -------- | -------------------------------------------------------- | ---------------------- |
| auth.ldap-domain                | Y        | Domain used for LDAP authentication                      | example.com            |
| spring.ldap.urls                | Y        | URL used for LDAP authentication                         | ldap://dc.example.com  |
| auth.ldap-authority             | Y        | Authority that should grant access to the application    | authority              |
| auth.in-memory-username         | N*       | Username used with in-memory authentication              | admin                  |
| auth.in-memory-password         | N*       | Password used for in-memory authentication               | admin                  |
| auth.in-memory-authority        | N*       | Authority given by in-memory authentication              | admin-authority        |
\* - Only used when inMemoryAuth profile is active

<a href="db_conf"></a>
### Database
| Parameter                       | Required | Description                                              | Example                                   |
| ------------------------------- | -------- | -------------------------------------------------------- | ----------------------------------------- |
| spring.datasource.url           | Y        | Database url                                             | jdbc:postgresql://localhost:5432/postgres |
| spring.datasource.username      | Y        | Database user name                                       | postgres                                  |
| spring.datasource.password      | Y        | Database user password                                   | postgres                                  |


<a href="db_conf_updates"></a>
### Database schema initialization and migration

| Parameter                       | Required | Description                                              | Example                                   |
| ------------------------------- | -------- | -------------------------------------------------------- | ----------------------------------------- |
| spring.liquibase.enabled | N | Whether or not the database updates should be applied during startup. Valid values: true,false | false |
| spring.liquibase.change-log | N | Changelog that describes a list of changesets to be applied. | `classpath:db/changelog/db.changelog-master.yaml` | 
| spring.liquibase.user | N | Database user to execute liquibase scripts (must have schema and data manipulation rights in the database). | `admin-user`  | 
| spring.liquibase.password | N | Database user password. | `changeme`  |
| spring.liquibase.parameters.admin-service-user-name | N | Postgres database user that is used by the admin service. Must be same as the value supplied in `spring.datasource.username`  | `service-user` |


<a href="smtp_conf"></a>
### Mail server client

| Parameter                                            | Required | Description                                              | Example          |
| ---------------------------------------------------- | -------- | -------------------------------------------------------- | ---------------- |
| spring.mail.host                                     | Y        | Mail server url                                          | smtp.gmail.com   |
| spring.mail.port                                     | Y        | Mail server port                                         | 587              |
| spring.mail.username                                 | Y        | Username of the email used to send email containing cdoc | username         |
| spring.mail.password                                 | Y        | Password of the email used to send email containing cdoc | password         |
| spring.mail.properties.mail.smtp.auth                | Y        | Require authentication                                   | true             |
| spring.mail.properties.mail.smtp.starttls.enable     | Y        | Start TLS                                                | true             |
| spring.mail.properties.mail.smtp.connectiontimeout   | Y        | Timeout of connection                                    | 5000             |
| spring.mail.properties.mail.smtp.timeout             | Y        | Timeout                                                  | 3000             |
| spring.mail.properties.mail.smtp.writetimeout        | Y        | Timeout of email sending                                 | 5000             |

<a href="email_conf"></a>
### Email content

The OIDC user credentials (client id and secret) are issued to the relying party's contact person via email which contains an encrypted txt file (in CDOC container) as an attachment. 

The email content and metadata can be configured using the following configuration parameters: 

| Parameter                                 | Required | Description                       | Example                                             |
| ----------------------------------------- | -------- | --------------------------------- | --------------------------------------------------- |
| tara.admin.email.from-email               | Y        | Email sender name                 | help@example.com                                         |
| tara.admin.email.template-name            | Y        | Email template file name          | client-secret-email-template.ftl                   |
| tara.admin.email.from-name                | Y        | Email name                        | Autentimisteenus                                |
| tara.admin.email.subject                  | Y        | Email subject                     | Autentimisteenuse kasutamiseks vajalikud andmed |
| tara.admin.email.attachment-file-name     | Y        | Email attachment file name        | parooliymbrik.cdoc                                  |
| tara.admin.data-file.encrypted-file-name  | Y        | Encrypted file name in cdoc       | parooliymbrik.txt                                   |
| tara.admin.data-file.text-file-name       | Y        | Client secret text file template  | client-secret-txt-template.ftl                     |


<a href="logging"></a>
### Logging

Logback is used for logging. A default logging configuration file is embedded within the webapp war file by default. 

The default log config can be overridden using following environment variables:

| Environment variable        | Mandatory | Description, example |
| :---------------- | :---------- | :----------------|
| `LOG_HOME` | No | Log files path. Default value Java IO temp dir (java.io.tmpdir) or `/tmp` |
| `LOG_FILES_MAX_COUNT` | No | Rolling file appender max files history. Default value `31` |
| `LOG_FILE_LEVEL` | No | Log level for file logging. Default value `OFF` |
| `LOG_CONSOLE_PATTERN` | No | Log files path. Default value `%d{yyyy-MM-dd'T'HH:mm:ss.SSS'Z',GMT} [${springAppName}] [%15.15t] %highlight(%-5level) %-40.40logger{39} %green(%marker) [%X{trace.id},%X{transaction.id}] -%X{remoteHost} -%msg%n}` |
| `LOG_CONSOLE_LEVEL` | No | Log files path. Default value `INFO` |

Application logs:

````
${LOG_HOME}/TaraAdmin.%d{yyyy-MM-dd,GMT}.log
````

<a href="health"></a>
### Health endpoint

The webapp uses `Spring Boot Actuator` to enable endpoints for monitoring support. To customize Monitoring, Metrics, Auditing, and more see [Spring Boot Actuator documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready).

Sample configuration: 

````
management.endpoints.web.cors.allowed-origins=https://localhost:8080
management.endpoints.web.cors.allowed-methods=GET,POST

management.endpoint.health.show-details=always

management.endpoint.health.status.http-mapping.down=503
management.endpoint.health.status.http-mapping.fatal=503
management.endpoint.health.status.http-mapping.out-of-service=503
````

Health endpoint is accessible at the following URL:
````
https://${api-path}/actuator/health
````

<a href="appendix"></a>
## APPENDIX

<a href="api_spec"></a>
### API specification

API specification in OpenAPI format can be found [here](../tara-admin-api-schema/src/main/resources/swagger.yaml).
