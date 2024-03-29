create table SMP_CONFIGURATION (
   PROPERTY varchar(512) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DESCRIPTION varchar(4000),
    VALUE varchar(4000),
    primary key (PROPERTY)
);
create table SMP_CONFIGURATION_AUD (
   PROPERTY varchar(512) not null,
    REV bigint not null,
    REVTYPE tinyint,
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar(4000),
    VALUE varchar(4000),
    primary key (PROPERTY, REV)
);

create table SMP_REV_INFO (
   id bigint generated by default as identity,
    REVISION_DATE timestamp,
    timestamp bigint not null,
    USERNAME varchar(255),
    primary key (id)
);