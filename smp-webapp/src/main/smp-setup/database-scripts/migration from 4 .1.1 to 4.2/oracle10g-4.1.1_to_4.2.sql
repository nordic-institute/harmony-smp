-- CREATE SEQUENCE smp_alert_prop_seq START WITH 1 INCREMENT BY 1;

-- CREATE SEQUENCE smp_alert_seq START WITH 1 INCREMENT BY 1;

-- set usernames for empty "users"

UPDATE smp_user
SET
    username = 'USERNAME_' || smp_user.id 
WHERE
    username IS NULL;

COMMIT;
-- create new alert table

CREATE TABLE smp_alert (
    id                  NUMBER(19,0) NOT NULL,
    created_on          TIMESTAMP NOT NULL,
    last_updated_on     TIMESTAMP NOT NULL,
    alert_level         VARCHAR2(255 CHAR),
    alert_status        VARCHAR2(255 CHAR),
    alert_status_desc   VARCHAR2(1024 CHAR),
    alert_type          VARCHAR2(255 CHAR),
    mail_subject        VARCHAR2(1024 CHAR),
    mail_to             VARCHAR2(1024 CHAR),
    processed_time      TIMESTAMP,
    reporting_time      TIMESTAMP,
    for_username        VARCHAR2(256 CHAR),
    PRIMARY KEY ( id )
);

COMMENT ON TABLE smp_alert IS
    'SMP alerts';

COMMENT ON COLUMN smp_alert.id IS
    'Unique alert id';

CREATE TABLE smp_alert_aud (
    id                  NUMBER(19,0) NOT NULL,
    rev                 NUMBER(19,0) NOT NULL,
    revtype             NUMBER(3,0),
    created_on          TIMESTAMP,
    last_updated_on     TIMESTAMP,
    alert_level         VARCHAR2(255 CHAR),
    alert_status        VARCHAR2(255 CHAR),
    alert_status_desc   VARCHAR2(1024 CHAR),
    alert_type          VARCHAR2(255 CHAR),
    mail_subject        VARCHAR2(1024 CHAR),
    mail_to             VARCHAR2(1024 CHAR),
    processed_time      TIMESTAMP,
    reporting_time      TIMESTAMP,
    for_username        VARCHAR2(256 CHAR),
    PRIMARY KEY ( id,
                  rev )
);

CREATE TABLE smp_alert_property (
    id                NUMBER(19,0) NOT NULL,
    created_on        TIMESTAMP NOT NULL,
    last_updated_on   TIMESTAMP NOT NULL,
    property          VARCHAR2(255 CHAR),
    value             VARCHAR2(1024 CHAR),
    fk_alert_id       NUMBER(19,0),
    PRIMARY KEY ( id )
);

COMMENT ON COLUMN smp_alert_property.id IS
    'Unique alert property id';

CREATE TABLE smp_alert_property_aud (
    id                NUMBER(19,0) NOT NULL,
    rev               NUMBER(19,0) NOT NULL,
    revtype           NUMBER(3,0),
    created_on        TIMESTAMP,
    last_updated_on   TIMESTAMP,
    property          VARCHAR2(255 CHAR),
    value             VARCHAR2(1024 CHAR),
    fk_alert_id       NUMBER(19,0),
    PRIMARY KEY ( id,
                  rev )
);
-- update certificate table

ALTER TABLE smp_certificate ADD expire_last_alert_on TIMESTAMP;

ALTER TABLE smp_certificate_aud ADD expire_last_alert_on TIMESTAMP;

COMMENT ON COLUMN smp_certificate.expire_last_alert_on IS
    'Generated last expire alert';

-- add audit table for configuration

CREATE TABLE smp_configuration_aud (
    property          VARCHAR2(512 CHAR) NOT NULL,
    rev               NUMBER(19,0) NOT NULL,
    revtype           NUMBER(3,0),
    created_on        TIMESTAMP,
    last_updated_on   TIMESTAMP,
    description       VARCHAR2(4000 CHAR),
    value             VARCHAR2(4000 CHAR),
    PRIMARY KEY ( property,
                  rev )
);

-- set option that service group scheme can be also null

ALTER TABLE smp_service_group MODIFY (
    participant_scheme NULL
);

-- modify user table

