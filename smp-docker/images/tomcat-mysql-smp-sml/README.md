# Experiamental SMP docker image
Purpose of image is to help SMP and AP sofware developers to create development environment for localy testing Dynamic Discovery using SML and SMP.
Image uses latest version of eDelivery SMP setup on tomcat, mysql ubuntu

# Image build

    docker build -t smp .

# Run container based on smp image
  
    docker run --name smp -it --rm -p [http-port]:8080  edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/smp-sml-tomcat-mysql

example:

    docker run --name smp --rm -it -p 8080:8080  edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/smp-sml-tomcat-mysql:4.2-SNAPSHOT

## SMP (param: -p 8080:8080 )
url: http://localhost:8080/smp

## MYSQL (param: -p 3306:3306)
Database client connection (for testing and debugging )
url: jdbc:mysql://localhost:3306/smp
Username: smp
Password: smp

## Volume (-v /opt/docker-data/smp:/data)
Mysql database files and tomcat configuration (and logs) can be externalized for experimenting with different SMP settings.

    docker run --name smp --rm -it -p 8080:8080  -v /opt/docker-data/smp:/data edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/smp-sml-tomcat-mysql:4.2-SNAPSHOT

# Start docker with pre-init data  
1. copy init sql script to folder (create folder)
  
    ./db-scripts

example:
        
    curl -k https://ec.europa.eu/digital-building-blocks/code/projects/EDELIVERY/repos/smp/raw/smp-soapui-tests/groovy/mysql-4.1_integration_test_data.sql?at=refs%2Fheads%2Fdevelopment --output ./db-scripts/mysql5innodb-data.sql

Then start the docker as:

    docker run --name smp --rm -it -p 8080:8080  -v - db-scripts:/tmp/custom-data/ edelivery-docker.devops.tech.ec.europa.eu/edeliverytest/smp-sml-tomcat-mysql:4.2-SNAPSHOT 


