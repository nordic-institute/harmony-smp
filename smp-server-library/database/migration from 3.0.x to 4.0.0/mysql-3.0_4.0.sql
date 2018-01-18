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
);


INSERT INTO smp_domain(domainId, bdmslSmpId) VALUES('default', 'DEFAULT-SMP-ID');



ALTER TABLE smp_service_group ADD
  domainId  VARCHAR(256)
            CHARACTER SET utf8
            COLLATE utf8_bin NOT NULL
            DEFAULT 'default';

ALTER TABLE smp_service_group ADD
  CONSTRAINT
    FK_srv_group_domain FOREIGN KEY (domainId)
    REFERENCES smp_domain (domainId);




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


commit;