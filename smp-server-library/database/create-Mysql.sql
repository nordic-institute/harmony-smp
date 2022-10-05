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


CREATE TABLE smp_service_group (
  businessIdentifier       VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifierScheme VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  extension                TEXT             NULL DEFAULT NULL,
  PRIMARY KEY (businessIdentifier, businessIdentifierScheme)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE smp_service_metadata (
  documentIdentifier       VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  documentIdentifierScheme VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifier       VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifierScheme VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  extension                TEXT             NULL DEFAULT NULL,
  xmlcontent               TEXT,
  PRIMARY KEY (documentIdentifier, documentIdentifierScheme, businessIdentifier, businessIdentifierScheme),
  KEY FK_service_metadata_service_group (businessIdentifier, businessIdentifierScheme),
  CONSTRAINT FK_service_metadata_service_group FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE smp_user (
  username VARCHAR(256)         NOT NULL,
  password VARCHAR(256),
  isadmin  TINYINT(1) DEFAULT 0 NOT NULL,
  PRIMARY KEY (username)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE smp_ownership (
  username                 VARCHAR(256)     NOT NULL,
  businessIdentifier       VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifierScheme VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  KEY FK_ownership_service_group (businessIdentifier, businessIdentifierScheme),
  KEY FK_ownership_user (username),
  CONSTRAINT FK_ownership_service_group FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT FK_ownership_user FOREIGN KEY (username) REFERENCES smp_user (username)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TRIGGER IF EXISTS smp_user_check_is_admin_value_before_insert;
DROP TRIGGER IF EXISTS smp_user_check_is_admin_value_before_update;
DELIMITER //
CREATE TRIGGER smp_user_check_is_admin_value_before_insert
BEFORE INSERT ON smp_user
FOR EACH ROW
  BEGIN
    IF NEW.ISADMIN <> 0 AND NEW.ISADMIN <> 1
    THEN
      SIGNAL SQLSTATE '99999'
      SET MESSAGE_TEXT = '0 or 1 are the only allowed values for ISADMIN column';
    END IF;
  END //
CREATE TRIGGER smp_user_check_is_admin_value_before_update
BEFORE UPDATE ON smp_user
FOR EACH ROW
  BEGIN
    IF NEW.ISADMIN <> 0 AND NEW.ISADMIN <> 1
    THEN
      SIGNAL SQLSTATE '99999'
      SET MESSAGE_TEXT = '0 or 1 are the only allowed values for ISADMIN column';
    END IF;
  END //
DELIMITER ;