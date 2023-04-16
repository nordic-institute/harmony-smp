insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 'system', 1, 'SYSTEM_ADMIN',  NOW(),  NOW()),
(2, 'user', 1, 'USER',  NOW(),  NOW());

insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 'system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'USERNAME_PASSWORD','UI',  NOW(),  NOW()),
(2, 2, 1, 'user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'USERNAME_PASSWORD','UI',  NOW(),  NOW());


insert into SMP_DOMAIN (ID, DOMAIN_CODE, VISIBILITY, SML_SUBDOMAIN, SML_SMP_ID, SIGNATURE_KEY_ALIAS, SML_CLIENT_CERT_AUTH,SML_REGISTERED, CREATED_ON, LAST_UPDATED_ON) values
(1, 'testdomain','PUBLIC', 'test-domain', 'DOMI-SMP-001','sample_key',1,0, NOW(),  NOW());
insert into SMP_GROUP (ID, FK_DOMAIN_ID, NAME, VISIBILITY, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 'Test group', 'PUBLIC', NOW(),  NOW());

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

insert into SMP_RESOURCE ( ID, FK_GROUP_ID, FK_DOCUMENT_ID, FK_DOREDEF_ID,  IDENTIFIER_SCHEME, IDENTIFIER_VALUE, SML_REGISTERED, VISIBILITY, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 1, 'iso6523-actorid-upis', '0088:777002abzz777', 0, 'PUBLIC', NOW(),  NOW());

insert into SMP_SUBRESOURCE (ID, FK_RESOURCE_ID,FK_SUREDEF_ID, FK_DOCUMENT_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 2, 'service-value', 'service-schema', NOW(),  NOW());

insert into SMP_SUBRESOURCE (ID, FK_RESOURCE_ID,FK_SUREDEF_ID, FK_DOCUMENT_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME, CREATED_ON, LAST_UPDATED_ON) values
(2, 1, 1, 2, 'service-value2', 'service-schema2', NOW(),  NOW());

insert into SMP_RESOURCE_MEMBER (ID, FK_GROUP_ID, FK_RESOURCE_ID, FK_USER_ID, MEMBERSHIP_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 2, 'ADMIN', NOW(),  NOW());

insert into SMP_GROUP_MEMBER (ID, FK_GROUP_ID, FK_USER_ID, MEMBERSHIP_ROLE, CREATED_ON, LAST_UPDATED_ON) values
(1, 1, 1, 'ADMIN', NOW(),  NOW()),
(2, 1, 2, 'ADMIN', NOW(),  NOW());
