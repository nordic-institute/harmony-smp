create sequence SMP_ALERT_PROP_SEQ start with 1 increment by  1;
create sequence SMP_ALERT_SEQ start with 1 increment by  1;

-- set usernames for empty "users"
UPDATE SMP_USER set USERNAME ='USERNAME_' | lpad(SMP_USER.ID, 3, '0')  where USERNAME IS NULL;
commit;
-- create new alert table
create table SMP_ALERT (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    ALERT_LEVEL varchar2(255 char),
    ALERT_STATUS varchar2(255 char),
    ALERT_STATUS_DESC varchar2(1024 char),
    ALERT_TYPE varchar2(255 char),
    MAIL_SUBJECT varchar2(1024 char),
    MAIL_TO varchar2(1024 char),
    PROCESSED_TIME timestamp,
    REPORTING_TIME timestamp,
    FOR_USERNAME varchar2(256 char),
    primary key (ID)
);

comment on table SMP_ALERT is
    'SMP alerts';

comment on column SMP_ALERT.ID is
    'Unique alert id';

create table SMP_ALERT_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    ALERT_LEVEL varchar2(255 char),
    ALERT_STATUS varchar2(255 char),
    ALERT_STATUS_DESC varchar2(1024 char),
    ALERT_TYPE varchar2(255 char),
    MAIL_SUBJECT varchar2(1024 char),
    MAIL_TO varchar2(1024 char),
    PROCESSED_TIME timestamp,
    REPORTING_TIME timestamp,
    FOR_USERNAME varchar2(256 char),
    primary key (ID, REV)
);

create table SMP_ALERT_PROPERTY (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    PROPERTY varchar2(255 char),
    VALUE varchar2(1024 char),
    FK_ALERT_ID number(19,0),
    primary key (ID)
);

comment on column SMP_ALERT_PROPERTY.ID is
    'Unique alert property id';

create table SMP_ALERT_PROPERTY_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    PROPERTY varchar2(255 char),
    VALUE varchar2(1024 char),
    FK_ALERT_ID number(19,0),
    primary key (ID, REV)
);
-- update certificate table
ALTER TABLE SMP_CERTIFICATE ADD  EXPIRE_LAST_ALERT_ON timestamp;
ALTER TABLE SMP_CERTIFICATE_AUD ADD EXPIRE_LAST_ALERT_ON timestamp;

comment on column SMP_CERTIFICATE.EXPIRE_LAST_ALERT_ON is
        'Generated last expire alert';

-- add audit table for configuration
create table SMP_CONFIGURATION_AUD (
   PROPERTY varchar2(512 char) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar2(4000 char),
    VALUE varchar2(4000 char),
    primary key (PROPERTY, REV)
);

-- set option that service group scheme can be also null
ALTER TABLE SMP_SERVICE_GROUP MODIFY (PARTICIPANT_SCHEME NULL);

-- modify user table
ALTER TABLE SMP_USER MODIFY (USERNAME NOT NULL);
ALTER TABLE SMP_USER ADD ACCESS_TOKEN varchar2(256 char);
ALTER TABLE SMP_USER ADD ACCESS_TOKEN_LAST_ALERT_ON timestamp;
ALTER TABLE SMP_USER ADD ACCESS_TOKEN_EXPIRE_ON timestamp;
ALTER TABLE SMP_USER ADD ACCESS_TOKEN_GENERATED_ON timestamp;
ALTER TABLE SMP_USER ADD ACCESS_TOKEN_ID varchar2(256 char);
ALTER TABLE SMP_USER ADD  LAST_FAILED_LOGIN_ON timestamp;
ALTER TABLE SMP_USER ADD AT_LAST_FAILED_LOGIN_ON timestamp;
ALTER TABLE SMP_USER ADD PASSWORD_LAST_ALERT_ON timestamp;
ALTER TABLE SMP_USER ADD PASSWORD_EXPIRE_ON timestamp;
ALTER TABLE SMP_USER ADD LOGIN_FAILURE_COUNT number(10,0);
ALTER TABLE SMP_USER ADD AT_LOGIN_FAILURE_COUNT number(10,0);

ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN varchar2(256 char);
ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN_LAST_ALERT_ON timestamp;
ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN_EXPIRE_ON timestamp;
ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN_GENERATED_ON timestamp;
ALTER TABLE SMP_USER_AUD ADD ACCESS_TOKEN_ID varchar2(256 char);
ALTER TABLE SMP_USER_AUD ADD  LAST_FAILED_LOGIN_ON timestamp;
ALTER TABLE SMP_USER_AUD ADD AT_LAST_FAILED_LOGIN_ON timestamp;
ALTER TABLE SMP_USER_AUD ADD PASSWORD_LAST_ALERT_ON timestamp;
ALTER TABLE SMP_USER_AUD ADD PASSWORD_EXPIRE_ON timestamp;
ALTER TABLE SMP_USER_AUD ADD LOGIN_FAILURE_COUNT number(10,0);
ALTER TABLE SMP_USER_AUD ADD AT_LOGIN_FAILURE_COUNT number(10,0);

comment on column SMP_USER.ACCESS_TOKEN is
    'BCrypted personal access token';

comment on column SMP_USER.ACCESS_TOKEN_LAST_ALERT_ON is
    'Generated last access token expire alert';

comment on column SMP_USER.ACCESS_TOKEN_EXPIRE_ON is
    'Date when personal access token will expire';

comment on column SMP_USER.ACCESS_TOKEN_GENERATED_ON is
    'Date when personal access token was generated';

comment on column SMP_USER.ACCESS_TOKEN_ID is
'Personal access token id';

comment on column SMP_USER.LAST_FAILED_LOGIN_ON is
'Last failed login attempt';

comment on column SMP_USER.AT_LAST_FAILED_LOGIN_ON is
'Last failed token login attempt';

comment on column SMP_USER.PASSWORD_LAST_ALERT_ON is
'Generated last password expire alert';

comment on column SMP_USER.PASSWORD_EXPIRE_ON is
'Date when password will expire';
comment on column SMP_USER.LOGIN_FAILURE_COUNT is
'Sequential login failure count';

comment on column SMP_USER.AT_LOGIN_FAILURE_COUNT is
'Sequential token login failure count';

alter table SMP_USER
   add constraint UK_tk9bjsmd2mevgt3b997i6pl27 unique (ACCESS_TOKEN_ID);

alter table SMP_ALERT_AUD
   add constraint FKrw0qnto448ojlirpfmfntd8v2
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_ALERT_PROPERTY
   add constraint FK15r37w3r5ty5f6074ykr2o4i6
   foreign key (FK_ALERT_ID)
   references SMP_ALERT;

alter table SMP_ALERT_PROPERTY_AUD
   add constraint FKod33qjx87ih1a0skxl2sgddar
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_CONFIGURATION_AUD
       add constraint FKd4yhbdlusovfbdti1fjkuxp9m
       foreign key (REV)
       references SMP_REV_INFO;
