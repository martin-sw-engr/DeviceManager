# DeviceManager

### To build the application
From terminal commandline, type

`mvn compile`

### To run the application
From terminal commandline, type

`mvn spring-boot:run`

### Changes made to the code
1. Used Spring Boot dependencies in `pom.xml` to create a SpringBoot app
2. Used lombok and JPA annotations with H2 database
3. Used `CommandLineRunner` to initialize the DB
4. Implemented REST API Controller 
5. Added `GlobalExceptionHandler` 

### TODO
1. Use a standard RDBMS other than H2 db.
2. More controller level tests need to be added.
3. Conditional updates using version-based eTag header can be added.
4. Controller endpoints can be made secure by using auth schemes such as OAuth.
5. input validation can be done in the controller layer

## Curl commands
### Displaying all devices for sale 
` curl http://localhost:8080/devices/forSale`

### Displaying all devices waiting for activation
` curl http://localhost:8080/devices/waitingForActivation`

### Removing a device configuration
`curl -X PATCH http://localhost:8080/devices/removeConfig/4`

### Updating a device configuration
`curl -X PATCH -d '{"deviceStatus":"READY","sim_id":"S3", "sim_status" : "BLOCKED", "country_code" : "IN", "operator_code":"Sprint","operator_name":"Sprint Inc"}' -H 'Content-Type:application/json' http://localhost:8080/devices/updateConfig/5`
