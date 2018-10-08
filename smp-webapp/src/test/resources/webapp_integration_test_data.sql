-- Copyright 2018 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
delete from SMP_OWNERSHIP where FK_USER_ID in (1);
--delete from SMP_SERVICE_GROUP_DOMAIN where FK_SG_ID in (1, 2, 3);

delete from SMP_USER where ID in (1, 2,3,4,5,6);
delete from SMP_SERVICE_GROUP where ID in (1, 2, 3);
delete from SMP_DOMAIN where ID in (1);


insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (1, 'test_admin', '$2a$06$k.Q/6anG4Eq/nNTZ0C1UIuAKxpr6ra5oaMkMSrlESIyA5jKEsUdyS', 'ROLE_SMP_ADMIN', b'1');
insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (2, 'test_user_hashed_pass',                     '$2a$06$k.Q/6anG4Eq/nNTZ0C1UIuAKxpr6ra5oaMkMSrlESIyA5jKEsUdyS', 'ROLE_SMP_ADMIN', b'1');
insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (3, 'test_user_clear_pass',                      'gutek123',                                                     'ROLE_SMP_ADMIN', b'1');
insert into SMP_USER(ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (4, 'CN=comon name,O=org,C=BE:0000000000000066', '',                                                             'ROLE_SMP_ADMIN', b'1');
insert into SMP_USER(ID, USERNAME, ROLE, ACTIVE) values (5, 'CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', 'ROLE_SMP_ADMIN', b'1');
insert into SMP_USER(ID, USERNAME, ROLE, ACTIVE) values (6, 'CN=utf-8_ż_SMP,O=EC,C=BE:0000000000000666', 'ROLE_SMP_ADMIN', b'1');

insert into SMP_SERVICE_GROUP(ID, PARTICIPANT_IDENTIFIER, PARTICIPANT_SCHEME,SML_REGISTRED) values (1, 'urn:australia:ncpb', 'ehealth-actorid-qns',b'1');
insert into SMP_SERVICE_GROUP(ID, PARTICIPANT_IDENTIFIER, PARTICIPANT_SCHEME,SML_REGISTRED) values (2, 'urn:brazil:ncpb', 'ehealth-actorid-qns',b'1');
--insert into SMP_SERVICE_GROUP(ID, PARTICIPANT_IDENTIFIER, PARTICIPANT_SCHEME,SML_REGISTRED) values (3, 'urn:poland:ncpb', 'ehealth-participantid-qns',b'1');

insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SIGNATURE_KEY_ALIAS) values (1, 'domain','subdomain','sig-key');
--insert into SMP_OWNERSHIP (FK_SG_ID, FK_USER_ID) values (3, 1);
--insert into SMP_SERVICE_GROUP_DOMAIN (ID, FK_SG_ID, FK_DOMAIN_ID) values (1, 3, 1);
-- insert into smp_ownership(username, businessidentifier, businessidentifierscheme) values ('CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', 'urn:australia:ncpb', 'ehealth-actorid-qns');



