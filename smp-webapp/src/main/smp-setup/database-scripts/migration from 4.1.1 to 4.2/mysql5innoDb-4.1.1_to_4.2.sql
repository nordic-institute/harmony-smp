-- set usernames for empty "users"
UPDATE SMP_USER set USERNAME ='USERNAME_' | SMP_USER.ID  where USERNAME IS NULL;
commit;

create table SMP_ALERT (
   ID bigint not null auto_increment comment 'Unique alert id',
    CREATED_ON datetime not null,
    LAST_UPDATED_ON datetime not null,
    ALERT_LEVEL varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
    ALERT_STATUS varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
    ALERT_STATUS_DESC varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
    ALERT_TYPE varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
    MAIL_SUBJECT varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
    MAIL_TO varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
    PROCESSED_TIME datetime,
    REPORTING_TIME datetime,
    FOR_USERNAME varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
    primary key (ID)
) comment='SMP alerts' ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table SMP_ALERT_AUD (
   ID bigint not null,
    REV bigint not null,
    REVTYPE tinyint,
    CREATED_ON datetime,
    LAST_UPDATED_ON datetime,
    ALERT_LEVEL varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
    ALERT_STATUS varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
    ALERT_STATUS_DESC varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
    ALERT_TYPE varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
    MAIL_SUBJECT varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
    MAIL_TO varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
    PROCESSED_TIME datetime,
    REPORTING_TIME datetime,
    FOR_USERNAME varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
    primary key (ID, REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table SMP_ALERT_PROPERTY (
   ID bigint not null auto_increment comment 'Unique alert property id',
    CREATED_ON datetime not null,
    LAST_UPDATED_ON datetime not null,
    PROPERTY varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
    VALUE varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
    FK_ALERT_ID bigint,
    primary key (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table SMP_ALERT_PROPERTY_AUD (
   ID bigint not null,
    REV bigint not null,
    REVTYPE tinyint,
    CREATED_ON datetime,
    LAST_UPDATED_ON datetime,
    PROPERTY varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
    VALUE varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
    FK_ALERT_ID bigint,
    primary key (ID, REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE SMP_CERTIFICATE ADD EXPIRE_LAST_ALERT_ON datetime comment 'Generated last expire alert';
ALTER TABLE SMP_CERTIFICATE_AUD ADD EXPIRE_LAST_ALERT_ON datetime comment 'Generated last expire alert';

create table SMP_CONFIGURATION_AUD (
   PROPERTY varchar(512)  CHARACTER SET utf8 COLLATE utf8_bin not null,
    REV bigint not null,
    REVTYPE tinyint,
    CREATED_ON datetime,
    LAST_UPDATED_ON datetime,
    DESCRIPTION varchar(4000)  CHARACTER SET utf8 COLLATE utf8_bin,
    VALUE varchar(4000)  CHARACTER SET utf8 COLLATE utf8_bin,
    primary key (PROPERTY, REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE SMP_USER MODIFY USERNAME varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin not null comment 'Unique username identifier. The Username must not be null';

ALTER TABLE SMP_USER ADD ACCESS_TOKEN varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin comment 'BCrypted personal access token';
ALTER TABLE SMP_USER ADD ACCESS_TOKEN_LAST_ALERT_ON datetime comment 'Generated last access token expire alert';
ALTER TABLE SMP_USER ADD ACCESS_TOKEN_EXPIRE_ON datetime comment 'Date when personal access token will expire';
ALTER TABLE SMP_USER ADD ACCESS_TOKEN_GENERATED_ON datetime comment 'Date when personal access token was generated';
ALTER TABLE SMP_USER ADD ACCESS_TOKEN_ID varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin comment 'Personal access token id';
ALTER TABLE SMP_USER ADD LAST_FAILED_LOGIN_ON datetime comment 'Last failed login attempt';
ALTER TABLE SMP_USER ADD AT_LAST_FAILED_LOGIN_ON datetime comment 'Last failed token login attempt';
ALTER TABLE SMP_USER ADD PASSWORD_LAST_ALERT_ON datetime comment 'Generated last password expire alert';
ALTER TABLE SMP_USER ADD PASSWORD_EXPIRE_ON datetime comment 'Date when password will expire';
ALTER TABLE SMP_USER ADD LOGIN_FAILURE_COUNT integer comment 'Sequential login failure count';
ALTER TABLE SMP_USER ADD AT_LOGIN_FAILURE_COUNT integer comment 'Sequential token login failure count';

ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin comment 'BCrypted personal access token';
ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN_LAST_ALERT_ON datetime comment 'Generated last access token expire alert';
ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN_EXPIRE_ON datetime comment 'Date when personal access token will expire';
ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN_GENERATED_ON datetime comment 'Date when personal access token was generated';
ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN_ID varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin comment 'Personal access token id';
ALTER TABLE SMP_USER_AUD ADD LAST_FAILED_LOGIN_ON datetime comment 'Last failed login attempt';
ALTER TABLE SMP_USER_AUD ADD AT_LAST_FAILED_LOGIN_ON datetime comment 'Last failed token login attempt';
ALTER TABLE SMP_USER_AUD ADD PASSWORD_LAST_ALERT_ON datetime comment 'Generated last password expire alert';
ALTER TABLE SMP_USER_AUD ADD PASSWORD_EXPIRE_ON datetime comment 'Date when password will expire';
ALTER TABLE SMP_USER_AUD ADD LOGIN_FAILURE_COUNT integer comment 'Sequential login failure count';
ALTER TABLE SMP_USER_AUD ADD AT_LOGIN_FAILURE_COUNT integer comment 'Sequential token login failure count';

alter table SMP_USER
   add constraint UK_tk9bjsmd2mevgt3b997i6pl27 unique (ACCESS_TOKEN_ID);
   alter table SMP_ALERT_AUD
   add constraint FKrw0qnto448ojlirpfmfntd8v2
   foreign key (REV)
   references SMP_REV_INFO (id);

alter table SMP_ALERT_PROPERTY
   add constraint FK15r37w3r5ty5f6074ykr2o4i6
   foreign key (FK_ALERT_ID)
   references SMP_ALERT (ID);

alter table SMP_ALERT_PROPERTY_AUD
   add constraint FKod33qjx87ih1a0skxl2sgddar
   foreign key (REV)
   references SMP_REV_INFO (id);

alter table SMP_CONFIGURATION_AUD
   add constraint FKd4yhbdlusovfbdti1fjkuxp9m
   foreign key (REV)
   references SMP_REV_INFO (id);

SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE SMP_REV_INFO MODIFY COLUMN id bigint not null auto_increment;
ALTER TABLE SMP_SERVICE_GROUP MODIFY COLUMN ID bigint not null auto_increment comment 'Unique ServiceGroup id';
ALTER TABLE SMP_SERVICE_GROUP_DOMAIN MODIFY COLUMN ID bigint not null auto_increment;
ALTER TABLE SMP_SERVICE_METADATA MODIFY COLUMN ID bigint not null auto_increment comment 'Shared primary key with master table SMP_SERVICE_METADATA';
ALTER TABLE SMP_USER MODIFY COLUMN ID bigint not null auto_increment comment 'Unique user id';
SET FOREIGN_KEY_CHECKS = 1;

-- drop sequence tables , because the are not needed anymore!
drop table SMP_DOMAIN_SEQ;
drop table SMP_REVISION_SEQ;
drop table SMP_SERVICE_GROUP_DOMAIN_SEQ;
drop table SMP_SERVICE_GROUP_SEQ;
drop table SMP_SERVICE_METADATA_SEQ;
drop table SMP_USER_SEQ;
-- set init back-compatible credentials to access tokens
UPDATE SMP_USER set ACCESS_TOKEN_ID = SMP_USER.USERNAME, ACCESS_TOKEN=SMP_USER.PASSWORD;
commit;