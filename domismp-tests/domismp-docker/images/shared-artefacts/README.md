# Test keystores and truststores
The folder contains the keystore and truststore files needed for the SMP/SML integration and the Tomcat HTTPS endpoint.

## The SMP keystore and SML truststore are needed for SMP/SML integration.
 - [sml-truststore-docker-demo.p12](sml-truststore-docker-demo.p12)
 - [smp-keystore-docker-demo.p12](smp-keystore-docker-demo.p12)

(Keystore and key password: test123)

The smp certificates 
 - CN=smp_domain_01,OU=edelivery,O=digit,C=eu
 - CN=smp_domain_02,OU=edelivery,O=digit,C=eu
are registered in SML as trusted certificates for domains
 - domain-01.test.edelivery.local 
 - domain-02.test.edelivery.local

For detailed DomiSML configuration see the SML init script:[sml-mysql5innodb-data.sql](sml-mysql5innodb-data.sql)


# Tomcate HTTPS endpoint
The Tomcat instance is configured with a mutual TLS endpoint. To access the HTTPS endpoint, start the Docker container with the following port mapping:
example snipped for docker-compose.yml:

    ports:
      - "8943:8443"

Then you can access the TLS endpoint via:

https://localhost:8943/smp/
Or (if the domain is correctly configured)
https://eulogin.protected.smp.local:8943/smp/ 

Important: Tomcat is configured for mutual authentication, therefore users must have a client TLS certificate. 
To access the HTTPS endpoint via a browser, install the client certificate in your browser. 
The pre-configured client certificate/key is provided in the keystore:
[client-tls-keystore.p12](client-tls-keystore.p12)
(Keystore and key password: test123)

# Test TLS client certificate for REST API (using SOAPUI) with mutual HTTPS authentication
To register new participants using the REST API with mutual HTTPS authentication, first register the certificate located at:
[client_test.cer](client_test.cer)

This can be done via the DomiSMP UI.
1. Log in as user: “user” 
2. Upload the certificate on the "user settings" page under “Certificate”.

If you are using SOAPUI, add the SSL configuration using the keystore:
[client-tls-keystore.p12](client-tls-keystore.p12)

Then target the HTTPS endpoint:
https://localhost:8943/smp/

