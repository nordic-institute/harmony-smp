insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (1, 'system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SYSTEM_ADMIN', 1, NOW(), NOW());
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (2, 'smp', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SMP_ADMIN', 1, NOW(), NOW());
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (3, 'user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SERVICE_GROUP_ADMIN', 1, NOW(), NOW());

update SMP_USER_SEQ set next_val=4;


-- insert domain
insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SML_SMP_ID, SIGNATURE_KEY_ALIAS, SML_BLUE_COAT_AUTH,SML_REGISTERED, CREATED_ON, LAST_UPDATED_ON) values (1, 'testDomain','test','CEF-SMP-002', 'sample_key', 1,0, NOW(), NOW());
update SMP_USER_SEQ set next_val=2;
