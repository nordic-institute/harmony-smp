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
    
 The build first build the *smp.war* (The war is here: smp-webapp/target/smp.war) and then embeds in to the spring-boot 
 executable jar: *smp-springboot-[VERSION]-exec.jar* (The example file path: smp-springboot/target/smp-springboot-5.0-SNAPSHOT-exec.jar)
 
## Start the application

The DomiSMP can be started with 3 steps. 
To startup the DomiSMP, first, the database must be initialized. The second step is to prepare the application.properties 
with database configuration for the DomiSMP spring-boot application. And the final step is to run the application.

 
### Prepare the DomiSMP database
For details on how to prepare the database, read the Admin Guide available at: 
[DomiSMP Release Page](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/SMP).
Please find bellow the *Linux OS* command line example script for creating the DomiSMP database on MysSQL.

To run the script check the following steps
 - check if you have the correct command line emulator (example uses the /bin/sh)
 - locally installed MySQL database.
 - clone (and checkout the right branch: eg. for development checkout development branch) of the DomiSMP. The repo contains the database DDL scripts.

Before executing the example script set the following variables:
 - PROJECT_HOME: The DomiSMP code / project home: exp.: /code/smp
 - DATABASE: the smp database schema 
 - DB_ADMIN: the mysql database root username
 - DB_ADMIN_PASSWORD: the mysql database root password
 - DB_USERNAME: the DomiSMP mysql database username
 - DB_PASSWORD: the DomiSMP mysql database username

*Explanation if the script:*
The script connect to mysql database using CLI tool 'mysql' and  deletes database/schema and user defined in variable [DATABASE] and [DB_USERNAME]. The the DomiSMP schema is generated from script 
[PROJECT_HOME]/smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb.ddl
and insert the init data from
[PROJECT_HOME]/smp-soapui-tests/src/test/resources/init-data/init-test-mysql-soapui.sql


```
#!/bin/sh
 
PROJECT_HOME=/cef/code/smp

DATABASE=smpdb
DB_ADMIN=root
DB_ADMIN_PASSWORD=root
DB_USERNAME=smp;
DB_PASSWORD=smp;

# recreate database 
echo "clean the database $DATABASE if exists "
mysql -h localhost -u $DB_ADMIN --password=$DB_ADMIN_PASSWORD -e "drop schema if exists $DATABASE;DROP USER IF EXISTS $DB_USERNAME;  create schema $DATABASE;alter database $DATABASE charset=utf8; create user $DB_USERNAME identified by '$DB_PASSWORD';grant all on $DATABASE.* to $DB_USERNAME;"

# create new database
echo "create database"
mysql -h localhost -u $DB_ADMIN --password=$DB_ADMIN_PASSWORD $DATABASE < "$PROJECT_HOME/smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb.ddl"
echo "init database for soapui tests"
mysql -h localhost -u $DB_ADMIN --password=$DB_ADMIN_PASSWORD $DATABASE < "$PROJECT_HOME/smp-soapui-tests/groovy/mysql-4.1_integration_test_data.sql"
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

NOTE: Only the "java property type" of the springboot properties format is supported (json or yaml types are not supported!)

Example of the springboot configuration: application.properties:
NOTE: Please update the properties to meet you local mysql installation configuration

```
# the tomcat server port
server.port=8084

# Database configuration
smp.jdbc.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect

# *********************************
#  Custom defined datasource
# *********************************
# mysql database example
smp.jdbc.driver=com.mysql.jdbc.Driver
smp.jdbc.url=jdbc:mysql://localhost:3306/smpdb
smp.jdbc.user=smp
smp.jdbc.password=smp
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
