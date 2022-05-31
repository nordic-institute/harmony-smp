# Service Metadata Publishing

## Continuous Integration

[https://webgate.ec.europa.eu/CITnet/bamboo/browse/EDELIVERY-SMPDEV]

## Building SMP
SMP requires Maven 3.6+ and Java 1.8. 

    
## Source code history
This is a continuation of CIPA SMP Joinup repository, which was migrated here to GIT on 07.12.2016:
[https://joinup.ec.europa.eu/svn/cipaedelivery/trunk]

##  Build SMP
Step 1:

mvn clean install 


## Execute integartion tests
By default integrations tests are executes on H2 database.  
Any remote DB with preconfigured schema might be used as well. Sample build command:

    mvn clean install \
    -Djdbc.driver=oracle.jdbc.OracleDriver \
    -Djdbc.url=jdbc:oracle:thin:<HOST_AND_PORT_AND_SERVICENAME> \
    -Djdbc.user=<USERNAME> \
    -Djdbc.password=<PASSWORD> \ 
    -Dtarget-database=Oracle \ 
    -Djdbc.read-connections.max=10