ALTER TABLE smp_user MODIFY (
    username NOT NULL
);

ALTER TABLE smp_user ADD access_token VARCHAR2(256 CHAR);

ALTER TABLE smp_user ADD access_token_last_alert_on TIMESTAMP;

ALTER TABLE smp_user ADD access_token_expire_on TIMESTAMP;

ALTER TABLE smp_user ADD access_token_generated_on TIMESTAMP;

ALTER TABLE smp_user ADD access_token_id VARCHAR2(256 CHAR);

ALTER TABLE smp_user ADD last_failed_login_on TIMESTAMP;

ALTER TABLE smp_user ADD at_last_failed_login_on TIMESTAMP;

ALTER TABLE smp_user ADD password_last_alert_on TIMESTAMP;

ALTER TABLE smp_user ADD password_expire_on TIMESTAMP;

ALTER TABLE smp_user ADD login_failure_count NUMBER(10,0);

ALTER TABLE smp_user ADD at_login_failure_count NUMBER(10,0);

ALTER TABLE smp_user_aud ADD access_token VARCHAR2(256 CHAR);

ALTER TABLE smp_user_aud ADD access_token_last_alert_on TIMESTAMP;

ALTER TABLE smp_user_aud ADD access_token_expire_on TIMESTAMP;

ALTER TABLE smp_user_aud ADD access_token_generated_on TIMESTAMP;

ALTER TABLE smp_user_aud ADD access_token_id VARCHAR2(256 CHAR);

ALTER TABLE smp_user_aud ADD last_failed_login_on TIMESTAMP;

ALTER TABLE smp_user_aud ADD at_last_failed_login_on TIMESTAMP;

ALTER TABLE smp_user_aud ADD password_last_alert_on TIMESTAMP;

ALTER TABLE smp_user_aud ADD password_expire_on TIMESTAMP;

ALTER TABLE smp_user_aud ADD login_failure_count NUMBER(10,0);

ALTER TABLE smp_user_aud ADD at_login_failure_count NUMBER(10,0);

COMMENT ON COLUMN smp_user.access_token IS
    'BCrypted personal access token';

COMMENT ON COLUMN smp_user.access_token_last_alert_on IS
    'Generated last access token expire alert';

COMMENT ON COLUMN smp_user.access_token_expire_on IS
    'Date when personal access token will expire';

COMMENT ON COLUMN smp_user.access_token_generated_on IS
    'Date when personal access token was generated';

COMMENT ON COLUMN smp_user.access_token_id IS
    'Personal access token id';

COMMENT ON COLUMN smp_user.last_failed_login_on IS
    'Last failed login attempt';

COMMENT ON COLUMN smp_user.at_last_failed_login_on IS
    'Last failed token login attempt';

COMMENT ON COLUMN smp_user.password_last_alert_on IS
    'Generated last password expire alert';

COMMENT ON COLUMN smp_user.password_expire_on IS
    'Date when password will expire';

COMMENT ON COLUMN smp_user.login_failure_count IS
    'Sequential login failure count';

COMMENT ON COLUMN smp_user.at_login_failure_count IS
    'Sequential token login failure count';

ALTER TABLE smp_user ADD CONSTRAINT uk_tk9bjsmd2mevgt3b997i6pl27 UNIQUE ( access_token_id );

ALTER TABLE smp_alert_aud ADD CONSTRAINT fkrw0qnto448ojlirpfmfntd8v2 FOREIGN KEY ( rev )
    REFERENCES smp_rev_info;

ALTER TABLE smp_alert_property ADD CONSTRAINT fk15r37w3r5ty5f6074ykr2o4i6 FOREIGN KEY ( fk_alert_id )
    REFERENCES smp_alert;

ALTER TABLE smp_alert_property_aud ADD CONSTRAINT fkod33qjx87ih1a0skxl2sgddar FOREIGN KEY ( rev )
    REFERENCES smp_rev_info;

ALTER TABLE smp_configuration_aud ADD CONSTRAINT fkd4yhbdlusovfbdti1fjkuxp9m FOREIGN KEY ( rev )
    REFERENCES smp_rev_info;


-- set init back-compatible credentials to access tokens

UPDATE smp_user
SET
    access_token_id = smp_user.username,
    access_token = smp_user.password;

COMMIT;
