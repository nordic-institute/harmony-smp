
-- --------------------------------------------------------------------------------------------------------- 
-- create backup old tables
-- --------------------------------------------------------------------------------------------------------- 
alter table SMP_DOMAIN rename to SMP_DOMAIN_BCK;
alter table SMP_OWNERSHIP rename to SMP_OWNERSHIP_BCK;
alter table SMP_SERVICE_GROUP rename to SMP_SERVICE_GROUP_BCK;
alter table SMP_SERVICE_METADATA rename to SMP_SERVICE_METADATA_BCK;
alter table SMP_USER rename to SMP_USER_BCK;

-- --------------------------------------------------------------------------------------------------------- 
-- create new tables 
-- --------------------------------------------------------------------------------------------------------- 
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
        issuer varchar2(512 char),
        LAST_UPDATED_ON timestamp not null,
        serialNumber varchar2(128 char),
        subject varchar2(512 char),
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
        issuer varchar2(512 char),
        LAST_UPDATED_ON timestamp,
        serialNumber varchar2(128 char),
        subject varchar2(512 char),
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
        SML_CLIENT_CERT_HEADER varchar2(4000 char),
        SML_CLIENT_KEY_ALIAS varchar2(256 char),
        SML_PARTC_IDENT_REGEXP varchar2(4000 char),
        SML_SMP_ID varchar2(256 char),
        SML_SUBDOMAIN varchar2(256 char),
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
        SML_CLIENT_CERT_HEADER varchar2(4000 char),
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
        primary key (ID, REV)
    );

    create table SMP_SERVICE_GROUP_DOMAIN (
       ID number(19,0) not null,
        CREATED_ON timestamp not null,
        LAST_UPDATED_ON timestamp not null,
        SML_REGISTRED number(1,0) not null,
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
        SML_REGISTRED number(1,0),
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


-- --------------------------------------------------------------------------------------------------------- 
-- migrate data 
-- --------------------------------------------------------------------------------------------------------- 


-- migrate domains
INSERT INTO SMP_DOMAIN (ID, DOMAIN_CODE, SIGNATURE_KEY_ALIAS,SML_CLIENT_CERT_HEADER, SML_PARTC_IDENT_REGEXP, SML_SMP_ID, SML_SUBDOMAIN, LAST_UPDATED_ON, CREATED_ON )
   SELECT SMP_DOMAIN_SEQ.nextval, DOMAINID, SIGNATURECERTALIAS, BDMSLCLIENTCERTHEADER, '', BDMSLSMPID,'', sysdate, sysdate
      FROM SMP_DOMAIN_BCK;

-- migrate users
INSERT INTO SMP_USER (ID,EMAIL,ACTIVE,CREATED_ON,LAST_UPDATED_ON,USERNAME, PASSWORD,PASSWORD_CHANGED,ROLE)
    SELECT SMP_USER_SEQ.nextval,null, 1, sysdate, sysdate, USERNAME, PASSWORD, null, DECODE(isAdmin, 0,'SERVICE_GROUP_ADMIN',1,'SMP_ADMIN') 
        FROM SMP_USER_BCK;
-- create certificate records 
INSERT INTO SMP_CERTIFICATE (ID,CERTIFICATE_ID,CREATED_ON,LAST_UPDATED_ON)
    SELECT ID ,USERNAME, CREATED_ON, LAST_UPDATED_ON  FROM SMP_USER where PASSWORD  is null;

-- migrate service groups
INSERT INTO  SMP_SERVICE_GROUP ( ID, CREATED_ON, LAST_UPDATED_ON, PARTICIPANT_IDENTIFIER, PARTICIPANT_SCHEME)
    select SMP_SERVICE_GROUP_SEQ.nextval, sysdate, sysdate, BUSINESSIDENTIFIER, BUSINESSIDENTIFIERSCHEME from SMP_SERVICE_GROUP_BCK;
-- insert extensions
INSERT INTO SMP_SG_EXTENSION (ID, CREATED_ON, LAST_UPDATED_ON, EXTENSION) 
    select sg.id, sysdate,sysdate, clob_to_blob(sgb.extension)   from SMP_SERVICE_GROUP sg, SMP_SERVICE_GROUP_bck sgb
    where sg.PARTICIPANT_IDENTIFIER= sgb.BUSINESSIDENTIFIER 
        and sg.PARTICIPANT_SCHEME= sgb.BUSINESSIDENTIFIERSCHEME and sgb.extension is not null;

-- insert service group domains 
INSERT INTO SMP_SERVICE_GROUP_DOMAIN (ID, CREATED_ON, LAST_UPDATED_ON, SML_REGISTRED, FK_DOMAIN_ID, FK_SG_ID )
    select SMP_SERVICE_GROUP_DOMAIN_SEQ.nextval, sysdate, sysdate, 0, D.ID, SG.ID from SMP_SERVICE_GROUP_BCK SGB, SMP_SERVICE_GROUP SG, SMP_DOMAIN D WHERE
        SGB.BUSINESSIDENTIFIER = SG.PARTICIPANT_IDENTIFIER
        and SGB.BUSINESSIDENTIFIERSCHEME = SG.PARTICIPANT_SCHEME
        and SGB.DOMAINID = D.DOMAIN_CODE;


-- migrate service metadata (on migration there could be only one domain per service group therefore no need for domain)
INSERT INTO  SMP_SERVICE_METADATA ( ID, CREATED_ON, LAST_UPDATED_ON, DOCUMENT_IDENTIFIER, DOCUMENT_SCHEME, FK_SG_DOM_ID)
    select SMP_SERVICE_METADATA_SEQ.nextval, sysdate, sysdate, MD.DOCUMENTIDENTIFIER,  MD.DOCUMENTIDENTIFIERSCHEME, SGD.ID
        from SMP_SERVICE_METADATA_BCK MD, SMP_SERVICE_GROUP SG, SMP_SERVICE_GROUP_DOMAIN SGD
            where MD.BUSINESSIDENTIFIER = SG.PARTICIPANT_IDENTIFIER and MD.BUSINESSIDENTIFIERSCHEME = SG.PARTICIPANT_SCHEME
                 and SGD.FK_SG_ID = SG.id;
                
-- update service metadata xml
INSERT INTO  SMP_SERVICE_METADATA_XML ( ID, CREATED_ON, LAST_UPDATED_ON, XML_CONTENT)
    select MD.ID, sysdate, sysdate,  clob_to_blob(MDB.XMLCONTENT)
        from SMP_SERVICE_METADATA_BCK MDB, SMP_SERVICE_GROUP SG, SMP_SERVICE_GROUP_DOMAIN SGD, SMP_SERVICE_METADATA MD
            where MDB.BUSINESSIDENTIFIER = SG.PARTICIPANT_IDENTIFIER and MDB.BUSINESSIDENTIFIERSCHEME = SG.PARTICIPANT_SCHEME
                 and SGD.FK_SG_ID = SG.id -- only one service group domain at migration time
                 and MD.FK_SG_DOM_ID = SGD.id
                 and MDB.DOCUMENTIDENTIFIER = MD.DOCUMENT_IDENTIFIER
                 and MDB.DOCUMENTIDENTIFIERSCHEME = MD.DOCUMENT_SCHEME;

-- owners
INSERT INTO SMP_OWNERSHIP (FK_SG_ID, FK_USER_ID)
    select SG.ID, U.ID FROM SMP_OWNERSHIP_BCK OB, SMP_SERVICE_GROUP SG, SMP_USER U
        WHERE OB.USERNAME =U.USERNAME
            and OB.BUSINESSIDENTIFIER = SG.PARTICIPANT_IDENTIFIER
            and OB.BUSINESSIDENTIFIERSCHEME = SG.PARTICIPANT_SCHEME;

 -- we do not need certificate DN in USERNAME so remove it from username columns
UPDATE SMP_USER set USERNAME=null where PASSWORD  is null;
drop FUNCTION clob_to_blob

-- remove backup if migration succeeded- do in manually
-- drop table SMP_DOMAIN_BCK;
-- drop table SMP_OWNERSHIP_BCK;
-- drop table SMP_SERVICE_METADATA_BCK;
-- drop table SMP_SERVICE_GROUP_BCK;
-- drop table SMP_USER_BCK;









