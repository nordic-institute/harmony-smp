
ALTER TABLE `smp`.`smp_service_group` 
CHANGE COLUMN `extension` `extension` TEXT NULL DEFAULT NULL ;

ALTER TABLE `smp`.`smp_service_metadata` 
CHANGE COLUMN `extension` `extension` TEXT NULL DEFAULT NULL ;