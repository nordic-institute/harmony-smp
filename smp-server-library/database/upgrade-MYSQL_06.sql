LOCK TABLES 
	smp_service_group WRITE, 
    smp_service_metadata WRITE,
    smp_ownership WRITE,
    smp_process WRITE;
ALTER TABLE smp_service_metadata DROP FOREIGN KEY FK_service_metadata_1;
ALTER TABLE smp_ownership DROP FOREIGN KEY FK_ownership_1;
ALTER TABLE smp_process DROP FOREIGN KEY FK_process_1;

-- tables locked, foreign keys dropped, updating columns:

ALTER TABLE smp_service_group MODIFY businessIdentifier VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_service_group MODIFY businessIdentifierScheme VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

ALTER TABLE smp_ownership MODIFY businessIdentifier VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_ownership MODIFY businessIdentifierScheme VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

ALTER TABLE smp_service_metadata MODIFY businessIdentifier VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_service_metadata MODIFY businessIdentifierScheme VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_service_metadata MODIFY documentIdentifier VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
ALTER TABLE smp_service_metadata MODIFY documentIdentifierScheme VARCHAR(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

-- columns updated, recreate foreign keys and unlock tables:

ALTER TABLE smp_service_metadata ADD CONSTRAINT FK_service_metadata_1 FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE smp_ownership ADD CONSTRAINT FK_ownership_1 FOREIGN KEY (businessIdentifier, businessIdentifierScheme) REFERENCES smp_service_group (businessIdentifier, businessIdentifierScheme) ON DELETE CASCADE ON UPDATE CASCADE;
UNLOCK TABLES;