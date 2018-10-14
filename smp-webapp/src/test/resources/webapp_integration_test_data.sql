-- Copyright 2018 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (1, 'test_admin', '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW', 'SMP_ADMIN', 1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (2, 'test_user_hashed_pass',                     '$2a$06$AXSSUDJlpzzq/gPZb7eIBeb8Mi0.PTKqDjzujZH.bWPwj5.ePEInW', 'SMP_ADMIN',1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (3, 'test_user_clear_pass',                      'test123',                                                     'SMP_ADMIN',1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (4, 'cert1', '',                                                             'SMP_ADMIN', 1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (4, 'CN=comon name,O=org,C=BE:0000000000000066', null,null,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());

insert into SMP_USER(ID, USERNAME, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (5, 'cert2', 'SMP_ADMIN', 1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (5, 'CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', null,null,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());

insert into SMP_USER(ID, USERNAME, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (6, 'Cert2', 'SMP_ADMIN', 1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO, CREATED_ON, LAST_UPDATED_ON) values (6, 'CN=utf-8_ż_SMP,O=EC,C=BE:0000000000000666', null,null,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());

-- set the ids to higher values - tests are using sequnce which stars from 1
insert into SMP_SERVICE_GROUP(ID, PARTICIPANT_IDENTIFIER, PARTICIPANT_SCHEME,SML_REGISTRED, CREATED_ON, LAST_UPDATED_ON) values (100000, 'urn:australia:ncpb', 'ehealth-actorid-qns', 1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
insert into SMP_SERVICE_GROUP(ID, PARTICIPANT_IDENTIFIER, PARTICIPANT_SCHEME,SML_REGISTRED, CREATED_ON, LAST_UPDATED_ON) values (200000, 'urn:brazil:ncpb', 'ehealth-actorid-qns', 1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
--insert into SMP_SERVICE_GROUP(ID, PARTICIPANT_IDENTIFIER, PARTICIPANT_SCHEME,SML_REGISTRED) values (3, 'urn:poland:ncpb', 'ehealth-participantid-qns', 1);

insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SML_SMP_ID, SIGNATURE_KEY_ALIAS, CREATED_ON, LAST_UPDATED_ON) values (1, 'domain','subdomain', 'CEF-SMP-001','sig-key',CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());

 --insert into SMP_OWNERSHIP (FK_SG_ID, FK_USER_ID) values (3, 1);
--insert into SMP_SERVICE_GROUP_DOMAIN (ID, FK_SG_ID, FK_DOMAIN_ID) values (1, 3, 1);
-- insert into smp_ownership(username, businessidentifier, businessidentifierscheme) values ('CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', 'urn:australia:ncpb', 'ehealth-actorid-qns');



