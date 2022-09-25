# TARA/GovSSO admin web application API

## API specification 

The API specification in OpenAPI format can be found [here](src/main/resources/swagger.yaml).

## Generate/update server classes from API specification
In case of an api change (swagger.yml file change), one needs to regenerate server-stub classes.

To invoke regeneration, issue:
```
./mvnw clean install -P regenerate-swagger
```
