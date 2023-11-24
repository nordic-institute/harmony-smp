CONNECT smp/test@//localhost:1521/xe;
DELETE FROM SMP_RESOURCE_MEMBER_AUD;
DELETE FROM SMP_DOCUMENT_AUD;
DELETE FROM SMP_SUBRESOURCE_AUD;
DELETE FROM SMP_SG_EXTENSION_AUD;
DELETE FROM SMP_RESOURCE_DOMAIN_AUD;
DELETE FROM SMP_RESOURCE_AUD ;
DELETE FROM SMP_DOMAIN_AUD;
DELETE FROM SMP_CERTIFICATE_AUD ;
DELETE FROM SMP_USER_AUD;
DELETE FROM SMP_REV_INFO;


--DELETE FROM SMP_CONFIGURATION;
DELETE FROM SMP_RESOURCE_MEMBER;
DELETE FROM SMP_DOCUMENT;
DELETE FROM SMP_SUBRESOURCE;
DELETE FROM SMP_SG_EXTENSION;
DELETE FROM SMP_RESOURCE_DOMAIN;
DELETE FROM SMP_RESOURCE;
DELETE FROM SMP_DOMAIN;
DELETE FROM SMP_CERTIFICATE;
DELETE FROM SMP_USER;
DELETE FROM SMP_RESOURCE_MEMBER;

set define off;

insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'peppol_user', '$2a$10$.pqNZZ4fRDdNbLhNlnEYg.1/d4yAGpLDgeXpJFI0sw7.WtyKphFzu','peppol_user', '$2a$10$.pqNZZ4fRDdNbLhNlnEYg.1/d4yAGpLDgeXpJFI0sw7.WtyKphFzu', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'the_admin', '','the_admin', '', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'AdminSMP1TEST', '$2a$06$u6Hym7Zrbsf4gEIeAsJRceK.Kg7tei3kDypwucQQdky0lXOLCkrCO','LvglqPCs', '$2a$10$zaFAFqFIfLUZx15ZDPMvDeWBtsZLaAkrY3Vmya5e3/yCCkFq/FJCu', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'AdminSMP2TEST', '$2a$10$h8Q3Kjbs6ZrGkU6ditjNueINlJOMDJ/g/OKiqFZy32WmdhLjV5TAi','VIhnrCJK', '$2a$10$BtInQBIycY2BSN28PD7TxO9ipAR3lhxUT2FLeShptGmjt6HaLpR7O', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'test', '','test', '', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'test1', '$2a$06$toKXJgjqQINZdjQqSao3NeWz2n1S64PFPhVU1e8gIHh4xdbwzy1Uy','test1', '$2a$06$toKXJgjqQINZdjQqSao3NeWz2n1S64PFPhVU1e8gIHh4xdbwzy1Uy', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36','system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SYSTEM_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'smp', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36','smp', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ACCESS_TOKEN_ID, ACCESS_TOKEN, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36','user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SERVICE_GROUP_ADMIN', 1, sysdate, sysdate);


insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'EHEALTH_SMP_EC', '', 'SERVICE_GROUP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:f71ee8b11cb3b787', null,null, sysdate, sysdate);


insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'EHEALTH_z_ẞ_W_,O', '', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=EHEALTH_z_ẞ_W_,O=European_z_ẞ_W_Commission,C=BE:f71ee8b11cb3b787', null,null,sysdate, sysdate);


insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'EHEALTH_SMP_1000000007-1', '', 'SERVICE_GROUP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD', null,null, sysdate, sysdate);


insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'EHEALTH_SMP_1000000007-2', '', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE', null,null, sysdate, sysdate);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'EHEALTH&SMP_EC', '', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=EHEALTH&SMP_EC,O=European&Commission,C=B&E:f71ee8b11cb3b787', null,null, sysdate, sysdate);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'EHEALTH_SMP_EC2', '', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:000000000000100f', null,null, sysdate, sysdate);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'SMP_1000000007-3', '', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=SMP_1000000007,O=DG-DIGIT,C=BE', null,null, sysdate, sysdate);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'SMP_1000000007-4', '', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD', null,null, sysdate, sysdate);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'SMP_1000000181,O=DIGIT,C=DK:123456789', '$2a$10$v2d/2E99dWHBM2ipTIip1enyaRKBTi.Xj/Iz0K8g0gjHBWdKRsHaC', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=SMP_1000000181,O=DIGIT,C=DK:123456789', null,null, sysdate, sysdate);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'red_gw', '', 'SERVICE_GROUP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=red_gw,O=eDelivery,C=BE:9792CE69BC89F14C', null,null,sysdate, sysdate);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'blue_gw', '', 'SERVICE_GROUP_ADMIN', 1, sysdate, sysdate);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.CURRVAL, 'CN=blue_gw,O=eDelivery,C=BE:e07b6b956330a19a', null,null,sysdate, sysdate);


-- insert domain
insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SIGNATURE_KEY_ALIAS,SML_BLUE_COAT_AUTH, SML_REGISTERED,  CREATED_ON, LAST_UPDATED_ON) values (SMP_DOMAIN_SEQ.nextval, 'domain','subdomain','sample_key',1,0, sysdate, sysdate);