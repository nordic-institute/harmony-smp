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
  domainId              VARCHAR(50) NOT NULL,
  bdmslClientCertHeader VARCHAR(4000) ,
  bdmslClientCertAlias  VARCHAR(50) ,
  bdmslSmpId            VARCHAR(50)  NOT NULL,
  signatureCertAlias    VARCHAR(50) ,
  PRIMARY KEY(domainId)
);


CREATE TABLE smp_domain_AUD (
  domainId              VARCHAR(50) NOT NULL,
  bdmslClientCertHeader VARCHAR(4000),
  bdmslClientCertAlias  VARCHAR(50),
  bdmslSmpId            VARCHAR(50) NOT NULL,
  signatureCertAlias    VARCHAR(50) NULL,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY(domainId, REV)
);



CREATE TABLE smp_service_group (
  businessIdentifier       VARCHAR(50) NOT NULL,
  businessIdentifierScheme VARCHAR(100) NOT NULL,
  domainId                 VARCHAR(50) DEFAULT 'domain1' NOT NULL ,
  xmlContent                TEXT             NULL DEFAULT NULL,
  PRIMARY KEY (businessIdentifier, businessIdentifierScheme),
  CONSTRAINT FK_srv_group_domain FOREIGN KEY (domainId)
    REFERENCES smp_domain (domainId)
);

CREATE TABLE smp_service_group_AUD (
  businessIdentifier       VARCHAR(50) NOT NULL,
  businessIdentifierScheme VARCHAR(100) NOT NULL,
  domainId                 VARCHAR(50) NOT NULL,
  xmlContent                TEXT             NULL DEFAULT NULL,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY (businessIdentifier, businessIdentifierScheme, REV)
);

CREATE TABLE smp_service_metadata (
  documentIdentifier       VARCHAR(500) NOT NULL,
  documentIdentifierScheme VARCHAR(100) NOT NULL,
  businessIdentifier       VARCHAR(50) NOT NULL,
  businessIdentifierScheme VARCHAR(100) NOT NULL,
  xmlcontent               TEXT,
  PRIMARY KEY (documentIdentifier, documentIdentifierScheme, businessIdentifier, businessIdentifierScheme),
  FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE smp_service_metadata_AUD (
  documentIdentifier       VARCHAR(500) NOT NULL,
  documentIdentifierScheme VARCHAR(100) NOT NULL,
  businessIdentifier       VARCHAR(50) NOT NULL,
  businessIdentifierScheme VARCHAR(100) NOT NULL,
  xmlcontent               TEXT,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY (documentIdentifier, documentIdentifierScheme, businessIdentifier, businessIdentifierScheme, REV)
);


CREATE TABLE smp_user (
  username VARCHAR(256)         NOT NULL,
  password VARCHAR(256),
  isadmin  TINYINT(1) DEFAULT 0 NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE smp_user_AUD (
  username VARCHAR(256)         NOT NULL,
  password VARCHAR(256),
  isadmin  TINYINT(1) DEFAULT 0 NOT NULL,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY (username, REV)
);


CREATE TABLE smp_ownership (
  username                 VARCHAR(256)     NOT NULL,
  businessIdentifier       VARCHAR(50) NOT NULL,
  businessIdentifierScheme VARCHAR(100),
  PRIMARY KEY (businessIdentifier, businessIdentifierScheme, username),
  FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (username) REFERENCES smp_user (username)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


CREATE TABLE smp_ownership_AUD (
  username                 VARCHAR(256)     NOT NULL,
  businessIdentifier       VARCHAR(50) NOT NULL,
  businessIdentifierScheme VARCHAR(100) NOT NULL,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY (username, businessIdentifier, businessIdentifierScheme, REV)
);


CREATE TABLE SMP_REV_INFO (
  ID INT AUTO_INCREMENT NOT NULL,
  TIMESTAMP BIGINT NULL,
  REVISION_DATE timestamp NULL,
  username VARCHAR(255) NULL,
  CONSTRAINT PK_SMP_REV_INFO PRIMARY KEY (ID)
);




create table hibernate_sequence(
    next_val BIGINT NOT NULL
);

INSERT INTO hibernate_sequence(next_val) values(1);

INSERT INTO smp_domain(domainId, bdmslSmpId) VALUES('domain1', 'DEFAULT-SMP-ID');
-- default admin user with password "changeit"
INSERT INTO smp_user(username, password, isadmin) VALUES ('smp_admin', '$2a$10$SZXMo7K/wA.ULWxH7uximOxeNk4mf3zU6nxJx/2VfKA19QlqwSpNO', '1');

commit;
