# Experiamental SMP docker image
Purpose of image is to help SMP and AP sofware developers to create development environment for localy testing Dynamic Discovery using SML and SMP.
Image uses latest version of eDelivery SMP setup on tomcat, mysql ubuntu

# Image build

docker build -t smp .

# Run container based on smp image
docker run --name smp -it --rm -p [http-port]:8080 -v [local volume]:/data smp
example:
docker run --name smp --rm -it -p 8080:8080 -v  /opt/dockerdata/smp:/data smp smp

## SMP (param: -p 8080:8080 )
url: http://localhost:8080/smp

## MYSQL (param: -p 3306:3306)
Database client connection (for testing and debugging )
url: jdbc:mysql://localhost:3306/smp
Username: smp
Password:  smp

## Volume (-v /opt/dockerdata/sml:/data)
Mysql database files and tomcat configuration (and logs) can be externalized for experimenting with different SMP settings.




