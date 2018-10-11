create sequence SMP_DOMAIN_SEQ start with 1 increment by  1;
create sequence SMP_REVISION_SEQ start with 1 increment by  1;
create sequence SMP_SERVICE_GROUP_DOMAIN_SEQ start with 1 increment by  50;
create sequence SMP_SERVICE_GROUP_SEQ start with 1 increment by  50;
create sequence SMP_SERVICE_METADATA_SEQ start with 1 increment by  50;
create sequence SMP_USER_SEQ start with 1 increment by  50;

    create table SMP_CERTIFICATE (
       ID number(19,0) not null,
        CERTIFICATE_ID varchar2(4000 char),
        CREATED_ON timestamp not null,
        LAST_UPDATED_ON timestamp not null,
        VALID_FROM timestamp,
        VALID_TO timestamp,
        primary key (ID)
    );

    create table SMP_CERTIFICATE_AUD (
       ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CERTIFICATE_ID varchar2(4000 char),
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        VALID_FROM timestamp,
        VALID_TO timestamp,
        primary key (ID, REV)
    );

    create table SMP_DOMAIN (
       ID number(19,0) not null,
        CREATED_ON timestamp not null,
        DOMAIN_CODE varchar2(256 char) not null,
        LAST_UPDATED_ON timestamp not null,
        SIGNATURE_KEY_ALIAS varchar2(256 char),
        SML_CLIENT_CERT_HEADER varchar2(256 char),
        SML_CLIENT_KEY_ALIAS varchar2(256 char),
        SML_PARTC_IDENT_REGEXP varchar2(4000 char),
        SML_SMP_ID varchar2(256 char),
        SML_SUBDOMAIN varchar2(256 char) not null,
        primary key (ID)
    );

    create table SMP_DOMAIN_AUD (
       ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp,
        DOMAIN_CODE varchar2(256 char),
        LAST_UPDATED_ON timestamp,
        SIGNATURE_KEY_ALIAS varchar2(256 char),
        SML_CLIENT_CERT_HEADER varchar2(256 char),
        SML_CLIENT_KEY_ALIAS varchar2(256 char),
        SML_PARTC_IDENT_REGEXP varchar2(4000 char),
        SML_SMP_ID varchar2(256 char),
        SML_SUBDOMAIN varchar2(256 char),
        primary key (ID, REV)
    );

    create table SMP_OWNERSHIP (
       FK_SG_ID number(19,0) not null,
        FK_USER_ID number(19,0) not null,
        primary key (FK_SG_ID, FK_USER_ID)
    );

    create table SMP_OWNERSHIP_AUD (
       REV number(19,0) not null,
        FK_SG_ID number(19,0) not null,
        FK_USER_ID number(19,0) not null,
        REVTYPE number(3,0),
        primary key (REV, FK_SG_ID, FK_USER_ID)
    );

    create table SMP_REV_INFO (
       id number(19,0) not null,
        REVISION_DATE timestamp,
        timestamp number(19,0) not null,
        USERNAME varchar2(255 char),
        primary key (id)
    );

    create table SMP_SERVICE_GROUP (
       ID number(19,0) not null,
        CREATED_ON timestamp not null,
        LAST_UPDATED_ON timestamp not null,
        PARTICIPANT_IDENTIFIER varchar2(256 char) not null,
        PARTICIPANT_SCHEME varchar2(256 char) not null,
        SML_REGISTRED number(1,0) not null,
        primary key (ID)
    );

    create table SMP_SERVICE_GROUP_AUD (
       ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        PARTICIPANT_IDENTIFIER varchar2(256 char),
        PARTICIPANT_SCHEME varchar2(256 char),
        SML_REGISTRED number(1,0),
        primary key (ID, REV)
    );

    create table SMP_SERVICE_GROUP_DOMAIN (
       ID number(19,0) not null,
        CREATED_ON timestamp not null,
        LAST_UPDATED_ON timestamp not null,
        FK_DOMAIN_ID number(19,0),
        FK_SG_ID number(19,0),
        primary key (ID)
    );

    create table SMP_SERVICE_GROUP_DOMAIN_AUD (
       ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        FK_DOMAIN_ID number(19,0),
        FK_SG_ID number(19,0),
        primary key (ID, REV)
    );

    create table SMP_SERVICE_METADATA (
       ID number(19,0) not null,
        CREATED_ON timestamp not null,
        DOCUMENT_IDENTIFIER varchar2(500 char) not null,
        DOCUMENT_SCHEME varchar2(500 char),
        LAST_UPDATED_ON timestamp not null,
        FK_SG_DOM_ID number(19,0) not null,
        primary key (ID)
    );

    create table SMP_SERVICE_METADATA_AUD (
       ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp,
        DOCUMENT_IDENTIFIER varchar2(500 char),
        DOCUMENT_SCHEME varchar2(500 char),
        LAST_UPDATED_ON timestamp,
        FK_SG_DOM_ID number(19,0),
        primary key (ID, REV)
    );

    create table SMP_SERVICE_METADATA_XML (
       ID number(19,0) not null,
        CREATED_ON timestamp not null,
        LAST_UPDATED_ON timestamp not null,
        XML_CONTENT blob,
        primary key (ID)
    );

    create table SMP_SERVICE_METADATA_XML_AUD (
       ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        XML_CONTENT blob,
        primary key (ID, REV)
    );

    create table SMP_SG_EXTENSION (
       ID number(19,0) not null,
        CREATED_ON timestamp not null,
        EXTENSION blob,
        LAST_UPDATED_ON timestamp not null,
        primary key (ID)
    );

    create table SMP_SG_EXTENSION_AUD (
       ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp,
        EXTENSION blob,
        LAST_UPDATED_ON timestamp,
        primary key (ID, REV)
    );

    create table SMP_USER (
       ID number(19,0) not null,
        ACTIVE number(1,0) not null,
        CREATED_ON timestamp not null,
        EMAIL varchar2(256 char),
        LAST_UPDATED_ON timestamp not null,
        PASSWORD varchar2(256 char),
        PASSWORD_CHANGED timestamp,
        ROLE varchar2(256 char),
        USERNAME varchar2(256 char),
        primary key (ID)
    );

    create table SMP_USER_AUD (
       ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        ACTIVE number(1,0),
        CREATED_ON timestamp,
        EMAIL varchar2(256 char),
        LAST_UPDATED_ON timestamp,
        PASSWORD varchar2(256 char),
        PASSWORD_CHANGED timestamp,
        ROLE varchar2(256 char),
        USERNAME varchar2(256 char),
        primary key (ID, REV)
    );

    alter table SMP_CERTIFICATE 
       add constraint UK_3x3rvf6hkim9fg16caurkgg6f unique (CERTIFICATE_ID);

    alter table SMP_DOMAIN 
       add constraint UK_djrwqd4luj5i7w4l7fueuaqbj unique (DOMAIN_CODE);

    alter table SMP_DOMAIN 
       add constraint UK_likb3jn0nlxlekaws0xx10uqc unique (SML_SUBDOMAIN);
