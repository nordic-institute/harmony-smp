-- Copyright 2018 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.


insert into SMP_CONFIGURATION (PROPERTY, VALUE, CREATED_ON, LAST_UPDATED_ON) VALUES ('configuration.dir','./target/keystores/',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
insert into SMP_CONFIGURATION (PROPERTY, VALUE, CREATED_ON, LAST_UPDATED_ON) VALUES ('encryption.key.filename','encryptionKey.key',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
insert into SMP_CONFIGURATION (PROPERTY, VALUE, CREATED_ON, LAST_UPDATED_ON) VALUES ('smp.keystore.password', 'FarFJE2WUfY39SVRTFOqSg==',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
insert into SMP_CONFIGURATION (PROPERTY, VALUE, CREATED_ON, LAST_UPDATED_ON) VALUES ('smp.keystore.filename', 'smp-keystore.jks',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
insert into SMP_CONFIGURATION (PROPERTY, VALUE, CREATED_ON, LAST_UPDATED_ON) VALUES ('contextPath.output', 'true',CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
