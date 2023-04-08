# Spring-boot SMP application

[![License badge](https://img.shields.io/badge/license-EUPL-blue.svg)](https://ec.europa.eu/digital-building-blocks/wikis/download/attachments/52601883/eupl_v1.2_en%20.pdf?version=1&modificationDate=1507206778126&api=v2)
[![Documentation badge](https://img.shields.io/badge/docs-latest-brightgreen.svg)](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP)
[![Support badge]( https://img.shields.io/badge/support-sof-yellowgreen.svg)](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/Support+eDelivery)

## NOTE

Springboot artefact is intended for **test and demo purposes only**. 

The Springboot artefact use the embedded Tomcat server and may not contain the latest security updates! 
Please see the maven project for the Tomcat server version!
		  
## Introduction

The purpose of the submodule is to build a spring-boot application for starting the DomiSMP application on the embedded 
Tomcat webserver. The goal of the spring-boot startup of DomiSMP application
is to make it easier to start the DomiSMP for the demo and testing. 

## Build

In order to build DomiSMP spring-boot build the bdmls project from the root of the project:

    mvn clean install 
    
 The build first build the smp-webapp.war which is embedded in to the spring-boot 
 executable jar.
 
## Start the application

The DomiSMP can be started with 3 steps. 
To startup the DomiSMP, first, the database must be initialized. The second step is to prepare the application.properties 
with database configuration for the DomiSMP spring-boot application. And the final step is to run the application.

 
### Prepare the DomiSMP database
For details on how to prepare the database, read the Admin Guide available at: 
[DomiSMP Release Page](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP).
Please find bellow the bash example bash script for creating the DomiSMP database on MysSQL.

```
#!/bin/sh
 
PROJECT_HOME=[The DomiSMP project home: exp.: /code/smp]
SQLFOLDER=$PROJECT_HOME/smp-webapp/src/main/sml-setup/database-scripts/

DATABASE=smldbdtest
DB_ADMIN=root
DB_ADMIN_PASSWORD=root
DB_USERNAME=smltest;
DB_PASSWORD=smltest;

# recreate database
echo "clean the database"
mysql -h localhost -u $DB_ADMIN_PASSWORD --password=$DB_ADMIN_PASSWORD -e "drop schema if exists $DATABASE;DROP USER IF EXISTS $DB_USERNAME;  create schema $DATABASE;alter database $DATABASE charset=utf8; create user $DB_USERNAME identified by '$DB_PASSWORD';grant all on $DATABASE.* to $DB_USERNAME;"

# create new database
echo "create database"
mysql -h localhost -u root --password=root $DATABASE < "$SQLFOLDER/mysql5innodb.ddl"
echo "init database for soapui tests"
mysql -h localhost -u root --password=root $DATABASE < "$PROJECT_HOME/smp-soapui-tests/src/test/resources/init-data/init-test-mysql-soapui.sql"
```

### Prepare the DomiSMP database configuration.

To set the  DomiSMP database configuration, the following properties must be set.
 - smp.jdbc.hibernate.dialect: the database hibernate dialect name
 - jdbc.driver: the jdbc driver (The MySQL driver is embedded by default. To add other drivers should be added to the 
 pom.xml and rebuild the spring-boot application. )
 - smp.jdbc.url: the Url of the database
 - smp.jdbc.user: the database username
 - smp.jdbc.password: the database password. 
 
To set/change other spring-boot parameters (as example the server port: server.port) please read the spring-boot documentation.
The configuration properties must be set in the file "application.properties" and placed in the working directory of the DomiSMP 
spring-boot application. For alternatives on how to set spring-boot properties please read the spring-boot documentation; for 
the DomiSMP startup properties, please read the DomiSMP Admin guide.


Example of the springboot configuration: application.properties:

```
# the tomcat server port
server.port=8084

# Database configuration
smp.jdbc.hibernate.dialect=org.hibernate.dialect.MySQLDialect
smp.jdbc.driver=com.mysql.cj.jdbc.Driver
smp.jdbc.url=jdbc:mysql://localhost:3306/smldbdtest?allowPublicKeyRetrieval=true
smp.jdbc.user=smltest
smp.jdbc.password=smltest
```

### Start the application.

with maven the application can be started as

    mvn spring-boot:run
    
with java

     java -jar target/smp-springboot-5.0-SNAPSHOT-exec.jar

with java adding libraries in the subfolder ./libs/ for example to add JDBC drivers
    
    java -cp ./smp-springboot-exec-5.0-SNAPSHOT.jar:./libs/* org.springframework.boot.loader.JarLauncher     
     
if the spring-boot SMP application was started with the server.port=8084 the application]
is accessible on url: http://localhost:8084/edelivery-sml/
