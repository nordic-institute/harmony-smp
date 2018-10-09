--
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
  bdmslSmpId            VARCHAR(50) NOT NULL,
  signatureCertAlias    VARCHAR(50),
  PRIMARY KEY(domainId),
  CONSTRAINT check_max_one_auth CHECK (
    NOT (bdmslClientCertAlias IS NOT NULL AND bdmslClientCertHeader IS NOT NULL)
  )
);

CREATE TABLE smp_service_group (
  xmlContent                CLOB,
  businessIdentifier       VARCHAR(50)  NOT NULL,
  businessIdentifierScheme VARCHAR(100) NOT NULL,
  domainId                 VARCHAR(50)  DEFAULT 'domain1' NOT NULL,
  PRIMARY KEY (businessIdentifier, businessIdentifierScheme),
  CONSTRAINT
    FK_srv_group_domain FOREIGN KEY (domainId)
    REFERENCES smp_domain (domainId)
);

CREATE TABLE smp_service_metadata (
  documentIdentifierScheme VARCHAR(100) NOT NULL,
  businessIdentifier       VARCHAR(50)  NOT NULL,
  businessIdentifierScheme VARCHAR(100) NOT NULL,
  documentIdentifier       VARCHAR(500) NOT NULL,
  xmlcontent               CLOB,
  PRIMARY KEY (
    documentIdentifierScheme,
    businessIdentifier,
    businessIdentifierScheme,
    documentIdentifier),
  CONSTRAINT
    FK_srv_metadata_srv_group FOREIGN KEY (
    businessIdentifier, businessIdentifierScheme)
  REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme)
);

CREATE TABLE smp_user (
  username VARCHAR(256),
  password VARCHAR(256),
  isadmin  NUMBER(1) DEFAULT 0 NOT NULL,
  PRIMARY KEY (username),
  CONSTRAINT check_is_admin_value CHECK (isadmin = 0 OR isadmin = 1)
);

CREATE TABLE smp_ownership (
  username                 VARCHAR(256) NOT NULL,
  businessIdentifier       VARCHAR(50)  NOT NULL,
  businessIdentifierScheme VARCHAR(100) NOT NULL,
  PRIMARY KEY (username, businessIdentifier, businessIdentifierScheme),
  CONSTRAINT FK_ownership_user FOREIGN KEY (username) REFERENCES smp_user (username),
  CONSTRAINT FK_ownership_service_group FOREIGN KEY (
    businessIdentifier, businessIdentifierScheme)
  REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme)
);


INSERT INTO smp_domain(domainId, bdmslSmpId) VALUES('domain1', 'DEFAULT-SMP-ID');
-- default admin user with password "changeit"
INSERT INTO smp_user(username, password, isadmin) VALUES ('smp_admin', '$2a$10$SZXMo7K/wA.ULWxH7uximOxeNk4mf3zU6nxJx/2VfKA19QlqwSpNO', '1');

commit;