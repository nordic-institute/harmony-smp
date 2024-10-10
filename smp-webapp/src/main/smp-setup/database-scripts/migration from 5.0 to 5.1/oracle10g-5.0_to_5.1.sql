create sequence SMP_DOC_PROP_SEQ start with 1 increment by  1;
create sequence SMP_DOCVER_EVENT_SEQ start with 1 increment by  1;
create sequence SMP_DOMAIN_CONF_SEQ start with 1 increment by  1;

ALTER TABLE SMP_CREDENTIAL ADD RESET_EXPIRE_ON timestamp;
ALTER TABLE SMP_CREDENTIAL ADD RESET_TOKEN varchar2(256 char);
comment on column SMP_CREDENTIAL.RESET_EXPIRE_ON is
    'Date time when reset token will expire';

comment on column SMP_CREDENTIAL.RESET_TOKEN is
    'Reset token for credential reset';

ALTER TABLE SMP_CREDENTIAL_AUD ADD RESET_EXPIRE_ON timestamp;
ALTER TABLE SMP_CREDENTIAL_AUD ADD RESET_TOKEN varchar2(256 char);

ALTER TABLE SMP_DOCUMENT ADD REF_DOCUMENT_URL varchar2(1024 char);
ALTER TABLE SMP_DOCUMENT ADD SHARING_ENABLED number(1,0);
ALTER TABLE SMP_DOCUMENT ADD FK_REF_DOCUMENT_ID number(19,0);
ALTER TABLE SMP_DOCUMENT_AUD ADD REF_DOCUMENT_URL varchar2(1024 char);
ALTER TABLE SMP_DOCUMENT_AUD ADD SHARING_ENABLED number(1,0);
ALTER TABLE SMP_DOCUMENT_AUD ADD FK_REF_DOCUMENT_ID number(19,0);

ALTER TABLE SMP_RESOURCE ADD REVIEW_ENABLED number(1,0);
ALTER TABLE SMP_RESOURCE_AUD ADD REVIEW_ENABLED number(1,0);

ALTER TABLE SMP_RESOURCE_MEMBER ADD PERMISSION_REVIEW number(1,0);
comment on column SMP_RESOURCE_MEMBER.PERMISSION_REVIEW is
        'User permission to review the resource document';
ALTER TABLE SMP_RESOURCE_MEMBER_AUD ADD PERMISSION_REVIEW number(1,0);

create table SMP_DOCUMENT_PROPERTY (
    ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DESCRIPTION varchar2(4000 char),
    PROPERTY_NAME varchar2(255 char),
    PROPERTY_TYPE varchar2(64 char),
    PROPERTY_VALUE varchar2(4000 char),
    FK_DOCUMENT_ID number(19,0),
    primary key (ID)
);

comment on column SMP_DOCUMENT_PROPERTY.ID is
    'Unique document property id';

comment on column SMP_DOCUMENT_PROPERTY.DESCRIPTION is
    'Property description';

create table SMP_DOCUMENT_PROPERTY_AUD (
    ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar2(4000 char),
    PROPERTY_NAME varchar2(255 char),
    PROPERTY_TYPE varchar2(64 char),
    PROPERTY_VALUE varchar2(4000 char),
    FK_DOCUMENT_ID number(19,0),
    primary key (ID, REV)
);

ALTER TABLE SMP_DOCUMENT_VERSION ADD STATUS varchar2(255 char) default 'RETIRED'  not null;
comment on column SMP_DOCUMENT_VERSION.STATUS is
        'Document version status';

ALTER TABLE SMP_DOCUMENT_VERSION_AUD ADD STATUS varchar2(255 char);

create table SMP_DOCUMENT_VERSION_EVENT (
    ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DETAILS varchar2(1024 char),
    EVENT_ON timestamp,
    EVENT_SOURCE varchar2(255 char) not null,
    EVENT_TYPE varchar2(255 char) not null,
    EVENT_STATUS varchar2(255 char) not null,
    EVENT_BY_USERNAME varchar2(64 char),
    FK_DOCUMENT_VERSION_ID number(19,0),
    primary key (ID)
);

comment on table SMP_DOCUMENT_VERSION_EVENT is
    'Document version Events.';
comment on column SMP_DOCUMENT_VERSION_EVENT.ID is
    'Unique document version event identifier';
