# Import/export command line utility

A command line application for:
   * exporting clients from TARA-Server (CAS) database to Excel format file.
   * importing clients from file (Excel or json) to tara-admin database and hydra.

## Export clients from CAS to an Excel file

Execute the following command:
````
java -Dspring.profiles.active=exportFromCas \         
        -Dcas-export.db-url=jdbc:postgresql://cas-service-db:5432/cas \
        -Dcas-export.user=cas \
        -Dcas-export.password=cas \        
        -jar tara-admin-import/target/client-utils.jar
````

An Excel file with the name 'clients.xlsx' is written to the current directory when the export is successful.


## Importing from a file

Note that if the import file contains a client with a client_id that already exists, it will be removed and recreated.

Clients can be imported from:

1. Excel file.
    
    Must be a valid Excel file. Must have a fixed header of the following columns: Institution name, Institution registry code, Client ID, Redirect URI, Secret, Return URL (legacy), Client name (et), Client name (en), Client name (ru), Client shortname (et), Client shortname (en), Client shortname (ru), Contacts, Description

    For each excel row a check is made whether the Institution registry code exists in the system. If the institution is not present, a new institution will be created. 
   
2. JSON file
  
   Must contain a JSON array of #/components/schemas/Client objects as specified in the [swagger.yml](../tara-admin-api-schema/src/main/resources/swagger.yaml).
   
   All imported clients are imported as under a single institution (Example Institution by default). 


Use the following command to import from a file:

````
java -Dspring.profiles.active=importFromFile \ 
    -Dfile-import.file-name=clients.xslx \
    -Dtara-oidc.url=https://oidc-service-backend:4445 \
    -Dauth.tls-truststore-path=/etc/tara/secrets/tls/truststore.p12 \
    -Dauth.tls-truststore-password=changeit \ 
    -Dspring.datasource.url=jdbc:postgresql://admin-service-db:5432/admin-service-db \
    -Dspring.datasource.username=taraadmin \
    -Dspring.datasource.password=changeme \    
    -jar tara-admin-import/target/client-utils.jar
````

Upon finishing, a JSON report is printed to the console, that includes the following information:

| JSON element in report | Description |
| :--------------------- | :---------- |
| `status` | The result of the import process. <p>One of the following values: <br>FINISHED_SUCCESSFULLY - all clients that were imported from the input file, <br>FINISHED_WITH_ERRORS - one or more clients was not imported</p> |
| `clientsFound` | The count of clients that read from the input file. |
| `clientsMigrateSuccessful` | The count of clients that were successfully imported. |
| `clientsMigrateFailed` | A count of clients that coult not be imported. |
| `clientsNotMigrated`| A list of client_id's of the clients that could not be imported. | 


Sample output: 

````


  ______ _____ _   _ _____  _____ _    _ ______ _____
 |  ____|_   _| \ | |_   _|/ ____| |  | |  ____|  __ \
 | |__    | | |  \| | | | | (___ | |__| | |__  | |  | |
 |  __|   | | | . ` | | |  \___ \|  __  |  __| | |  | |
 | |     _| |_| |\  |_| |_ ____) | |  | | |____| |__| |
 |_|    |_____|_| \_|_____|_____/|_|  |_|______|_____/


{
  "status" : "FINISHED_SUCCESSFULLY",
  "clientsFound" : 2,
  "clientsMigrateSuccessful" : 2,
  "clientsMigrateFailed" : 0,
  "clientsNotMigrated" : "[]"
}
````