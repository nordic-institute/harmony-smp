-- Copyright 2018 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('encryption.key.filename','encryptionKey.key',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.keystore.password', 'FarFJE2WUfY39SVRTFOqSg==',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.keystore.filename', 'smp-keystore_multiple_domains.jks',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.keystore.type', 'JKS',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.truststore.filename', 'smp-truststore.jks',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.truststore.type', 'JKS',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.truststore.password', '{DEC}{test123}',current_timestamp(), current_timestamp());

insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('contextPath.output', 'true',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('bdmsl.integration.physical.address', '0.0.0.0',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('bdmsl.integration.logical.address', 'http://localhost/smp',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('bdmsl.integration.url', 'http://localhost/edelivery-sml',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('bdmsl.integration.enabled', 'false',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.http.forwarded.headers.enabled', 'true',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.automation.authentication.external.tls.clientCert.enabled', 'true',current_timestamp(), current_timestamp());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('identifiersBehaviour.scheme.mandatory', 'false', current_timestamp(), current_timestamp());

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON)
values (1, 'smp_admin', 1, 'SYSTEM_ADMIN', current_timestamp(), current_timestamp());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 'smp_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','USERNAME_PASSWORD','UI' ,current_timestamp(), current_timestamp()),
(2, 1, 'pat_smp_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','ACCESS_TOKEN','REST_API' ,current_timestamp(), current_timestamp());

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(2, 'sg_admin', 1, 'USER', current_timestamp(), current_timestamp());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(3, 2, 'sg_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','USERNAME_PASSWORD','UI' ,current_timestamp(), current_timestamp()),
(4, 2, 'pat_sg_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','ACCESS_TOKEN','REST_API' ,current_timestamp(), current_timestamp());

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(3, 'sys_admin', 1, 'USER', current_timestamp(), current_timestamp());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(5, 3, 'sys_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','USERNAME_PASSWORD','UI' ,current_timestamp(), current_timestamp()),
(6, 3, 'pat_sys_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','ACCESS_TOKEN','REST_API' ,current_timestamp(), current_timestamp());

insert into SMP_USER(ID, USERNAME,  ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(4, 'test_user_hashed_pass', 1, 'USER', current_timestamp(), current_timestamp());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(7, 4, 'test_user_hashed_pass', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','USERNAME_PASSWORD','UI' ,current_timestamp(), current_timestamp()),
(8, 4, 'test_pat_hashed_pass', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','ACCESS_TOKEN','REST_API' ,current_timestamp(), current_timestamp());

insert into SMP_USER(ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(5, 'test_user_clear_pass', 1, 'USER', current_timestamp(), current_timestamp());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(9, 5, 'test_user_clear_pass', 'test123','USERNAME_PASSWORD','UI' ,current_timestamp(), current_timestamp()),
(10, 5, 'test_pat_clear_pass', 'test123','ACCESS_TOKEN','REST_API' ,current_timestamp(), current_timestamp());

insert into SMP_USER(ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(6, 'cert1',  1, 'USER', current_timestamp(), current_timestamp());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(11, 6, 'CN=common name,O=org,C=BE:000000000000bb66', null,'CERTIFICATE','REST_API' ,current_timestamp(), current_timestamp());
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values
(11, 'CN=common name,O=org,C=BE:000000000000bb66', null,null,current_timestamp(), current_timestamp());

insert into SMP_USER(ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(7, 'cert2',  1, 'USER', current_timestamp(), current_timestamp());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(12, 7, 'CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', null,'CERTIFICATE','REST_API' ,current_timestamp(), current_timestamp());
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values
(12, 'CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', null,null,current_timestamp(), current_timestamp());

-- insert into SMP_USER(ID, USERNAME, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (8, 'Cert3', 'SMP_ADMIN', 1,current_timestamp(), current_timestamp());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (8, 'CN=utf-8_ż_SMP,O=EC,C=BE:0000000000000666', null,null,current_timestamp(), current_timestamp());

-- insert into SMP_USER(ID, USERNAME, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (9, 'Cert4', 'SMP_ADMIN', 1,current_timestamp(), current_timestamp());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (9, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:f71ee8b11cb3b787', null,null,current_timestamp(), current_timestamp());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (10, 'cert5', '',                                                             'SMP_ADMIN', 1,current_timestamp(), current_timestamp());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (10, 'CN=common name UPPER database SN,O=org,C=BE:000000000000BB66', null,null,current_timestamp(), current_timestamp());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (11, 'cert6', '',                                                             'SMP_ADMIN', 1,current_timestamp(), current_timestamp());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (11, 'CN=ncp.fi.ehealth.testa.eu,O=Kansanelakelaitos,C=FI:f71ee8b11cb3b787', null,null,current_timestamp(), current_timestamp());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (12, 'cert7', '',                                                             'SMP_ADMIN', 1,current_timestamp(), current_timestamp());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (12, 'CN=Internal Business CA 2,O=T-Systems International GmbH,C=DE:f71ee8b11cb3b787', null,null,current_timestamp(), current_timestamp());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (13, 'cert8', '',                                                             'SMP_ADMIN', 1,current_timestamp(), current_timestamp());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (13, 'CN=GRP:test_proxy_01,O=European Commission,C=BE:0000000000001234', null,null,current_timestamp(), current_timestamp());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (14, 'cert9', '',                                                             'SMP_ADMIN', 1,current_timestamp(), current_timestamp());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (14, 'CN=GRP:TEST_\+\,& \=eau!,O=European Commission,C=BE:0000000000001234', null,null,current_timestamp(), current_timestamp());


-- set the ids to higher values - tests are using sequnce which stars from 1
-- insert into SMP_RESOURCE(ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME, CREATED_ON, LAST_UPDATED_ON) values (100000, 'urn:australia:ncpb', 'ehealth-actorid-qns', current_timestamp(), current_timestamp());
-- insert into SMP_RESOURCE(ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME, CREATED_ON, LAST_UPDATED_ON) values (200000, 'urn:brazil:ncpb', 'ehealth-actorid-qns', current_timestamp(), current_timestamp());
--insert into SMP_SG_EXTENSION(ID, EXTENSION,CREATED_ON, LAST_UPDATED_ON) values (100000, '<Extension xmlns:ns2="http://www.w3.org/2000/09/xmldsig#" xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05"><ExtensionID>id1</ExtensionID><ExtensionName>name1</ExtensionName><ExtensionAgencyName>agencyName1</ExtensionAgencyName><ExtensionAgencyURI>agencyUri1</ExtensionAgencyURI><ExtensionVersionID>versionId1</ExtensionVersionID><ExtensionReasonCode>reasonCode1</ExtensionReasonCode><ExtensionReason>reason1</ExtensionReason></Extension>', current_timestamp(), current_timestamp());



-- set ownership
--insert into SMP_RESOURCE_MEMBER (FK_SG_ID, FK_USER_ID) values (100000, 5);
--insert into SMP_RESOURCE_MEMBER (FK_SG_ID, FK_USER_ID) values (200000, 2);

--insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SML_SMP_ID, SIGNATURE_KEY_ALIAS,SML_REGISTERED,SML_BLUE_COAT_AUTH,SML_CLIENT_CERT_HEADER, CREATED_ON, LAST_UPDATED_ON) values (1, 'domain','subdomain', 'CEF-SMP-001','single_domain_key',0,1,'SML_CLIENT_CERT_HEADER',current_timestamp(), current_timestamp());
--insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SML_SMP_ID, SIGNATURE_KEY_ALIAS,SML_REGISTERED,SML_BLUE_COAT_AUTH,SML_CLIENT_CERT_HEADER, CREATED_ON, LAST_UPDATED_ON) values (2, 'domainTwo','newdomain', 'CEF-SMP-002','single_domain_key',0,1,'SML_CLIENT_CERT_HEADER',current_timestamp(), current_timestamp());


--insert into SMP_RESOURCE_DOMAIN (ID, FK_SG_ID, FK_DOMAIN_ID,SML_REGISTERED,  CREATED_ON, LAST_UPDATED_ON) values (1000,200000, 1, 0, current_timestamp(), current_timestamp());

--insert into SMP_RESOURCE_DOMAIN (ID, FK_SG_ID, FK_DOMAIN_ID,SML_REGISTERED,  CREATED_ON, LAST_UPDATED_ON) values (1001,100000, 1, 0 ,current_timestamp(), current_timestamp());
--insert into SMP_SUBRESOURCE (ID, FK_SG_DOM_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME, LAST_UPDATED_ON, CREATED_ON)  values (1000,1001,'doc_7','busdox-docid-qns',current_timestamp(), current_timestamp());
--insert into SMP_DOCUMENT (ID, XML_CONTENT, LAST_UPDATED_ON, CREATED_ON)  values (1000, FILE_READ('classpath:/input/ServiceMetadata.xml'),current_timestamp(), current_timestamp());



