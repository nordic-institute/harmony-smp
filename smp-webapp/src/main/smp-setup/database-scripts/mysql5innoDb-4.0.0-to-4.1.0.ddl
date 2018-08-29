-- Copyright 2018 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.2
--
-- You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the Licence for the specific language governing permissions and limitations under the Licence.

CREATE TABLE smp_domain_AUD (
  domainId              VARCHAR(50)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NOT NULL,
  bdmslClientCertHeader VARCHAR(4000)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NULL,
  bdmslClientCertAlias  VARCHAR(50)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NULL,
  bdmslSmpId            VARCHAR(50)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NOT NULL,
  signatureCertAlias    VARCHAR(50)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NULL,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY(domainId, REV)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE smp_service_group_AUD (
  businessIdentifier       VARCHAR(50)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifierScheme VARCHAR(100)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  domainId                 VARCHAR(50)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL
                           DEFAULT 'domain1',
  extension                TEXT             NULL DEFAULT NULL,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY (businessIdentifier, businessIdentifierScheme, REV)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE smp_service_metadata_AUD (
  documentIdentifier       VARCHAR(500)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  documentIdentifierScheme VARCHAR(100)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifier       VARCHAR(50)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifierScheme VARCHAR(100)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  xmlcontent               TEXT,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY (documentIdentifier, documentIdentifierScheme, businessIdentifier, businessIdentifierScheme, REV)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE smp_user_AUD (
  username VARCHAR(256)         NOT NULL,
  password VARCHAR(256),
  isadmin  TINYINT(1) DEFAULT 0 NOT NULL,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY (username, REV)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE smp_ownership_AUD (
  username                 VARCHAR(256)     NOT NULL,
  businessIdentifier       VARCHAR(50)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifierScheme VARCHAR(100)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  REV integer not null,
  REVTYPE tinyint,
  PRIMARY KEY (username, businessIdentifier, businessIdentifierScheme, REV)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE SMP_REV_INFO (
  ID INT AUTO_INCREMENT NOT NULL,
  TIMESTAMP BIGINT NULL,
  REVISION_DATE timestamp NULL,
  username VARCHAR(256)         NOT NULL,
  CONSTRAINT PK_SMP_REV_INFO PRIMARY KEY (ID)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



CREATE TABLE hibernate_sequence(
    next_val BIGINT NOT NULL
);

INSERT INTO hibernate_sequence(next_val) values(1

commit;
