insert into bdmsl_configuration(property, value, description, created_on, last_updated_on) values
('useProxy','false','true if a proxy is required to connect to the internet. Possible values: true/false', NOW(), NOW()),
('unsecureLoginAllowed','true','true if the use of HTTPS is not required. If the value is set to true, then the user unsecure-http-client is automatically created. Possible values: true/false', NOW(), NOW()),
('signResponse','false','true if the responses must be signed. Possible values: true/false', NOW(), NOW()),
('paginationListRequest','100','Number of participants per page for the list operation of ManageParticipantIdentifier service. This property is used for pagination purposes.', NOW(), NOW()),
('keystorePassword','vXA7JjCy0iDQmX1UEN1Qwg==','Base64 encrypted password for Keystore.', NOW(), NOW()),
('keystoreFileName','keystore.jks','The JKS keystore file. Should be just the filename if the file is in the classpath or in the configurationDir', NOW(), NOW()),
('keystoreAlias','sendercn','The alias in the keystore.', NOW(), NOW()),
('httpProxyUser','user','The proxy user', NOW(), NOW()),
('httpProxyPort','80','The http proxy port', NOW(), NOW()),
('httpProxyPassword','setencPasswd','Base64 encrypted password for Proxy.', NOW(), NOW()),
('httpProxyHost','127.0.0.1','The http proxy host', NOW(), NOW()),
('encriptionPrivateKey','encriptionPrivateKey.private','Name of the 256 bit AES secret key to encrypt or decrypt passwords.', NOW(), NOW()),
('dnsClient.server','127.0.0.1','The DNS server', NOW(), NOW()),
('dnsClient.publisherPrefix','publisher','This is the prefix for the publishers (SMP). This is to be concatenated with the associated DNS domain in the table bdmsl_certificate_domain', NOW(), NOW()),
('dnsClient.enabled','true','true if registration of DNS records is required. Must be true in production. Possible values: true/false', NOW(), NOW()),
('dnsClient.SIG0PublicKeyName','sig0.acc.edelivery.tech.ec.europa.eu.','The public key name of the SIG0 key', NOW(), NOW()),
('dnsClient.SIG0KeyFileName','SIG0.private','The actual SIG0 key file. Should be just the filename if the file is in the classpath or in the configurationDir', NOW(), NOW()),
('dnsClient.SIG0Enabled','false','true if the SIG0 signing is enabled. Required fr DNSSEC. Possible values: true/false', NOW(), NOW()),
('dataInconsistencyAnalyzer.senderEmail','automated-notifications@nomail.ec.europa.eu','Sender email address for reporting Data Inconsistency Analyzer.', NOW(), NOW()),
('dataInconsistencyAnalyzer.recipientEmail','email@domain.com','Email address to receive Data Inconsistency Checker results', NOW(), NOW()),
('dataInconsistencyAnalyzer.cronJobExpression','0 0 3 ? * *','Cron expression for dataInconsistencyChecker job. Example: 0 0 3 ? * * (everyday at 3:00 am)', NOW(), NOW()),
('configurationDir','/opt/smlconf/','The absolute path to the folder containing all the configuration files (keystore and sig0 key)', NOW(), NOW()),
('certificateChangeCronExpression','0 0 2 ? * *','Cron expression for the changeCertificate job. Example: 0 0 2 ? * * (everyday at 2:00 am)', NOW(), NOW()),
('authorization.smp.certSubjectRegex','^.*(CN=SMP_|OU=PEPPOL TEST SMP).*$','User with ROOT-CA is granted SMP_ROLE only if its certificates Subject matches configured regexp', NOW(), NOW()),
('authentication.bluecoat.enabled','true','Enables reverse proxy authentication.', NOW(), NOW()),
('adminPassword','$2a$10$9RzbkquhBYRkHUoKMTNZhOPJmevTbUKWf549MEiCWUd.1LdblMhBi','BCrypt Hashed password to access admin services', NOW(), NOW()),
('mail.smtp.host','smtp.localhost','BCrypt Hashed password to access admin services', NOW(), NOW()),
('mail.smtp.port','25','BCrypt Hashed password to access admin services', NOW(), NOW()),
('sml.property.refresh.cronJobExpression','5 */1 * * * *','Properies update', NOW(), NOW());   


insert into bdmsl_subdomain(subdomain_id, subdomain_name,dns_zone, description, participant_id_regexp, dns_record_types, smp_url_schemas, created_on, last_updated_on) values
(1, 'domain-01.test.edelivery.local','test.edelivery.local','Domain for no trestriction ','^.*$','all','all', NOW(), NOW()),
(2, 'domain-02.test.edelivery.local', 'test.edelivery.local','Domain for with party id restriction', '^((((0002|0007|0009|0037|0060|0088|0096|0097|0106|0135|0142|9901|9902|9904|9905|9906|9907|9908|9909|9910|9912|9913|9914|9915|9916|9917|9918|9919|9920|9921|9922|9923|9924|9925|9926|9927|9928|9929|9930|9931|9932|9933|9934|9935|9936|9937|9938|9939|9940|9941|9942|9943|9944|9945|9946|9947|9948|9949|9950|9951|9952|9953|9954|9955|9956|9957|0184):).*)|(\\*))$','all','all',  NOW(), NOW());


INSERT INTO bdmsl_certificate_domain(truststore_alias, certificate, crl_url,  is_root_ca, fk_subdomain_id, created_on, last_updated_on, is_admin) VALUES
('CN=smp_domain_01', 'CN=smp_domain_01,O=digit,C=eu','',0, 1, NOW(), NOW(),1),
('CN=smp_domain_02','CN=smp_domain_02,O=digit,C=eu','',0, 2, NOW(), NOW(),1);

INSERT INTO bdmsl_certificate (id, certificate_id, valid_from ,valid_until,created_on, last_updated_on ) VALUES
(1, 'CN=smp_domain_01,O=digit,C=eu:0000000000000000000000006443d8a8',DATE_ADD(NOW(), INTERVAL -3 DAY),DATE_ADD(NOW(), INTERVAL 365 DAY), NOW(), NOW());

INSERT INTO bdmsl_smp (smp_id, fk_certificate_id, fk_subdomain_id, endpoint_logical_address, endpoint_physical_address, created_on, last_updated_on ) VALUES
('DOMI-SMP-001', 1,1, 'http://localhost:8080/smp/','0.0.0.0',NOW(), NOW());



