# TARA admin web application API

## API specification 

The API specification in OpenAPI format can be found [here](src/main/resources/swagger.yaml).

## Generate/update server classes from API specification
In case of an api change (swagger.yml file change), one needs to regenerate server-stub classes.
By default, only service api and config packages are regenerated. To include the model package in the update, one needs to edit the `.swagger-codegen-ignore` file first and remove the model package from the ignore list.

To invoke regeneration, issue:
```
./mvnw clean install -P regenerate-swagger
```
