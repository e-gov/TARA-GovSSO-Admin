<img src="doc/img/eu_regional_development_fund_horizontal.jpg" width="350" height="200">

# TARA admin application

TARA admin project's main goal is to provide a web application that allows TARA service managers to:
* manage institutions that want to register OIDC clients in TARA
* manage OIDC client registrations
* manage alerts that are to be displayed on the TARA login page

An additional subproject also provides tools to export/import TARA clients. 


## Requirements:

Java (JDK 11+) runtime and Node.js (14+) is required to build and run the webapp.

[Maven](https://maven.apache.org/) is used to build and test the software.

## Building the project

Execute the following command in the projects root directory to build the TARA admin webapp and additional command line tools:

```shell
./mvnw clean package
```

This will produce the war package for the webapp at `tara-admin-api/target/*.war` which can then be deployed to a standalone Tomcat web server. See further details [here](tara-admin-api/README.md)

In addition, the executable jar file can be found at `tara-admin-import/target/client-utils.jar` which can be used to export/import OIDC clients from the command line. See further details [here](tara-admin-import/README.md)

## Building and Running in Docker

TODO Disable file logging, it causes error when running in Docker. Current workaround is to delete file appender from
logback-spring.xml before building.

```shell
./mvnw clean install
./mvnw --projects tara-admin-api -DskipTests spring-boot:build-image
docker run --rm -p 16080:8080 tara-admin:latest
```
