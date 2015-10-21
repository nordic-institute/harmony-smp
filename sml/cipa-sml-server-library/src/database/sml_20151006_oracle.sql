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

-- This procedure allows an administrator to change the certificate of an SMP
create or replace PROCEDURE CHANGE_CERTIFICATE
(
  OLD_USER IN VARCHAR2
, NEW_SUBJECT IN VARCHAR2
, NEW_SERIAL_NUMBER IN VARCHAR2
) AS
  NEW_USER VARCHAR2(255);
  ORG VARCHAR2(255);
  COUNTRY VARCHAR2(255);
  CN VARCHAR2(255);
  OLD_USER_COUNT int;
  NEW_USER_COUNT int;
  OLD_USER_NOT_EXIST_EXCEPTION exception;
  INPUT_NULL_EXCEPTION exception;
  INVALID_NEW_SUBJECT_EXCEPTION exception;
  NEW_USER_EXISTS_EXCEPTION exception;
BEGIN
  -- check if the old user actually exists
  SELECT COUNT(*) INTO OLD_USER_COUNT FROM SML_USER WHERE SML_USERNAME = OLD_USER;
  if OLD_USER_COUNT = 0 then
    raise OLD_USER_NOT_EXIST_EXCEPTION;
  end if;

  -- validate input data
  if (NEW_SERIAL_NUMBER is NULL or NEW_SUBJECT is NULL) then
    raise INPUT_NULL_EXCEPTION;
  end if;

  -- Extract the Organization
  ORG := TRIM(REGEXP_SUBSTR(NEW_SUBJECT, '(O=[^,]+)'));
  -- Extract the Country
  COUNTRY := TRIM(REGEXP_SUBSTR(NEW_SUBJECT, '(C=[^,]+)'));
  -- Extract the common name
  CN := TRIM(REGEXP_SUBSTR(NEW_SUBJECT, '(CN=[^,]+)'));
  -- Builds the new username using the subject and the new serial number
  NEW_USER := CONCAT(CONCAT(CN, CONCAT(',',ORG)), CONCAT(',', CONCAT(COUNTRY, CONCAT(':', LOWER(REGEXP_REPLACE(NEW_SERIAL_NUMBER, '\s'))))));

  -- validate the processed data
  if (ORG is NULL or COUNTRY is NULL or CN is NULL or NEW_USER is NULL) then
    raise INVALID_NEW_SUBJECT_EXCEPTION;
  end if;

  -- check if the new user already exists
  SELECT COUNT(*) INTO NEW_USER_COUNT FROM SML_USER WHERE SML_USERNAME = NEW_USER;
  if (NEW_USER_COUNT > 0) then
    raise NEW_USER_EXISTS_EXCEPTION;
  end if;

  -- Actually performs the update
  INSERT INTO SML_USER(SML_USERNAME, SML_PASSWORD) VALUES (NEW_USER, null);
  UPDATE SERVICE_METADATA_PUBLISHER SET USERNAME = NEW_USER WHERE USERNAME = OLD_USER;
  UPDATE ALLOWED_WILDCARD_SCHEMES SET USERNAME = NEW_USER WHERE USERNAME = OLD_USER;
  DELETE FROM SML_USER WHERE SML_USERNAME = OLD_USER;
  COMMIT;

EXCEPTION
  when OLD_USER_NOT_EXIST_EXCEPTION then
    dbms_output.put_line(CONCAT('ERROR: The user couldn''t be found: ', OLD_USER));
  when INPUT_NULL_EXCEPTION then
    dbms_output.put_line('ERROR: Please correctly fill the input data');
  when INVALID_NEW_SUBJECT_EXCEPTION then
    dbms_output.put_line('ERROR: Please verify that the NEW_SUBJECT input is correct. It should look like: ''O=DG-DIGIT, CN=SMP_2000000002, C=BE''');
  when NEW_USER_EXISTS_EXCEPTION then
    dbms_output.put_line(CONCAT('ERROR: The new user already exists in the database: ', NEW_USER));
  when others then
    raise;
END CHANGE_CERTIFICATE;