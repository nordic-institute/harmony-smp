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
