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
  domainId              VARCHAR(256)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NOT NULL,
  bdmslClientCertHeader VARCHAR(4000)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NULL,
  bdmslClientCertAlias  VARCHAR(256)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NULL,
  bdmslSmpId            VARCHAR(256)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NOT NULL,
  signatureCertAlias    VARCHAR(256)
                        CHARACTER SET utf8
                        COLLATE utf8_bin NULL,
  PRIMARY KEY(domainId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE smp_service_group (
  businessIdentifier       VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  businessIdentifierScheme VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL,
  domainId                 VARCHAR(256)
                           CHARACTER SET utf8
                           COLLATE utf8_bin NOT NULL
                           DEFAULT 'default',
  extension                TEXT             NULL DEFAULT NULL,
  PRIMARY KEY (businessIdentifier, businessIdentifierScheme),
  CONSTRAINT FK_srv_group_domain FOREIGN KEY (domainId)
    REFERENCES smp_domain (domainId)
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


DROP TRIGGER IF EXISTS smp_domain_check_bdmsl_auth_before_insert;
DROP TRIGGER IF EXISTS smp_domain_check_bdmsl_auth_before_update;
DELIMITER //
CREATE TRIGGER smp_domain_check_bdmsl_auth_before_insert
BEFORE INSERT ON smp_domain
FOR EACH ROW
  BEGIN
    IF ((NEW.bdmslClientCertAlias > '' OR NEW.bdmslClientCertAlias = null) AND (NEW.bdmslClientCertHeader > '' OR NEW.bdmslClientCertHeader = null))
    THEN
      SIGNAL SQLSTATE '99999'
      SET MESSAGE_TEXT = 'Both BDMSL authentication ways cannot be switched ON at the same time: bdmslClientCertAlias and bdmslClientCertHeader';
    END IF;
  END //
CREATE TRIGGER smp_domain_check_bdmsl_auth_before_update
BEFORE UPDATE ON smp_domain
FOR EACH ROW
  BEGIN
    IF ((NEW.bdmslClientCertAlias > '' OR NEW.bdmslClientCertAlias = null) AND (NEW.bdmslClientCertHeader > '' OR NEW.bdmslClientCertHeader = null))
    THEN
      SIGNAL SQLSTATE '99999'
      SET MESSAGE_TEXT = 'Both BDMSL authentication ways cannot be switched ON at the same time: bdmslClientCertAlias and bdmslClientCertHeader';
    END IF;
  END //
DELIMITER ;


INSERT INTO smp_domain(domainId, bdmslSmpId) VALUES('default', 'DEFAULT-SMP-ID');
-- default admin user with password "changeit"
INSERT INTO smp_user(username, password, isadmin) VALUES ('smp_admin', '$2a$10$SZXMo7K/wA.ULWxH7uximOxeNk4mf3zU6nxJx/2VfKA19QlqwSpNO', '1');

commit;