create index SMP_SG_PART_ID_IDX on SMP_SERVICE_GROUP (PARTICIPANT_IDENTIFIER);
create index SMP_SG_PART_SCH_IDX on SMP_SERVICE_GROUP (PARTICIPANT_SCHEME);

    alter table SMP_SERVICE_GROUP 
       add constraint SMP_SG_UNIQ_PARTC_IDX unique (PARTICIPANT_SCHEME, PARTICIPANT_IDENTIFIER);
create index SMP_SMD_DOC_ID_IDX on SMP_SERVICE_METADATA (DOCUMENT_IDENTIFIER);
create index SMP_SMD_DOC_SCH_IDX on SMP_SERVICE_METADATA (DOCUMENT_SCHEME);

    alter table SMP_SERVICE_METADATA 
       add constraint SMP_MT_UNIQ_SG_DOC_IDX unique (FK_SG_DOM_ID, DOCUMENT_IDENTIFIER, DOCUMENT_SCHEME);

    alter table SMP_USER 
       add constraint UK_rt1f0anklfo05lt0my05fqq6 unique (USERNAME);

    alter table SMP_CERTIFICATE 
       add constraint FKayqgpj5ot3o8vrpduul7sstta 
       foreign key (ID) 
       references SMP_USER;

    alter table SMP_CERTIFICATE_AUD 
       add constraint FKnrwm8en8vv10li8ihwnurwd9e 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_DOMAIN_AUD 
       add constraint FK35qm8xmi74kfenugeonijodsg 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_OWNERSHIP 
       add constraint FKrnqwq06lbfwciup4rj8nvjpmy 
       foreign key (FK_USER_ID) 
       references SMP_USER;

    alter table SMP_OWNERSHIP 
       add constraint FKgexq5n6ftsid8ehqljvjh8p4i 
       foreign key (FK_SG_ID) 
       references SMP_SERVICE_GROUP;

    alter table SMP_OWNERSHIP_AUD 
       add constraint FK1lqynlbk8ow1ouxetf5wybk3k 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_SERVICE_GROUP_AUD 
       add constraint FKj3caimhegwyav1scpwrxoslef 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_SERVICE_GROUP_DOMAIN 
       add constraint FKo186xtefda6avl5p1tuqchp3n 
       foreign key (FK_DOMAIN_ID) 
       references SMP_DOMAIN;

    alter table SMP_SERVICE_GROUP_DOMAIN 
       add constraint FKgcvhnk2n34d3c6jhni5l3s3x3 
       foreign key (FK_SG_ID) 
       references SMP_SERVICE_GROUP;

    alter table SMP_SERVICE_GROUP_DOMAIN_AUD 
       add constraint FK6uc9r0eqw16baooxtmqjkih0j 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_SERVICE_METADATA 
       add constraint FKfvcml6b8x7kn80m30h8pxs7jl 
       foreign key (FK_SG_DOM_ID) 
       references SMP_SERVICE_GROUP_DOMAIN;

    alter table SMP_SERVICE_METADATA_AUD 
       add constraint FKbqr9pdnik1qxx2hi0xn4n7f61 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_SERVICE_METADATA_XML 
       add constraint FK4b1x06xlavcgbjnuilgksi7nm 
       foreign key (ID) 
       references SMP_SERVICE_METADATA;

    alter table SMP_SERVICE_METADATA_XML_AUD 
       add constraint FKevatmlvvwoxfnjxkvmokkencb 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_SG_EXTENSION 
       add constraint FKtf0mfonugp2jbkqo2o142chib 
       foreign key (ID) 
       references SMP_SERVICE_GROUP;

    alter table SMP_SG_EXTENSION_AUD 
       add constraint FKmdo9v2422adwyebvl34qa3ap6 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_USER_AUD 
       add constraint FK2786r5minnkai3d22b191iiiq 
       foreign key (REV) 
       references SMP_REV_INFO;
