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

ALTER TABLE smp_endpoint MODIFY (documentIdentifierScheme VARCHAR(100));
ALTER TABLE smp_endpoint MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_ownership MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_process MODIFY (documentIdentifierScheme VARCHAR(100));
ALTER TABLE smp_process MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_service_group MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_service_metadata MODIFY (documentIdentifierScheme VARCHAR(100));
ALTER TABLE smp_service_metadata MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_service_metadata_red MODIFY (documentIdentifierScheme VARCHAR(100));
ALTER TABLE smp_service_metadata_red MODIFY (businessIdentifierScheme VARCHAR(100));
