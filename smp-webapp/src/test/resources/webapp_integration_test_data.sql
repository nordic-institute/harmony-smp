--
-- Copyright 2017 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence at:
-- https://joinup.ec.europa.eu/software/page/eupl
-- or file: LICENCE-EUPL-v1.1.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the Licence for the specific language governing permissions and limitations under the Licence.


insert into smp_user(username, password, isadmin) values ('test_admin',                                '$2a$06$k.Q/6anG4Eq/nNTZ0C1UIuAKxpr6ra5oaMkMSrlESIyA5jKEsUdyS', 1);
insert into smp_user(username, password, isadmin) values ('test_user_hashed_pass',                     '$2a$06$k.Q/6anG4Eq/nNTZ0C1UIuAKxpr6ra5oaMkMSrlESIyA5jKEsUdyS', 0);
insert into smp_user(username, password, isadmin) values ('test_user_clear_pass',                      'gutek123',                                                     0);
insert into smp_user(username, password, isadmin) values ('CN=comon name,O=org,C=BE:0000000000000066', '',                                                             0);
insert into smp_user (username, isadmin) values ('CN=EHEALTH_SMP_EC,O=European Commission,C=BE:f71ee8b11cb3b787', 0);
insert into smp_user (username, isadmin) values ('CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', 0);

insert into smp_service_group(businessidentifier, businessidentifierscheme) values ('urn:australia:ncpb', 'ehealth-actorid-qns');
insert into smp_service_group(businessidentifier, businessidentifierscheme) values ('urn:brazil:ncpb', 'ehealth-actorid-qns');

insert into smp_ownership(username, businessidentifier, businessidentifierscheme) values ('CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08', 'urn:australia:ncpb', 'ehealth-actorid-qns');
insert into smp_ownership(username, businessidentifier, businessidentifierscheme) values ('CN=EHEALTH_SMP_EC,O=European Commission,C=BE:f71ee8b11cb3b787', 'urn:brazil:ncpb', 'ehealth-actorid-qns');



