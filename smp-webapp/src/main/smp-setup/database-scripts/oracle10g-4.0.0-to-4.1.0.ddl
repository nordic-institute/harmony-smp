--
-- Copyright 2018 European Commission | CEF eDelivery
--
-- Licensed under the EUPL, Version 1.2;
-- You may not use this work except in compliance with the Licence.
--
-- You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
--
-- Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the Licence for the specific language governing permissions and limitations under the Licence.

CREATE TABLE smp_domain_AUD (
  domainId              VARCHAR(50),
  bdmslClientCertHeader VARCHAR(4000),
  bdmslClientCertAlias  VARCHAR(50),
  bdmslSmpId            VARCHAR(50),
  signatureCertAlias    VARCHAR(50),
  REV INTEGER NOT NULL, 
  REVTYPE NUMBER(3),
  CONSTRAINT PK_SMP_DOMAIN_AUD PRIMARY KEY(domainId, REV)
);

CREATE TABLE smp_service_group_AUD (
  xmlContent                CLOB,
  businessIdentifier       VARCHAR(50),
  businessIdentifierScheme VARCHAR(100),
  domainId                 VARCHAR(50),
  REV INTEGER NOT NULL, 
  REVTYPE NUMBER(3),
  CONSTRAINT PK_SMP_GRP_AUD PRIMARY KEY (businessIdentifier, businessIdentifierScheme, REV)
);

CREATE TABLE smp_service_metadata_AUD (
  documentIdentifierScheme VARCHAR(100),
  businessIdentifier       VARCHAR(50) ,
  businessIdentifierScheme VARCHAR(100),
  documentIdentifier       VARCHAR(500),
  xmlcontent               CLOB,
  REV INTEGER NOT NULL, 
  REVTYPE NUMBER(3),
  CONSTRAINT PK_SMP_SMD_AUD PRIMARY KEY (
    documentIdentifierScheme,
    businessIdentifier,
    businessIdentifierScheme,
    documentIdentifier, REV)
);
 
CREATE TABLE smp_user_AUD (
  username VARCHAR(256),
  password VARCHAR(256),
  isadmin  NUMBER(1) DEFAULT 0,
  REV INTEGER NOT NULL, 
  REVTYPE NUMBER(3),
  CONSTRAINT PK_SMP_USER_AUD PRIMARY KEY (username, REV)
);

CREATE TABLE smp_ownership_AUD (
  username                 VARCHAR(256),
  businessIdentifier       VARCHAR(50),
  businessIdentifierScheme VARCHAR(100),
  REV INTEGER NOT NULL, 
  REVTYPE NUMBER(3),
  CONSTRAINT PK_OWNERSHIP_AUD PRIMARY KEY (username, businessIdentifier, businessIdentifierScheme, REV)
);


CREATE TABLE SMP_REV_INFO (
  ID NUMBER(38, 0) NOT NULL, 
  TIMESTAMP NUMBER(38, 0), 
  REVISION_DATE TIMESTAMP,
  username VARCHAR2(255), 
  CONSTRAINT PK_SMP_REV_INFO PRIMARY KEY (ID)
);
CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9999999999999999999999999999 CACHE 20 NOORDER;

commit;

