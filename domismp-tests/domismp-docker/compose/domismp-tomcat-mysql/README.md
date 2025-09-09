# Experimental SMP docker image
Purpose of compose plan is to startup fully functional SMP environment for demo and testing. The plan contains. 
 - MySql database
 - SML services
 - CAS instance (EULOGIN)
 - email server (inbucket)


# Run environment

The environment can be started with the following command. ()

    docker compose -f docker-compose.yml up -d

To start the environment with local configuration (for development and testing). 

    docker compose -f docker-compose.yml -f docker-compose.local.yml up -d

# Stop environment
docker-compose -f docker-compose.yml down


## Start/Stop environment with provides bash scripts

To start the environment the bash scripts can be used. Scripts configures the environment variables and starts the services.
The command: 

    ./runCompose.sh 

The compose scripts has the following options:

- i: path to the database data initialization script, default: SMP_PROJECT_FOLDER/smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb-data.sql
- v: version of the SMP to start. If not provided, the version will defined by maven project version
- l: start with local compose file docker-compose.localhost.yml, default: false. The compose file is used to start  the SMP with local configuration (e.g. exporting ports, etc.)

The command:

    ./runCompose.sh local -i /path/to/your/data.sql -v 1.0.0 -l true
 


## SMP 
url: http://localhost:8982/smp/

http://eulogin.protected.smp.local:8982/smp/
eulogin.protected.smp.local

### MYSQL 
Database client connection (for testing and debugging )
url: jdbc:mysql://localhost:3908/smp
Username: smp
Password: smp

### Volume (-v /opt/dockerdata/sml:/data)
Mysql database files and tomcat configuration (and logs) can be externalized for experimenting with different SMP settings.


## SML 
url: http://localhost:8982/edelivery-sml/

### MYSQL 
Database client connection (for testing and debugging )
url: jdbc:mysql://localhost:3908/sml
Username: sml
Password: sml

