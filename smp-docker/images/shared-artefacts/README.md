The SMP keystore and SML truststore are needed for SMP/SML integration.
 - [sml-truststore.p12](sml-truststore.p12)
 - [smp-keystore-docker.p12](smp-keystore-docker.p12)

The smp certificates 
 - CN=smp_domain_01,OU=edelivery,O=digit,C=eu
 - CN=smp_domain_02,OU=edelivery,O=digit,C=eu
are registered in SML as trusted certificates for domains
 - domain-01.test.edelivery.local 
 - domain-02.test.edelivery.local
For detailed DomiSML configuration see the SML init script:[sml-mysql5innodb-data.sql](sml-mysql5innodb-data.sql)
