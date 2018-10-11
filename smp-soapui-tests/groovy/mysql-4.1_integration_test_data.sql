-- Copyright 2018 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (1, 'peppol_user', '$2a$10$.pqNZZ4fRDdNbLhNlnEYg.1/d4yAGpLDgeXpJFI0sw7.WtyKphFzu', 'SMP_ADMIN', 1);
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (2, 'the_admin', '', 'SMP_ADMIN', 1);
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (3, 'AdminSMP1TEST', '$2a$06$u6Hym7Zrbsf4gEIeAsJRceK.Kg7tei3kDypwucQQdky0lXOLCkrCO', 'SMP_ADMIN', 1);
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (4, 'AdminSMP2TEST', '$2a$10$h8Q3Kjbs6ZrGkU6ditjNueINlJOMDJ/g/OKiqFZy32WmdhLjV5TAi', 'SMP_ADMIN', 1);
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (5, 'test', '', 'SMP_ADMIN', 1);
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (6, 'test1', '$2a$06$toKXJgjqQINZdjQqSao3NeWz2n1S64PFPhVU1e8gIHh4xdbwzy1Uy', 'SMP_ADMIN', 1);


insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (10, 'EHEALTH_SMP_EC', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (10, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:f71ee8b11cb3b787', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (11, 'EHEALTH_ż_ẞ_Ẅ_,O', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (11, 'CN=EHEALTH_ż_ẞ_Ẅ_,O=European_ż_ẞ_Ẅ_Commission,C=BE:f71ee8b11cb3b787', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (12, 'EHEALTH_SMP_1000000007', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (12, 'CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (13, 'EHEALTH_SMP_EC1', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (13, 'CN=EHEALTH_SMP_EC/emailAddress\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:f71ee8b11cb3b787', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (14, 'EHEALTH_SMP_1000000007', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (14, 'CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (15, 'EHEALTH&SMP_EC', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (15, 'CN=EHEALTH&SMP_EC,O=European&Commission,C=B&E:f71ee8b11cb3b787', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (16, 'EHEALTH_SMP_EC2', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (16, 'CN=EHEALTH_SMP_EC,O=European Commission,C=BE:000000000000100f', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (17, 'SMP_1000000007', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (17, 'CN=SMP_1000000007,O=DG-DIGIT,C=BE', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (18, 'SMP_1000000007', '', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (18, 'CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD', null,null);

insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE) values (19, 'SMP_1000000181,O=DIGIT,C=DK:123456789', '$2a$10$v2d/2E99dWHBM2ipTIip1enyaRKBTi.Xj/Iz0K8g0gjHBWdKRsHaC', 'SMP_ADMIN', 1);
insert into SMP_CERTIFICATE (ID, CERTIFICATE_ID, VALID_FROM, VALID_TO) values (19, 'CN=SMP_1000000181,O=DIGIT,C=DK:123456789', null,null);

-- insert domain
insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SIGNATURE_KEY_ALIAS) values (1, 'domain','subdomain','sig-key');