## CAS - EULOGIN 
url: https://localhost:7102/cas/login
Users: (For details see the CAS configuration: [userDataBase.xml](eulogin%2Finit-data%2FuserDataBase.xml)):
- user (Peter.PARKER@dummy-mail-not-exists.eu)/123456
- system (Bruce.BANNER@dummy-mail-not-exists.eu)/123456
- user-02 (Tony.STARK@dummy-mail-not-exists.eu)/123456

 To use eulogin add the following hostname mappings (Linux: /etc/hosts, windows: C:\Windows\System32\drivers\etc\hosts
                                                                                 
 - 127.0.0.1 eulogin-mock-server
 - 127.0.0.1 eulogin.protected.smp.local


## Mail server

Mock mail server for monitoring send alert mails. 
url: http://localhost:9005/monitor 

## Authorization server
DomiSMP 2.x supports OAuth2 / OpenID Connect authorization. In the compose plan Keycloak server is used as the authorization server for generating the JWT tokens. 


When started login to authorization server with browser (username/password: admin/admin)
http://authorization-server:8180/admin/master/console/
(User OOTS realm to see the test configuration)


test generate jwt token

> curl -k --cert ddc-client.crt.pem --key ddc-client.key.pem \
-d "grant_type=client_credentials&client_id=oots-ddc-client" \
https://authorization-server:8143/realms/OOTS/protocol/openid-connect/token


Expected response example
{"access_token":"eyJhbGciOiJSUzI1Ni....TxsxP8GAwkJXI949FRE8Wx3kUgP6AfJ5YAjKuJ2wNzw","expires_in":300,"refresh_expires_in":0,"token_type":"Bearer","not-before-policy":0,"scope":"oots-smp-group-be oots-smp-domain"}



> curl -X GET http://localhost:8280/smp/privateDomain/smp-1/urn%3Aoasis%3Anames%3Atc%3Aebcore%3Apartyid-type%3Aiso6523%3A0088%3A%3Atest%3Ajwt%3A001
- Response: http error 401
<?xml version="1.0" encoding="UTF-8" standalone="yes"?><ErrorResponse xmlns="ec:services:SMP:1.0"><BusinessCode>UNAUTHORIZED</BusinessCode><ErrorDescription>User is not authorized for the domain!</ErrorDescription><ErrorUniqueId>2025-08-11T08:28:19.712299141Z:9c4cdf7c-8ea7-4a58-87da-6cd3adb98082</ErrorUniqueId></ErrorResponse>


With the JWT token  (Please make sure it is not expired!)


> curl -X GET -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiYXQrand0Iiwia2lkIiA6ICJEU2dEdFc2bnFmTWVuUUdhcmc0SjRsVWtWMEp5RE1CaHdZUjlqTWF5aVI4In0.eyJleHAiOjE3NTQ5MDUwMjQsImlhdCI6MTc1NDkwNDcyNCwianRpIjoidHJydGNjOjViMzdlYTZhLTM1NGQtYjBmNy1hMzMwLTRjNGE0ZGI2NGVhOSIsImlzcyI6Imh0dHBzOi8vYXV0aG9yaXphdGlvbi1zZXJ2ZXI6ODE0My9yZWFsbXMvT09UUyIsImF1ZCI6Im9vdHMtYXVkaWVuY2UtY3VzdG9tIiwic3ViIjoiZjdiNWM2YzktNzJiYS00MDlmLTkzYjAtN2Q3ZTBkYTU5YmEyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoib290cy1kZGMtY2xpZW50Iiwic2NvcGUiOiJvb3RzLXNtcC1ncm91cC1iZSBvb3RzLXNtcC1kb21haW4iLCJjbGllbnRIb3N0IjoiMTcyLjIxLjAuMSIsImNsaWVudEFkZHJlc3MiOiIxNzIuMjEuMC4xIiwiY2xpZW50X2lkIjoib290cy1kZGMtY2xpZW50In0.EdLl3ODYrv-k1PhAIZAMgg5dKMf7bO2Svw6nvP07WVb4Mn6MF_D5nMRNzge4gJs-3qHGi8FFHJ3A0VOIWCZKOvU3jPlYEOvSoCBW_DZ1NvFBjyaFR3Y2tEfd-OIY2ecBo9ejj3SJKG71fIZ3DtiJ7YwfGaveFffZWKWLTNuuygi3yAUI1Z2McB7nd5bP9YRo6jH0gPNDR2fA4kIOUMcWqDfU_ZTLKtdg4tYk1k31oXArY4xN7LGlDSN9NGbkRQftyDBTJgK69G7SPx8j3MCokhxR_AhcpyEAIs1HFyMALetTxsxP8GAwkJXI949FRE8Wx3kUgP6AfJ5YAjKuJ2wNzw" http://localhost:8280/smp/oots-smp-domain/smp-1/urn%3Aoasis%3Anames%3Atc%3Aebcore%3Apartyid-type%3Aiso6523%3A0088%3A%3Atest%3Ajwt%3A001


== Example with dynamic discovery client

> java -jar ddc-3.1-SNAPSHOT.jar -get \
 -rs ehealth-actorid-qns -ri 0088:7770010100777:test:smp001 \
 -smp https://eulogin.protected.smp.local:8943/smp/oots-smp-domain/  \
 -kf domismp-tests/domismp-docker/compose/domismp-tomcat-mysql/keycloak/ddc/ddc-client.p12 \
 -kp test123 -kt PKCS12 -kkp test123 \
 -tf domismp-tests/domismp-docker/compose/domismp-tomcat-mysql/keycloak/ddc/ddc-truststore.p12 \
 -tp test123 -tt PKCS12 \
 -jwta https://authorization-server:8143/realms/OOTS/protocol/openid-connect/token  \
 -jwts oots-smp-domain oots-smp-group-be \
 -jwtc oots-ddc-client -nthv



To enable X5t#S256 go to OOTS realm -> Clients -> oots-ddc-client -> Advanced Settings -> and enable OAuth 2.0 Mutual TLS Certificate Bound Access Tokens Enabled 
Or make sure that the following attribute is set in keycloak/imports/realm-smp.json
/clients/["clientId"="oots-ddc-client"]/attributes/tls.client.certificate.bound.access.tokens=true