comment on column SMP_DOCUMENT_VERSION_EVENT.DETAILS is
    'Details of the event';
comment on column SMP_DOCUMENT_VERSION_EVENT.EVENT_ON is
    'Date time of the event';
comment on column SMP_DOCUMENT_VERSION_EVENT.EVENT_SOURCE is
    'Event source UI, API';
comment on column SMP_DOCUMENT_VERSION_EVENT.EVENT_TYPE is
    'Document version event type';
comment on column SMP_DOCUMENT_VERSION_EVENT.EVENT_STATUS is
    'Document version event type';
comment on column SMP_DOCUMENT_VERSION_EVENT.EVENT_BY_USERNAME is
    'username identifier of the user who triggered the event';

create table SMP_DOMAIN_CONFIGURATION (
    ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DESCRIPTION varchar2(4000 char),
    PROPERTY_NAME varchar2(512 char) not null,
    SYSTEM_DEFAULT number(1,0) not null,
    PROPERTY_VALUE varchar2(4000 char),
    FK_DOMAIN_ID number(19,0) not null,
    primary key (ID)
);
comment on table SMP_DOMAIN_CONFIGURATION is
    'SMP domain configuration';
comment on column SMP_DOMAIN_CONFIGURATION.ID is
    'Unique domain configuration id';
comment on column SMP_DOMAIN_CONFIGURATION.DESCRIPTION is
    'Property description';
comment on column SMP_DOMAIN_CONFIGURATION.PROPERTY_NAME is
    'Property name/key';
comment on column SMP_DOMAIN_CONFIGURATION.SYSTEM_DEFAULT is
    'Use system default value';

comment on column SMP_DOMAIN_CONFIGURATION.PROPERTY_VALUE is
    'Property value';

create table SMP_DOMAIN_CONFIGURATION_AUD (
    ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar2(4000 char),
    PROPERTY_NAME varchar2(512 char),
    SYSTEM_DEFAULT number(1,0),
    PROPERTY_VALUE varchar2(4000 char),
    FK_DOMAIN_ID number(19,0),
    primary key (ID, REV)
);

-- create constraints and indexes
alter table SMP_DOCUMENT_PROPERTY
       add constraint SMP_DOC_PROP_IDX unique (FK_DOCUMENT_ID, PROPERTY_NAME);

create index SMP_DOCVEREVNT_DOCVER_IDX on SMP_DOCUMENT_VERSION_EVENT (FK_DOCUMENT_VERSION_ID);

alter table SMP_DOMAIN_CONFIGURATION
       add constraint SMP_DOMAIN_CONF_IDX unique (ID, PROPERTY_NAME, FK_DOMAIN_ID);

alter table SMP_DOCUMENT
   add constraint FKbytp2kp8g3pj8qfp1g6a2g7p
   foreign key (FK_REF_DOCUMENT_ID)
   references SMP_DOCUMENT;

alter table SMP_DOCUMENT_PROPERTY
   add constraint FKfag3795e9mrvfvesd00yis9yh
   foreign key (FK_DOCUMENT_ID)
   references SMP_DOCUMENT;

alter table SMP_DOCUMENT_PROPERTY_AUD
   add constraint FK81057kcrugb1cfm0io5vkxtin
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_DOCUMENT_VERSION_EVENT
   add constraint FK6es2svpoxyrnt1h05c9junmdn
   foreign key (FK_DOCUMENT_VERSION_ID)
   references SMP_DOCUMENT_VERSION;

alter table SMP_DOMAIN_CONFIGURATION
   add constraint FK4303vstoigqtmeo3t2i034gm3
   foreign key (FK_DOMAIN_ID)
   references SMP_DOMAIN;

alter table SMP_DOMAIN_CONFIGURATION_AUD
   add constraint FKkelcga805bleh5x256hy5e1xb
   foreign key (REV)
   references SMP_REV_INFO;

-- ----------------------------------------------
-- update SMP_DOCUMENT_VERSION STATUS  to RETIRED and current versions to PUBLISHED AND set it to  NOT NULL
UPDATE SMP_DOCUMENT_VERSION  DV
    SET STATUS = 'PUBLISHED'
    where EXISTS (SELECT DOC.id from SMP_DOCUMENT DOC where DOC.ID = DV.FK_DOCUMENT_ID
    AND DOC.CURRENT_VERSION = DV.VERSION );

commit;


