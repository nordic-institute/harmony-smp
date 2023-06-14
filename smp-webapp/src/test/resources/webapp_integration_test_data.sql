
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('encryption.key.filename','encryptionKey.key', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.keystore.password', 'FarFJE2WUfY39SVRTFOqSg==', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.keystore.filename', 'smp-keystore_multiple_domains.jks', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.keystore.type', 'JKS', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.truststore.filename', 'smp-truststore.jks', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.truststore.type', 'JKS', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.truststore.password', '{DEC}{test123}', NOW(),  NOW());

insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('contextPath.output', 'true', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('bdmsl.integration.physical.address', '0.0.0.0', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('bdmsl.integration.logical.address', 'http://localhost/smp', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('bdmsl.integration.url', 'http://localhost/edelivery-sml', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('bdmsl.integration.enabled', 'false', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.http.forwarded.headers.enabled', 'true', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('smp.automation.authentication.external.tls.clientCert.enabled', 'true', NOW(),  NOW());
insert into SMP_CONFIGURATION (PROPERTY_NAME, PROPERTY_VALUE, CREATED_ON, LAST_UPDATED_ON) values ('identifiersBehaviour.scheme.mandatory', 'false',  NOW(),  NOW());

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON)
values (1, 'smp_admin', 1, 'SYSTEM_ADMIN',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 'smp_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','USERNAME_PASSWORD','UI' , NOW(),  NOW()),
(2, 1, 1, 'pat_smp_admin', '$2a$10$bP44Ij/mE6U6OUo/QrKCvOb7ouSClKnyE0Ak6t58BLob9OTI534IO','ACCESS_TOKEN','REST_API' , NOW(),  NOW());

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(2, 'sg_admin', 1, 'USER',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(3, 2, 1, 'sg_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','USERNAME_PASSWORD','UI' , NOW(),  NOW()),
(4, 2, 1, 'pat_sg_admin', '$2a$10$bP44Ij/mE6U6OUo/QrKCvOb7ouSClKnyE0Ak6t58BLob9OTI534IO','ACCESS_TOKEN','REST_API' , NOW(),  NOW());

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(3, 'sys_admin', 1, 'SYSTEM_ADMIN',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(5, 3, 1, 'sys_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','USERNAME_PASSWORD','UI' , NOW(),  NOW()),
(6, 3, 1, 'pat_sys_admin', '$2a$10$bP44Ij/mE6U6OUo/QrKCvOb7ouSClKnyE0Ak6t58BLob9OTI534IO','ACCESS_TOKEN','REST_API' , NOW(),  NOW());

insert into SMP_USER(ID, USERNAME,  ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(4, 'test_user_hashed_pass', 1, 'USER',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(7, 4, 1, 'test_user_hashed_pass', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW','USERNAME_PASSWORD','UI' , NOW(),  NOW()),
(8, 4, 1, 'test_pat_hashed_pass', '$2a$10$bP44Ij/mE6U6OUo/QrKCvOb7ouSClKnyE0Ak6t58BLob9OTI534IO','ACCESS_TOKEN','REST_API' , NOW(),  NOW());

insert into SMP_USER(ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(5, 'test_user_clear_pass', 1, 'USER',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(9, 5, 1, 'test_user_clear_pass', 'test123','USERNAME_PASSWORD','UI' , NOW(),  NOW()),
(10, 5, 1, 'test_pat_clear_pass', 'test123','ACCESS_TOKEN','REST_API' , NOW(),  NOW());

insert into SMP_USER(ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(6, 'cert1',  1, 'USER',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(11, 6, 1, 'CN=common name,O=org,C=BE:000000000000bb66', null,'CERTIFICATE','REST_API' , NOW(),  NOW()),
(12, 6, 1, 'CN=GRP:test_proxy_01,O=European Commission,C=BE:0000000000001234', null,'CERTIFICATE','REST_API' , NOW(),  NOW()),
(13, 6, 1, 'CN=GRP:TEST_\+\,& \=eau!,O=European Commission,C=BE:0000000000001234', null,'CERTIFICATE','REST_API' , NOW(),  NOW()),
(14, 6, 1, 'CN=ncp.fi.ehealth.testa.eu,O=Kansanelakelaitos,C=FI:f71ee8b11cb3b787', null,'CERTIFICATE','REST_API' , NOW(),  NOW()),
(15, 6, 1, 'CN=Internal Business CA 2,O=T-Systems International GmbH,C=DE:f71ee8b11cb3b787', null,'CERTIFICATE','REST_API' , NOW(),  NOW()),
(16, 6, 1, 'CN=common name UPPER database SN,O=org,C=BE:000000000000BB66', null,'CERTIFICATE','REST_API' , NOW(),  NOW())
;

insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values
(11, 'CN=common name,O=org,C=BE:000000000000bb66', null,null, NOW(),  NOW()),
(12, 'CN=GRP:test_proxy_01,O=European Commission,C=BE:0000000000001234', null,null, NOW(),  NOW()),
(13, 'CN=GRP:TEST_\+\,& \=eau!,O=European Commission,C=BE:0000000000001234', null,null, NOW(),  NOW()),
(14, 'CN=ncp.fi.ehealth.testa.eu,O=Kansanelakelaitos,C=FI:f71ee8b11cb3b787', null,null, NOW(),  NOW()),
(15, 'CN=Internal Business CA 2,O=T-Systems International GmbH,C=DE:f71ee8b11cb3b787', null,null, NOW(),  NOW()),
(16, 'CN=common name UPPER database SN,O=org,C=BE:000000000000BB66', null,null, NOW(),  NOW());

insert into SMP_USER(ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(7, 'cert2',  1, 'USER',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(17, 7, 1, 'CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', null,'CERTIFICATE','REST_API' , NOW(),  NOW());
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values
(17, 'CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', null,null, NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(18, 7, 1, 'CN=utf-8_z_SMP,O=EC,C=BE:0000000000000666', null,'CERTIFICATE','REST_API' , NOW(),  NOW());
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values
(18, 'CN=utf-8_z_SMP,O=EC,C=BE:0000000000000666', null,null, NOW(),  NOW());



-- insert into SMP_USER(ID, USERNAME, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (8, 'Cert3', 'SMP_ADMIN', 1, NOW(),  NOW());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (8, 'CN=utf-8_ż_SMP,O=EC,C=BE:0000000000000666', null,null, NOW(),  NOW());

-- insert into SMP_USER(ID, USERNAME, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (9, 'Cert4', 'SMP_ADMIN', 1, NOW(),  NOW());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (9, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:f71ee8b11cb3b787', null,null, NOW(),  NOW());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (10, 'cert5', '',                                                             'SMP_ADMIN', 1, NOW(),  NOW());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (10, 'CN=common name UPPER database SN,O=org,C=BE:000000000000BB66', null,null, NOW(),  NOW());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (11, 'cert6', '',                                                             'SMP_ADMIN', 1, NOW(),  NOW());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (11, 'CN=ncp.fi.ehealth.testa.eu,O=Kansanelakelaitos,C=FI:f71ee8b11cb3b787', null,null, NOW(),  NOW());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (12, 'cert7', '',                                                             'SMP_ADMIN', 1, NOW(),  NOW());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (12, 'CN=Internal Business CA 2,O=T-Systems International GmbH,C=DE:f71ee8b11cb3b787', null,null, NOW(),  NOW());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (13, 'cert8', '',                                                             'SMP_ADMIN', 1, NOW(),  NOW());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (13, 'CN=GRP:test_proxy_01,O=European Commission,C=BE:0000000000001234', null,null, NOW(),  NOW());

-- insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (14, 'cert9', '',                                                             'SMP_ADMIN', 1, NOW(),  NOW());
-- insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (14, 'CN=GRP:TEST_\+\,& \=eau!,O=European Commission,C=BE:0000000000001234', null,null, NOW(),  NOW());
-- --------------
-- Configure domains
insert into SMP_DOMAIN (ID, VISIBILITY, DOMAIN_CODE, SML_SUBDOMAIN, SML_SMP_ID, SIGNATURE_KEY_ALIAS,SML_REGISTERED,SML_CLIENT_CERT_AUTH, CREATED_ON, LAST_UPDATED_ON) values
(1,'PUBLIC', 'domain','subdomain', 'CEF-SMP-001','single_domain_key',0,1, NOW(),  NOW()),
(2, 'PUBLIC', 'domainTwo','newdomain', 'CEF-SMP-002','single_domain_key',0,1,NOW(),  NOW());

insert into SMP_GROUP (ID, FK_DOMAIN_ID, NAME, VISIBILITY, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 'domain group', 'PUBLIC', NOW(),  NOW());

-- --------------
-- configure extension and document types service group and servicemetadata
insert into SMP_EXTENSION ( ID, IDENTIFIER,  IMPLEMENTATION_NAME, NAME, VERSION, DESCRIPTION, CREATED_ON, LAST_UPDATED_ON) values
(1, 'edelivery-oasis-smp-extension',  'OasisSMPExtension','Oasis SMP 1.0 and 2.0','1.0', 'Oasis SMP 1.0 and 2.0 extension',  NOW(),  NOW());

insert into SMP_RESOURCE_DEF ( ID, FK_EXTENSION_ID, URL_SEGMENT, IDENTIFIER, DESCRIPTION, MIME_TYPE, NAME, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 'smp-1', 'edelivery-oasis-smp-1.0-servicegroup', 'Service group', 'application/xml','Oasis SMP ServiceGroup', NOW(),  NOW()),
(2, 1, 'cpa-1', 'edelivery-oasis-cppa', 'Cppa document group', 'application/xml','Oasis SMP cpa document', NOW(),  NOW());
insert into SMP_SUBRESOURCE_DEF (ID,FK_RESOURCE_DEF_ID,URL_SEGMENT, IDENTIFIER, DESCRIPTION, MIME_TYPE, NAME, CREATED_ON, LAST_UPDATED_ON) values
(1,1, 'services', 'edelivery-oasis-smp-1.0-servicemetadata', 'ServiceMetadata', 'application/xml','Oasis SMP ServiceMetadata', NOW(),  NOW());
-- register document types for domain
insert into SMP_DOMAIN_RESOURCE_DEF (ID, FK_RESOURCE_DEF_ID, FK_DOMAIN_ID,CREATED_ON, LAST_UPDATED_ON ) values
(1, 1, 1, NOW(),  NOW());

-- ----------------------------------
-- add documents
insert into SMP_DOCUMENT (ID, CURRENT_VERSION, MIME_TYPE, NAME,CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 'application/xml', 'service-group', NOW(),  NOW()),
(2, 1, 'application/xml', 'service-group', NOW(),  NOW()),
(3, 1, 'application/xml', 'service-metadata', NOW(),  NOW());

insert into SMP_DOCUMENT_VERSION (ID, FK_DOCUMENT_ID, VERSION, DOCUMENT_CONTENT, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, '<ServiceGroup xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05"><ParticipantIdentifier scheme="iso6523-actorid-upis">0088:777002abzz777</ParticipantIdentifier><ServiceMetadataReferenceCollection/></ServiceGroup>' , NOW(),  NOW()),
(2, 2, 1, '<ServiceGroup xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05"><ParticipantIdentifier scheme="iso6523-actorid-upis">0088:777002abzz777</ParticipantIdentifier><ServiceMetadataReferenceCollection/></ServiceGroup>' , NOW(),  NOW()),
(3, 3, 1, FILE_READ('classpath:/input/ServiceMetadata.xml') , NOW(),  NOW());

insert into SMP_RESOURCE ( ID, FK_GROUP_ID, FK_DOCUMENT_ID, FK_DOREDEF_ID,  IDENTIFIER_SCHEME, IDENTIFIER_VALUE, SML_REGISTERED, VISIBILITY, CREATED_ON, LAST_UPDATED_ON) values
(100000, 1, 1, 1, 'ehealth-actorid-qns', 'urn:australia:ncpb', 0, 'PUBLIC', NOW(),  NOW()),
(200000, 1, 2, 1, 'ehealth-actorid-qns', 'urn:brazil:ncpb', 0, 'PUBLIC', NOW(),  NOW());

insert into SMP_SUBRESOURCE (ID, FK_RESOURCE_ID,FK_SUREDEF_ID, FK_DOCUMENT_ID, IDENTIFIER_SCHEME, IDENTIFIER_VALUE, CREATED_ON, LAST_UPDATED_ON) values
(1, 100000, 1, 3, 'busdox-docid-qn', 'doc_7', NOW(),  NOW());

insert into SMP_GROUP_MEMBER (ID, FK_GROUP_ID, FK_USER_ID, MEMBERSHIP_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 'ADMIN', NOW(),  NOW());
-- set ownership
insert into SMP_RESOURCE_MEMBER (ID, FK_RESOURCE_ID, FK_USER_ID, MEMBERSHIP_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 100000, 1, 'ADMIN', NOW(),  NOW()),
(2, 200000, 1, 'ADMIN', NOW(),  NOW()),
(3, 100000, 5, 'ADMIN', NOW(),  NOW()),
(4, 100000, 2, 'ADMIN', NOW(),  NOW()),
(5, 100000, 6, 'ADMIN', NOW(),  NOW());



--insert into SMP_RESOURCE_DOMAIN (ID, FK_SG_ID, FK_DOMAIN_ID,SML_REGISTERED,  CREATED_ON, LAST_UPDATED_ON) values (1000,200000, 1, 0,  NOW(),  NOW());

--insert into SMP_RESOURCE_DOMAIN (ID, FK_SG_ID, FK_DOMAIN_ID,SML_REGISTERED,  CREATED_ON, LAST_UPDATED_ON) values (1001,100000, 1, 0 , NOW(),  NOW());
--insert into SMP_SUBRESOURCE (ID, FK_SG_DOM_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME, LAST_UPDATED_ON, CREATED_ON)  values (1000,1001,'doc_7','busdox-docid-qns', NOW(),  NOW());
--insert into SMP_DOCUMENT (ID, XML_CONTENT, LAST_UPDATED_ON, CREATED_ON)  values (1000, FILE_READ('classpath:/input/ServiceMetadata.xml'), NOW(),  NOW());



