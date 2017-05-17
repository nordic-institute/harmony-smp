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

ALTER TABLE smp_endpoint DROP FOREIGN KEY FK_smp_endpoint_documentIdentifierScheme;
ALTER TABLE smp_ownership DROP FOREIGN KEY FK_smp_ownership_username;
ALTER TABLE smp_ownership DROP FOREIGN KEY FK_smp_ownership_businessIdentifier;
ALTER TABLE smp_process DROP FOREIGN KEY FK_smp_process_documentIdentifierScheme;
ALTER TABLE smp_service_metadata DROP FOREIGN KEY FK_smp_service_metadata_businessIdentifier;
DROP TABLE smp_endpoint;
DROP TABLE smp_ownership;
DROP TABLE smp_process;
DROP TABLE smp_service_metadata;
DROP TABLE smp_user;
DROP TABLE smp_service_metadata_red;
DROP TABLE smp_service_group;