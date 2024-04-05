# Test and Domo SMP docker image

The Image is intended for internal testing of the DomiSMP snapshots builds. The images should not
be used in production environment. 
The image is shipped with jdk 8 and 11. By default the jdk 11 is used, but it can be changed by setting the environment 
variable JDK_VERSION=8 to start the container with JAVA_HOME pointing to jdk 8.

NOTE : that the jdk 11 JDPA_ADDRESS is by default "*:5005". To make it work with jdk 8, the JDPA_ADDRESS should be changed to "5005".

# Image build

    docker build -t domismp-tomcat-mysql .

# Run container based on smp image

    docker run --name smp -p 8080:8080 domismp-tomcat-mysql

    docker run --name smp -it --rm -p [http-port]:8080  edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/domismp-tomcat-mysql:${SMP_VERSION}

example:

    docker run --name smp --rm -it -p 8180:8080 -p 3316:3306 -e JDK_VERSION=8 -e JDPA_ADDRESS=5005 edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/domismp-tomcat-mysql:5.0-SNAPSHOT

## SMP (param: -p 8180:8080 )
url: http://localhost:8180/smp

Default (demo) users are:
 - System admin: system/123456 
 - User:  user/123456

NOTE: The users are configured just for the demonstration purposes. Please change users and its passwords at first login!

## MYSQL (param: -p 3306:3306)
Database client connection (for testing and debugging )
url: jdbc:mysql://localhost:3306/smp
Username: smp
Password: smp

## Volume (-v /opt/docker-data/smp:/data)
Mysql database files and tomcat configuration (and logs) can be externalized for experimenting with different SMP settings.

    docker run --name smp --rm -it -p 8180:8080  -v /opt/docker-data/smp:/data edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/domismp-tomcat-mysql:5.0-SNAPSHOT

# Start docker with pre-init data  
1. copy init sql script to folder (create folder)
  
    ./db-scripts

NOTE: Make sure the script name is *mysql5innodb-data.sql*


example:
        
    curl -k https://ec.europa.eu/digital-building-blocks/code/projects/EDELIVERY/repos/smp/raw/smp-soapui-tests/groovy/mysql-4.1_integration_test_data.sql?at=refs%2Fheads%2Fdevelopment --output ./db-scripts/mysql5innodb-data.sql

Then start the docker as:

    docker run --name smp --rm -it -p 8180:8080  -v ./db-scripts:/tmp/custom-data/ edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/domismp-tomcat-mysql:5.0-SNAPSHOT 

# Start with the docker compose file

Create a docker compose file: docker-compose.yml
with the following content: 
```
version: '3.8'
services:
  tomcat-mysql-sml:
    image: edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/domismp-tomcat-mysql:5.0-SNAPSHOT
    ports:
      - "3316:3306"
      - "8180:8080"
#    volumes:
#      - ./db-scripts:/tmp/custom-data/      
```

and start the container service

      docker compose up 
