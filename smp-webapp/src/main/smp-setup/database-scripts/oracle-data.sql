insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, EMAIL, CREATED_ON, LAST_UPDATED_ON) values
    (-1, 'system', 1, 'SYSTEM_ADMIN', 'system@mail-example.local', sysdate,  sysdate);
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
    (-1, -1, 1, 'system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'USERNAME_PASSWORD','UI',  sysdate,  sysdate);

insert into SMP_USER (ID, USERNAME, ACTIVE, APPLICATION_ROLE, EMAIL, CREATED_ON, LAST_UPDATED_ON) values
    (-2, 'user', 1, 'USER',  'user@mail-example.local',  sysdate,  sysdate);
insert into SMP_CREDENTIAL (ID, FK_USER_ID, CREDENTIAL_ACTIVE, CREDENTIAL_NAME, CREDENTIAL_VALUE, CREDENTIAL_TYPE, CREDENTIAL_TARGET, CREATED_ON, LAST_UPDATED_ON) values
    (-2, -2, 1, 'user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'USERNAME_PASSWORD','UI', sysdate,  sysdate);

-- insert domain
insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SIGNATURE_KEY_ALIAS, SML_CLIENT_KEY_ALIAS, SML_CLIENT_CERT_AUTH, SML_REGISTERED,  CREATED_ON, LAST_UPDATED_ON) values
(SMP_DOMAIN_SEQ.nextval, 'testDomain','domain','sample_key', 'smp_domain_01',1,0, sysdate, sysdate);
