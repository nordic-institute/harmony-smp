--
-- Version: MPL 1.1/EUPL 1.1
--
-- The contents of this file are subject to the Mozilla Public License Version
-- 1.1 (the "License"); you may not use this file except in compliance with
-- the License. You may obtain a copy of the License at:
-- http://www.mozilla.org/MPL/
--
-- Software distributed under the License is distributed on an "AS IS" basis,
-- WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
-- for the specific language governing rights and limitations under the
-- License.
--
-- The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
--
-- Alternatively, the contents of this file may be used under the
-- terms of the EUPL, Version 1.1 or - as soon they will be approved
-- by the European Commission - subsequent versions of the EUPL
-- (the "Licence"); You may not use this work except in compliance
-- with the Licence.
-- You may obtain a copy of the Licence at:
-- http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the Licence is distributed on an "AS IS" basis,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the Licence for the specific language governing permissions and
-- limitations under the Licence.
--
-- If you wish to allow use of your version of this file only
-- under the terms of the EUPL License and not to allow others to use
-- your version of this file under the MPL, indicate your decision by
-- deleting the provisions above and replace them with the notice and
-- other provisions required by the EUPL License. If you do not delete
-- the provisions above, a recipient may use your version of this file
-- under either the MPL or the EUPL License.
--


-- Not dumping tablespaces as no INFORMATION_SCHEMA.FILES table on this server
--


DROP TABLE IF EXISTS smp_service_group;
CREATE TABLE smp_service_group (
  businessIdentifier varchar(256) NOT NULL,
  businessIdentifierScheme varchar(256) NOT NULL,
  extension BLOB,
  PRIMARY KEY  (businessIdentifier,businessIdentifierScheme)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS smp_service_metadata;
CREATE TABLE smp_service_metadata (
  documentIdentifier varchar(256) NOT NULL,
  documentIdentifierScheme varchar(256) NOT NULL,
  businessIdentifier varchar(256) NOT NULL,
  businessIdentifierScheme varchar(256) NOT NULL,
  extension TEXT,
  xmlcontent TEXT,
  PRIMARY KEY  (documentIdentifier,documentIdentifierScheme,businessIdentifier,businessIdentifierScheme),
  KEY FK_service_metadata_1 (businessIdentifier,businessIdentifierScheme),
  CONSTRAINT FK_service_metadata_1 FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS smp_process;
CREATE TABLE smp_process (
  processIdentifier varchar(256) NOT NULL,
  processIdentifierType varchar(256) NOT NULL,
  extension BLOB,
  businessIdentifier varchar(256) NOT NULL,
  businessIdentifierScheme varchar(256) NOT NULL,
  documentIdentifier varchar(256) NOT NULL,
  documentIdentifierScheme varchar(256) NOT NULL,
  PRIMARY KEY  (processIdentifier,processIdentifierType,businessIdentifier,businessIdentifierScheme,documentIdentifier,documentIdentifierScheme),
  KEY FK_process_1 (documentIdentifier,documentIdentifierScheme,businessIdentifier,businessIdentifierScheme),
  CONSTRAINT FK_process_1 FOREIGN KEY (documentIdentifier, documentIdentifierScheme, businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_metadata (documentIdentifier, documentIdentifierScheme, businessIdentifier, businessIdentifierScheme) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS smp_user;
CREATE TABLE smp_user (
  username varchar(256) NOT NULL,
  password varchar(256),
  PRIMARY KEY  (username)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS smp_service_metadata_red;
CREATE TABLE smp_service_metadata_red (
  businessIdentifier varchar(256) NOT NULL,
  businessIdentifierScheme varchar(256) NOT NULL,
  documentIdentifier varchar(256) NOT NULL,
  documentIdentifierScheme varchar(256) NOT NULL,
  redirectionUrl varchar(256) NOT NULL,
  extension TEXT,
  certificateUID varchar(256) NOT NULL,
  PRIMARY KEY  (businessIdentifier,businessIdentifierScheme,documentIdentifier,documentIdentifierScheme)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS smp_endpoint;
CREATE TABLE smp_endpoint (
  transportProfile varchar(256) NOT NULL,
  endpointReference varchar(256) NOT NULL,
  extension BLOB,
  processIdentifier varchar(256) NOT NULL,
  processIdentifierType varchar(256) NOT NULL,
  businessIdentifier varchar(256) NOT NULL,
  businessIdentifierScheme varchar(256) NOT NULL,
  documentIdentifier varchar(256) NOT NULL,
  documentIdentifierScheme varchar(256) NOT NULL default '',
  requireBusinessLevelSignature bit(1) NOT NULL,
  minimumAuthenticationLevel varchar(256) default NULL,
  serviceActivationDate datetime default NULL,
  serviceExpirationDate datetime NOT NULL,
  certificate TEXT NOT NULL,
  serviceDescription BLOB NOT NULL,
  technicalContactUrl varchar(256) NOT NULL,
  technicalInformationUrl varchar(256) default NULL,
  PRIMARY KEY  (businessIdentifierScheme,businessIdentifier,processIdentifierType,processIdentifier,documentIdentifier,documentIdentifierScheme,endpointReference,transportProfile),
  KEY FK_endpoint_1 (processIdentifier,processIdentifierType,businessIdentifier,businessIdentifierScheme,documentIdentifier,documentIdentifierScheme),
  CONSTRAINT FK_endpoint_1 FOREIGN KEY (processIdentifier, processIdentifierType, businessIdentifier, businessIdentifierScheme, documentIdentifier, documentIdentifierScheme) REFERENCES smp_process (processIdentifier, processIdentifierType, businessIdentifier, businessIdentifierScheme, documentIdentifier, documentIdentifierScheme) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS smp_ownership;
CREATE TABLE smp_ownership (
  username varchar(256) NOT NULL,
  businessIdentifier varchar(256) NOT NULL,
  businessIdentifierScheme varchar(256) NOT NULL,
  KEY FK_ownership_1 (businessIdentifier,businessIdentifierScheme),
  KEY FK_ownership_2 (username),
  CONSTRAINT FK_ownership_2 FOREIGN KEY (username) REFERENCES smp_user (username) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_ownership_1 FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE smp_service_group
CHANGE COLUMN extension extension TEXT NULL DEFAULT NULL ;

ALTER TABLE smp_service_metadata
CHANGE COLUMN extension extension TEXT NULL DEFAULT NULL ;