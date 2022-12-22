SMP docker image 
================================
This Dockerfile extends the Oracle WebLogic image built from Oracle WebLogic Server 14c (14.1.1.0). The image deploy the SMP application to admin 
server and to the cluster. 
The image does not initialize the domain nor deploy the SMP to the WebLogic domain. Instead, it sets all prerequisites to 
create the WebLogic domain and deploy SMP at the first startup of the image. Initializing the domain at first startup allows 
users more flexibility in the domain configuration and SMP deployment. When the domain initialization is completed, 
the domain package is stored in the file: "${DOCKER_DATA}/${WL_CLUSTER_NAME}.jar" 
(when using the default values, the file is: /data/smp-cluster.jar). The file must be mounted to the same location in the starting 
nodes to be started as a cluster node for the same domain. The easiest way is to mount folder:  ./data:/data to the 
admin server and cluster nodes. 


# How to build the image

The following preconditions must be met to build the image:
 - image [oracle/weblogic:14.1.1.0-generic](../oracle/weblogic-14.1.1.0) must be build or must be accessible via "docker pull registry"
 - smp artefacts *smp.war* and *smp-setup.zip* must be added to subfolder *./artefacts*. 

   
To build image executed the command (set the smp version accordingly)

        $ docker build -t "smp-weblogic-141:5.0-SNAPSHOT" . 


# How to run the domain

To start the containerized Administration Server, run:

        $ docker run -d --name wlsadmin --hostname smp-wls-admin -p 7001:7001 \
          -v <HOST DIRECTORY TO SHARED DATA>/dasta:/data \
          smp-weblogic-141:5.0-SNAPSHOT

To start a containerized Managed Server (smp-node-1) to self-register with the Administration Server above, run:

        $ docker run -d --name smp-node-1  -p 8001:8001 \
          -v <HOST DIRECTORY TO SHARED DATA>/dasta:/data \
          -e WL_ADMIN_HOST=smp-wls-admin \          
          -e WL_MANAGED_SERV_NAME=smp-node-1 smp-weblogic-141:5.0-SNAPSHOT startManagedServer.sh

To start a second Managed Server (smp-node-2), run:

        $ docker run -d --name smp-node-2  -p 8001:8001 \
          -v <HOST DIRECTORY TO SHARED DATA>/dasta:/data \
          -e WL_ADMIN_HOST=smp-wls-admin \          
          -e WL_MANAGED_SERV_NAME=smp-node-2  smp-weblogic-141:5.0-SNAPSHOT startManagedServer.sh


Run the WLS Administration Console:

In your browser, enter `https://localhost:7001/console`.

Run the sample application:

To access the sample application, in your browser enter `http://localhost:7001/smp/`.

# SMP and WebLogic configuration

At the first startup of the admin server, the domain is initialized and stored into the file: 
`${DOCKER_DATA}/${WL_CLUSTER_NAME}-${SMP_VERSION}.jar`
 (the default values gives file path: `/data/smp-cluster-5.0-SNAPSHOT.jar`). The file is needed to create node deployment 
 on an empty WebLogic installation using the: unpack.sh command. Make sure the file is available on the same container 
 path when starting the nodes.  

## WebLogic domain init configuration
When the domain has initialized the file 
`./weblogic-14.1-smp/properties/init/domain.properties` is used as domain base properties. To the file, the following 
environment properties are appended:
See the: `weblogic-14.1-smp/container-scripts/init-scripts/createWLSDomain.sh`

    DOMAIN_NAME=${WL_DOMAIN_NAME}
    ADMIN_PORT=${WL_ADMIN_PORT}
    ADMIN_HTTPS_PORT=${WL_ADMIN_PORT_HTTPS}
    ADMIN_NAME=${WL_ADMIN_NAME}
    ADMIN_HOST=${WL_ADMIN_HOST}
    MANAGED_SERVER_PORT=${WL_MANAGED_SERVER_PORT}
    MANAGED_SERVER_NAME_BASE=${WL_MANAGED_SERV_BASE_NAME}
    CONFIGURED_MANAGED_SERVER_COUNT=${WL_MANAGED_SERVER_COUNT}
    CLUSTER_NAME=${WL_CLUSTER_NAME}
    DEBUG_FLAG=${WL_DEBUG_FLAG}
    PRODUCTION_MODE_ENABLED=${WL_PRODUCTION_MODE_ENABLED}

In case other properties are needed, define your own "domain.properties" and map it to the container folder: `/u01/init/`
as example: 

    volumes:
          - ./properties/weblogic-init:/u01/init/


## WebLogic admin username and password
The weblogi admin username and password credential are used to access `https://localhost:7001/console` and also 
for the cluster nodes to connect to WebLogic admin. The credentials are not defined in folder
`/u01/init/domain_security.properties`  as example: 
     
     username=wls-smp
     password=wls-pass-01
 
The default user name is used, and a random password is generated. The password is logged to the admin logs at the domain 
initialization event.

    ‘/u01/oracle/init/properties/domain_security.properties’: No such file or directory
    To increase security please provide custom admin username and password in /tmp/create-domain/properties/domain_security.properties.
    Generated WebLogic admin user with credentials: weblogic/9HLS3cugQBlXyncNC0GcHuE3MNbhgOrrcR5kZluXAA68lTJapKeYxk7D4LbeYTwc

The credentials are copied to the file `/data/weblogic/security.properties`, with intention to be used for node server. 
After servers are started for the first time - the file can be removed/deleted  
    
    # example of generated  /data/weblogic/security.properties
    username=weblogic
    password=weblogic-custom-password

## Weblogic Database configuration.
Weblogic database configured based on the file `/u01/init/datasource.properties`

    dsname=eDeliverySmpDs
    dsdbname=eDeliverySmpDs
    dsjndiname=jdbc/eDeliverySmpDs
    dsdriver=oracle.jdbc.OracleDriver
    dsurl=jdbc:oracle:thin:@//smp-oracle-db:1521/xe
    dsusername=smp
    dspassword=test
    dstestquery=SQL SELECT 1 FROM DUAL

## SMP initial configuration.
SMP initial configuration can be provided in file  `/smp.config.properties`

    # example of the SMP configuration file (please note the example where SMP uses JNDI datasource!)
    hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
    datasource.jndi=jdbc/eDeliverySmpDs
    configuration.dir=/data/smp/security
    authentication.blueCoat.enabled=true
    log.folder=./logs/
    
