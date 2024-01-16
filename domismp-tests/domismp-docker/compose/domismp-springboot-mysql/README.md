# Experimental SMP docker image with springboot 

Purpose of compose plan is to startup fully functional SMP environment for demo and testing. The plan contains MySql database
JDK8, it has disabled SML integration
 
# Run environment
docker-compose -f docker-compose.yml up -d 


## SMP 
url: http://localhost:8282/smp/

### MYSQL 
Database client connection (for testing and debugging )
url: jdbc:mysql://localhost:3208/smp
Username: smp
Password: smp

### Volume (-v /opt/dockerdata/sml:/data)
Mysql database files and tomcat configuration (and logs) can be externalized for experimenting with different SMP settings.

## Mail server
Mock mail server for monitoring send alert mails. 
url: http://localhost:9005/monitor 
    
