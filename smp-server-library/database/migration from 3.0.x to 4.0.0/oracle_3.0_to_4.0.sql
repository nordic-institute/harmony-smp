-- Copyright 2018 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the Licence for the specific language governing permissions and limitations under the Licence.

CREATE TABLE smp_domain (
  domainId              VARCHAR(50),
  bdmslClientCertHeader VARCHAR(4000),
  bdmslClientCertAlias  VARCHAR(50),
  signatureCertAlias    VARCHAR(50),
  PRIMARY KEY(domainId),
  CONSTRAINT check_max_one_auth CHECK (
    NOT (bdmslClientCertAlias IS NOT NULL AND bdmslClientCertHeader IS NOT NULL)
  )
);

INSERT INTO smp_domain(domainId) VALUES('default');

ALTER TABLE smp_service_group ADD (
  domainId  VARCHAR(50) DEFAULT 'default' NOT NULL
);

ALTER TABLE smp_service_group ADD (
  CONSTRAINT
    FK_srv_group_domain FOREIGN KEY (domainId)
    REFERENCES smp_domain (domainId)
);


commit;