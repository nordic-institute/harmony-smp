# Service Metadata Publishing

## Continous Integration

[https://webgate.ec.europa.eu/CITnet/bamboo/browse/EDELIVERY-SMPDEV]

## Building SMP
SMP requires Maven 3.0 and Java 1.7. 

Integration tests included into build process require access to DB. By default it is a local instance of MySQL with preconfigured schema:   
[smp-server-library/database]

Any remote DB with preconfigured schema might be used as well. Sample build command:

    mvn clean install \
    -Djdbc.driver=oracle.jdbc.OracleDriver \
    -Djdbc.url=jdbc:oracle:thin:<HOST_AND_PORT_AND_SERVICENAME> \
    -Djdbc.user=<USERNAME> \
    -Djdbc.password=<PASSWORD> \ 
    -Dtarget-database=Oracle \ 
    -Djdbc.read-connections.max=10
    
## Source code history
This is a continuation of CIPA SMP Joinup repository, which was migrated here to GIT on 07.12.2016:
[https://joinup.ec.europa.eu/svn/cipaedelivery/trunk]

The untouched version of last SVN Joinup state stays here: 
[https://ec.europa.eu/cefdigital/code/projects/EDELIVERY/repos/smp/browse?at=refs/heads/svn_joinup_trunk]

