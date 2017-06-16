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

LOCK TABLES
	smp_service_group WRITE, 
    smp_service_metadata WRITE,
    smp_ownership WRITE,
    smp_process WRITE;
ALTER TABLE smp_service_metadata DROP FOREIGN KEY FK_service_metadata_1;
ALTER TABLE smp_ownership DROP FOREIGN KEY FK_ownership_1;
ALTER TABLE smp_process DROP FOREIGN KEY FK_process_1;

-- tables locked, foreign keys dropped, updating columns:

ALTER TABLE smp_service_group MODIFY businessIdentifier VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_service_group MODIFY businessIdentifierScheme VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

ALTER TABLE smp_ownership MODIFY businessIdentifier VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_ownership MODIFY businessIdentifierScheme VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

ALTER TABLE smp_service_metadata MODIFY businessIdentifier VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_service_metadata MODIFY businessIdentifierScheme VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_service_metadata MODIFY documentIdentifier VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_service_metadata MODIFY documentIdentifierScheme VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

-- columns updated, recreate foreign keys and unlock tables:

ALTER TABLE smp_service_metadata ADD CONSTRAINT FK_service_metadata_1 FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE smp_ownership ADD CONSTRAINT FK_ownership_1 FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme) ON DELETE CASCADE ON UPDATE CASCADE;
UNLOCK TABLES;