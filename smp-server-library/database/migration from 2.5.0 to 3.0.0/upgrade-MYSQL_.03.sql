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

ALTER TABLE smp_user ADD isadmin TINYINT(1) DEFAULT 0 NOT NULL;

DROP TRIGGER IF EXISTS smp_user_check_is_admin_value_before_insert;

DROP TRIGGER IF EXISTS smp_user_check_is_admin_value_before_update;

delimiter //
CREATE TRIGGER smp_user_check_is_admin_value_before_insert BEFORE INSERT ON smp_user
FOR EACH ROW
BEGIN
	IF NEW.ISADMIN <> 0 AND NEW.ISADMIN <> 1 THEN
		SIGNAL SQLSTATE '99999'
		SET MESSAGE_TEXT = '0 or 1 are the only allowed values for ISADMIN column';
	END IF;
END //

CREATE TRIGGER smp_user_check_is_admin_value_before_update BEFORE UPDATE ON smp_user
FOR EACH ROW
BEGIN
	IF NEW.ISADMIN <> 0 AND NEW.ISADMIN <> 1 THEN
		SIGNAL SQLSTATE '99999'
		SET MESSAGE_TEXT = '0 or 1 are the only allowed values for ISADMIN column';
	END IF;
END //
delimiter ;
