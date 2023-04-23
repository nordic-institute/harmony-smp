insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SYSTEM_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'smp', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SMP_ADMIN', 1, sysdate, sysdate);
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (SMP_USER_SEQ.nextval, 'user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SERVICE_GROUP_ADMIN', 1, sysdate, sysdate);


-- insert domain
insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SIGNATURE_KEY_ALIAS, SML_CLIENT_KEY_ALIAS, SML_CLIENT_CERT_AUTH, SML_REGISTERED,  CREATED_ON, LAST_UPDATED_ON) values
(SMP_DOMAIN_SEQ.nextval, 'testDomain','domain','sample_key', 'smp_domain_01',1,0, sysdate, sysdate);
