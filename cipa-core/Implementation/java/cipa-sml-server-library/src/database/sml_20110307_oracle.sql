--
-- Version: 1.0
-- Migrated version of Mysql dump script

----------------------------------

DROP TABLE migrate cascade constraints;
CREATE TABLE migrate (
rec_value VARCHAR(50) NOT NULL, 
migration_code VARCHAR(200) NOT NULL, 
scheme VARCHAR(25) NOT NULL, 
  PRIMARY KEY (scheme, rec_value, migration_code)
);

DROP TABLE sml_user cascade constraints;
CREATE TABLE sml_user (
sml_username VARCHAR(200) NOT NULL, 
sml_password CLOB, 
  PRIMARY KEY (sml_username)
);


DROP TABLE allowed_wildcard_schemes cascade constraints;
CREATE TABLE allowed_wildcard_schemes (
username VARCHAR(200) NOT NULL, 
scheme VARCHAR(25) NOT NULL, 
  PRIMARY KEY (scheme, username),
  CONSTRAINT FK_allowed_wildcard_schemes FOREIGN KEY (username) REFERENCES sml_user (sml_username)
);

DROP TABLE service_metadata_publisher;
CREATE TABLE service_metadata_publisher(
smp_id VARCHAR(200) NOT NULL, 
logical_address CLOB NOT NULL, 
physical_address CLOB NOT NULL, 
username VARCHAR(200) NOT NULL,
  PRIMARY KEY (smp_id),
  CONSTRAINT FK_service_metadata_publisher FOREIGN KEY (username) REFERENCES sml_user (sml_username)
);



DROP TABLE recipient_part_identifier;
CREATE TABLE recipient_part_identifier (
rec_value VARCHAR(50) NOT NULL, 
scheme VARCHAR(25) NOT NULL, 
smp_id VARCHAR(200) NOT NULL, 
  PRIMARY KEY (scheme, rec_value),
  CONSTRAINT FK_recipient_part_identifier FOREIGN KEY (smp_id) REFERENCES SERVICE_METADATA_PUBLISHER (smp_id)
);

