# WebLogic oracle docker plan
WebLogic Oracle database plan starts up clustered WebLogic 12.2c environment with the Oracle 11xe or oracle 19c database.
The cluster is consisted from two nodes and the admin served. DB Connection pool and the SMP application is deployed to all 
servers.    

# start environment
execute bash script 

    ./compose/weblogic-oracle/runCompose.sh
    
**Note**: if the Nodes are not starting (Caused By: com.rsa.jsafe.JSAFE_PaddingException: Invalid padding.). Please make sure
the notes are using the same ./smp-docker/compose/weblogic-oracle/data/smp-cluster-4.2-RC2-SNAPSHOT.jar generated from the admin server!
 

Restart clean node-01
docker-compose -f ./compose/weblogic-oracle/docker-compose.yml -p smp-wls-orcl up --force-recreate --no-deps   smp-node-01