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


CREATE TABLE smp_endpoint (
certificate CLOB NOT NULL, 
extension CLOB, 
minimumAuthenticationLevel VARCHAR(256), 
requireBusinessLevelSignature NUMBER(3) default '0' NOT NULL, 
serviceActivationDate DATE, 
serviceDescription CLOB NOT NULL, 
serviceExpirationDate DATE NOT NULL, 
technicalContactUrl VARCHAR(256) NOT NULL,
technicalInformationUrl VARCHAR(256), 
documentIdentifierScheme VARCHAR(100) NOT NULL,
processIdentifier VARCHAR(200) NOT NULL, 
businessIdentifier VARCHAR(50) NOT NULL, 
businessIdentifierScheme VARCHAR(100) NOT NULL,
endpointReference VARCHAR(256) NOT NULL, 
documentIdentifier VARCHAR(500) NOT NULL, 
processIdentifierType VARCHAR(25) NOT NULL, 
transportProfile VARCHAR(256) NOT NULL, 
PRIMARY KEY(
documentIdentifierScheme, 
processIdentifier, 
businessIdentifier, 
businessIdentifierScheme, 
endpointReference, 
documentIdentifier, 
processIdentifierType, 
transportProfile)
);

CREATE TABLE smp_ownership (
username VARCHAR(256) NOT NULL, 
businessIdentifier VARCHAR(50) NOT NULL, 
businessIdentifierScheme VARCHAR(100) NOT NULL,
PRIMARY KEY (username, businessIdentifier, businessIdentifierScheme));

CREATE TABLE smp_process (
extension CLOB, 
documentIdentifierScheme VARCHAR(100) NOT NULL,
processIdentifier VARCHAR(200) NOT NULL, 
businessIdentifier VARCHAR(50) NOT NULL, 
businessIdentifierScheme VARCHAR(100) NOT NULL,
documentIdentifier VARCHAR(500) NOT NULL, 
processIdentifierType VARCHAR(25) NOT NULL, 
PRIMARY KEY (
documentIdentifierScheme, 
processIdentifier, 
businessIdentifier, 
businessIdentifierScheme, 
documentIdentifier, processIdentifierType));

CREATE TABLE smp_service_group (
extension CLOB, 
businessIdentifier VARCHAR(50) NOT NULL, 
businessIdentifierScheme VARCHAR(100) NOT NULL,
PRIMARY KEY (businessIdentifier, businessIdentifierScheme));

CREATE TABLE smp_service_metadata (
extension CLOB, 
documentIdentifierScheme VARCHAR(100) NOT NULL,
businessIdentifier VARCHAR(50) NOT NULL, 
businessIdentifierScheme VARCHAR(100) NOT NULL,
documentIdentifier VARCHAR(500) NOT NULL,
xmlcontent CLOB,
PRIMARY KEY (
documentIdentifierScheme, 
businessIdentifier, 
businessIdentifierScheme, 
documentIdentifier));

CREATE TABLE smp_user (
username VARCHAR(256), 
password VARCHAR(256),
isadmin NUMBER(1) DEFAULT 0 NOT NULL,
PRIMARY KEY (username),
CONSTRAINT check_is_admin_value CHECK (isadmin = 0 OR isadmin = 1));

CREATE TABLE smp_service_metadata_red (
certificateUID VARCHAR(256) NOT NULL, 
extension CLOB, 
redirectionUrl VARCHAR(256) NOT NULL, 
documentIdentifierScheme VARCHAR(100) NOT NULL,
businessIdentifier VARCHAR(50) NOT NULL, 
businessIdentifierScheme VARCHAR(100) NOT NULL,
documentIdentifier VARCHAR(500) NOT NULL, 
PRIMARY KEY (
documentIdentifierScheme, 
businessIdentifier, 
businessIdentifierScheme, 
documentIdentifier));

ALTER TABLE smp_endpoint ADD CONSTRAINT 
FK_smp_end_docIdentifierScheme FOREIGN KEY (
documentIdentifierScheme, processIdentifier, businessIdentifier, 
businessIdentifierScheme, documentIdentifier, processIdentifierType) 
REFERENCES smp_process (documentIdentifierScheme, processIdentifier, businessIdentifier, 
businessIdentifierScheme, documentIdentifier, processIdentifierType);

ALTER TABLE smp_ownership ADD CONSTRAINT 
FK_smp_ownership_username FOREIGN KEY (username) REFERENCES smp_user (username);

ALTER TABLE smp_ownership ADD CONSTRAINT 
FK_smp_ownership_businessId FOREIGN KEY(
businessIdentifier, businessIdentifierScheme) 
REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme);

ALTER TABLE smp_process ADD CONSTRAINT 
FK_smp_proc_docIdScheme FOREIGN KEY (
documentIdentifierScheme, 
businessIdentifier, 
businessIdentifierScheme, 
documentIdentifier) 
REFERENCES smp_service_metadata (
documentIdentifierScheme, businessIdentifier, 
businessIdentifierScheme, documentIdentifier);

ALTER TABLE smp_process DISABLE CONSTRAINT FK_smp_proc_docIdScheme;

ALTER TABLE smp_service_metadata ADD CONSTRAINT 
FK_smp_service_meta_busId FOREIGN KEY (
businessIdentifier, businessIdentifierScheme) 
REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme);
