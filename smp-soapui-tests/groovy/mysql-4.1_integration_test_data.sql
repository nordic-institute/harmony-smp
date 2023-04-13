insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 'system', 1, 'SYSTEM_ADMIN',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(2, 1, 1, 'system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'USERNAME_PASSWORD','UI',  NOW(),  NOW()),
(3, 1, 1, 'pat_system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'ACCESS_TOKEN', 'REST_API',  NOW(),  NOW());

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE,  CREATED_ON, LAST_UPDATED_ON) values
(2, 'user', 1, 'USER',  NOW(),  NOW());
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(4, 2, 1, 'user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'USERNAME_PASSWORD','UI',  NOW(),  NOW()),
(5, 2, 1, 'user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'ACCESS_TOKEN', 'REST_API',  NOW(),  NOW()),
(6, 2, 1, 'LvglqPCs', '$2a$10$zaFAFqFIfLUZx15ZDPMvDeWBtsZLaAkrY3Vmya5e3/yCCkFq/FJCu', 'ACCESS_TOKEN', 'REST_API',  NOW(),  NOW()) ;

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(3, 'soapui_test_01', 1, 'USER',  NOW(),  NOW()),
(4, 'soapui_test_02', 1, 'USER',  NOW(),  NOW()),
(5, 'soapui_test_03', 1, 'USER',  NOW(),  NOW());

insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(7, 3, 1, 'VIhnrCJK', '$2a$10$BtInQBIycY2BSN28PD7TxO9ipAR3lhxUT2FLeShptGmjt6HaLpR7O', 'ACCESS_TOKEN', 'REST_API',  NOW(),  NOW()),
(8, 5, 1, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:f71ee8b11cb3b787', null, 'CERTIFICATE', 'REST_API',  NOW(),  NOW()),
(9, 4, 1, 'CN=EHEALTH&SMP_EC,O=European&Commission,C=B&E:f71ee8b11cb3b787', null, 'CERTIFICATE', 'REST_API',  NOW(),  NOW()),
(10, 4, 1, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:000000000000100f', null, 'CERTIFICATE', 'REST_API',  NOW(),  NOW()),
(11, 2, 1, 'CN=blue_gw,O=eDelivery,C=BE:E07B6b956330a19a', null, 'CERTIFICATE', 'REST_API',  NOW(),  NOW()),
(12, 2, 1, 'CN=red_gw,O=eDelivery,C=BE:9792ce69BC89F14C', null, 'CERTIFICATE', 'REST_API',  NOW(),  NOW()),
(13, 2, 1, 'CN=SMP_0112992001,O=DIGIT,C=BE', null, 'CERTIFICATE', 'REST_API',  NOW(),  NOW()),
(14, 2, 1, 'CN=EHEALTH_z_ẞ_W_,O=European_z_ẞ_W_Commission,C=BE:f71ee8b11cb3b787', null, 'CERTIFICATE', 'REST_API',  NOW(),  NOW());

insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, SUBJECT, ISSUER, SERIALNUMBER,VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values
(8, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:f71ee8b11cb3b787','CN=EHEALTH_SMP_EC,O=European Commission,C=BE','CN=EHEALTH_SMP_EC,O=European Commission,`C=BE','f71ee8b11cb3b787', date_add(NOW(),interval -1 year), date_add(NOW(),interval 1 year), NOW(), NOW()),
(9, 'CN=EHEALTH&SMP_EC,O=European&Commission,C=B&E:f71ee8b11cb3b787','CN=EHEALTH&SMP_EC,O=European&Commission,C=B&E','CN=EHEALTH&SMP_EC,O=European&Commission,C=B&E','f71ee8b11cb3b787', date_add(NOW(),interval -1 year), date_add(NOW(),interval 1 year), NOW(), NOW()),
(10, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:000000000000100f','CN=EHEALTH_SMP_EC,O=European Commission,C=BE','CN=EHEALTH_SMP_EC,O=European Commission,C=BE','000000000000100f', date_add(NOW(),interval -1 year), date_add(NOW(),interval 1 year), NOW(), NOW()),
(11, 'CN=blue_gw,O=eDelivery,C=BE:E07B6b956330a19a','CN=EHEALTH_SMP_EC,O=European Commission,C=BE','CN=EHEALTH_SMP_EC,O=European Commission,C=BE','E07B6b956330a19a', date_add(NOW(),interval -1 year), date_add(NOW(),interval 1 year), NOW(), NOW()),
(12, 'CN=red_gw,O=eDelivery,C=BE:9792ce69BC89F14C','CN=red_gw,O=eDelivery,C=BE','CN=red_gw,O=eDelivery,C=BE','9792ce69BC89F14C', date_add(NOW(),interval -1 year), date_add(NOW(),interval 1 year), NOW(), NOW()),
(13, 'CN=SMP_0112992001,O=DIGIT,C=BE','CN=SMP_0112992001,O=DIGIT,C=BE','CN=SMP_0112992001,O=DIGIT,C=BE','', date_add(NOW(),interval -1 year), date_add(NOW(),interval 1 year), NOW(), NOW()),
(14, 'CN=EHEALTH_z_ẞ_W_,O=European_z_ẞ_W_Commission,C=BE:f71ee8b11cb3b787','CN=EHEALTH_z_ẞ_W_,O=European_z_ẞ_W_Commission,C=BE','CN=EHEALTH_z_ẞ_W_,O=European_z_ẞ_W_Commission,C=BE','f71ee8b11cb3b787', date_add(NOW(),interval -1 year), date_add(NOW(),interval 1 year), NOW(), NOW());


insert into SMP_DOMAIN (ID, DOMAIN_CODE, VISIBILITY, SML_SUBDOMAIN, SML_SMP_ID, SIGNATURE_KEY_ALIAS, SML_CLIENT_CERT_AUTH,SML_REGISTERED, CREATED_ON, LAST_UPDATED_ON) values
(1, 'testdomain','PUBLIC', 'test-domain', 'CEF-SMP-002','sample_key',1,0, NOW(),  NOW());

insert into SMP_EXTENSION ( ID, IDENTIFIER,  IMPLEMENTATION_NAME, NAME, VERSION, DESCRIPTION, CREATED_ON, LAST_UPDATED_ON) values
(1, 'edelivery-oasis-smp-extension',  'OasisSMPExtension','Oasis SMP 1.0 and 2.0','1.0', 'Oasis SMP 1.0 and 2.0 extension',  NOW(),  NOW());

insert into SMP_RESOURCE_DEF ( ID, FK_EXTENSION_ID, URL_SEGMENT, IDENTIFIER, DESCRIPTION, MIME_TYPE, NAME, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 'smp-1', 'edelivery-oasis-smp-1.0-servicegroup', 'Service group', 'text/xml','Oasis SMP ServiceGroup', NOW(),  NOW());

insert into SMP_SUBRESOURCE_DEF (ID,FK_RESOURCE_DEF_ID,URL_SEGMENT, IDENTIFIER, DESCRIPTION, MIME_TYPE, NAME, CREATED_ON, LAST_UPDATED_ON) values
(1,1, 'services', 'edelivery-oasis-smp-1.0-servicemetadata', 'ServiceMetadata', 'text/xml','Oasis SMP ServiceMetadata', NOW(),  NOW());

insert into SMP_DOMAIN_RESOURCE_DEF (ID, FK_RESOURCE_DEF_ID, FK_DOMAIN_ID,CREATED_ON, LAST_UPDATED_ON ) values
(1, 1, 1, NOW(),  NOW());


insert into SMP_DOCUMENT (ID, CURRENT_VERSION, MIME_TYPE, NAME,CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 'text/xml', 'service-group', NOW(),  NOW());

insert into SMP_DOCUMENT_VERSION (ID, FK_DOCUMENT_ID, VERSION, DOCUMENT_CONTENT, CREATED_ON, LAST_UPDATED_ON) values
(1,1,  1, '<ServiceGroup xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05"><ParticipantIdentifier scheme="iso6523-actorid-upis">0088:777002abzz777</ParticipantIdentifier><ServiceMetadataReferenceCollection/></ServiceGroup>' , NOW(),  NOW());

insert into SMP_DOCUMENT (ID, CURRENT_VERSION, MIME_TYPE, NAME,CREATED_ON, LAST_UPDATED_ON) values
(2, 1, 'text/xml', 'service-metadta', NOW(),  NOW());

insert into SMP_DOCUMENT_VERSION (ID, FK_DOCUMENT_ID, VERSION, DOCUMENT_CONTENT, CREATED_ON, LAST_UPDATED_ON) values
(2,2,  1, '<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05"><Redirect href="http://localhost:8080/url"><CertificateUID/></Redirect></ServiceMetadata>' , NOW(),  NOW());

insert into SMP_RESOURCE ( ID, FK_DOCUMENT_ID, FK_DOREDEF_ID,  IDENTIFIER_SCHEME, IDENTIFIER_VALUE, SML_REGISTERED, VISIBILITY, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 'iso6523-actorid-upis', '0088:777002abzz777', 0, 'PUBLIC', NOW(),  NOW()),
(2, 1, 1, 'iso6523-actorid-upis', '0088:01', 0, 'PUBLIC', NOW(),  NOW()),
(3, 1, 1, 'iso6523-actorid-upis', '0088:02', 0, 'PUBLIC', NOW(),  NOW()),
(4, 1, 1, 'iso6523-actorid-upis', '0088:03', 0, 'PUBLIC', NOW(),  NOW()),
(5, 1, 1, 'iso6523-actorid-upis', '0088:04', 0, 'PUBLIC', NOW(),  NOW()),
(6, 1, 1, 'iso6523-actorid-upis', '0088:05', 0, 'PUBLIC', NOW(),  NOW()),
(7, 1, 1, 'iso6523-actorid-upis', '0088:06', 0, 'PUBLIC', NOW(),  NOW()),
(8, 1, 1, 'iso6523-actorid-upis', '0088:07', 0, 'PUBLIC', NOW(),  NOW()),
(9, 1, 1, 'iso6523-actorid-upis', '0088:08', 0, 'PUBLIC', NOW(),  NOW()),
(10, 1, 1, 'iso6523-actorid-upis', '0088:09', 0, 'PUBLIC', NOW(),  NOW()),
(11, 1, 1, 'iso6523-actorid-upis', '0088:10', 0, 'PUBLIC', NOW(),  NOW()),
(12, 1, 1, 'iso6523-actorid-upis', '0088:11', 0, 'PUBLIC', NOW(),  NOW()),
(13, 1, 1, 'iso6523-actorid-upis', '0088:12', 0, 'PUBLIC', NOW(),  NOW()),
(14, 1, 1, 'iso6523-actorid-upis', '0088:13', 0, 'PUBLIC', NOW(),  NOW()),
(15, 1, 1, 'iso6523-actorid-upis', '0088:14', 0, 'PUBLIC', NOW(),  NOW()),
(16, 1, 1, 'iso6523-actorid-upis', '0088:15', 0, 'PUBLIC', NOW(),  NOW()),
(17, 1, 1, 'iso6523-actorid-upis', '0088:16', 0, 'PUBLIC', NOW(),  NOW()),
(18, 1, 1, 'iso6523-actorid-upis', '0088:17', 0, 'PUBLIC', NOW(),  NOW()),
(19, 1, 1, 'iso6523-actorid-upis', '0088:18', 0, 'PUBLIC', NOW(),  NOW()),
(20, 1, 1, 'iso6523-actorid-upis', '0088:19', 0, 'PUBLIC', NOW(),  NOW())
;

insert into SMP_SUBRESOURCE (ID, FK_RESOURCE_ID,FK_SUREDEF_ID, FK_DOCUMENT_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 2, 'service-value', 'service-schema', NOW(),  NOW());

insert into SMP_SUBRESOURCE (ID, FK_RESOURCE_ID,FK_SUREDEF_ID, FK_DOCUMENT_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME, CREATED_ON, LAST_UPDATED_ON) values
(2, 1, 1, 2, 'service-value2', 'service-schema2', NOW(),  NOW());


insert into SMP_GROUP (ID, FK_DOMAIN_ID, NAME, VISIBILITY, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 'Test group', 'PUBLIC', NOW(),  NOW());

insert into  SMP_GROUP_RESOURCE (FK_GROUP_ID, FK_RESOURCE_ID) values
(1, 1);

insert into SMP_RESOURCE_MEMBER (ID, FK_RESOURCE_ID, FK_USER_ID, MEMBERSHIP_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 2, 'ADMIN', NOW(),  NOW());

insert into SMP_GROUP_MEMBER (ID, FK_GROUP_ID, FK_USER_ID, MEMBERSHIP_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 2, 'ADMIN', NOW(),  NOW()),
(2, 1, 3, 'ADMIN', NOW(),  NOW()),
(3, 1, 4, 'ADMIN', NOW(),  NOW());

insert into SMP_DOMAIN_MEMBER (ID, FK_DOMAIN_ID, FK_USER_ID, MEMBERSHIP_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 'ADMIN', NOW(),  NOW()),
(2, 1, 2, 'VIEWER', NOW(),  NOW());
