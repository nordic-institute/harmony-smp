
-- --------------------------------------------------------------------------------------------------------- 
-- create backup old tables
-- --------------------------------------------------------------------------------------------------------- 
alter table smp_domain rename to SMP_DOMAIN_BCK;
alter table smp_ownership rename to SMP_OWNERSHIP_BCK;
alter table smp_service_group rename to SMP_SERVICE_GROUP_BCK;
alter table smp_service_metadata rename to SMP_SERVICE_METADATA_BCK;
alter table smp_user rename to SMP_USER_BCK;

-- --------------------------------------------------------------------------------------------------------- 
-- create new tables 
-- --------------------------------------------------------------------------------------------------------- 

    create table SMP_CERTIFICATE (
       ID bigint not null,
        CERTIFICATE_ID varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
        CREATED_ON datetime not null,
        ISSUER varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
        LAST_UPDATED_ON datetime not null,
        SERIALNUMBER varchar(128)  CHARACTER SET utf8 COLLATE utf8_bin,
        SUBJECT varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
        VALID_FROM datetime,
        VALID_TO datetime,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_CERTIFICATE_AUD (
       ID bigint not null,
        REV bigint not null,
        REVTYPE tinyint,
        CERTIFICATE_ID varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
        CREATED_ON datetime,
        ISSUER varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
        LAST_UPDATED_ON datetime,
        SERIALNUMBER varchar(128)  CHARACTER SET utf8 COLLATE utf8_bin,
        SUBJECT varchar(1024)  CHARACTER SET utf8 COLLATE utf8_bin,
        VALID_FROM datetime,
        VALID_TO datetime,
        primary key (ID, REV)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_DOMAIN (
       ID bigint not null auto_increment,
        CREATED_ON datetime not null,
        DOMAIN_CODE varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin not null,
        LAST_UPDATED_ON datetime not null,
        SIGNATURE_KEY_ALIAS varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_CLIENT_CERT_HEADER varchar(4000)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_CLIENT_KEY_ALIAS varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_PARTC_IDENT_REGEXP varchar(4000)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_SMP_ID varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_SUBDOMAIN varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_REGISTERED bit,
        SML_BLUE_COAT_AUTH bit,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_DOMAIN_AUD (
       ID bigint not null,
        REV bigint not null,
        REVTYPE tinyint,
        CREATED_ON datetime,
        DOMAIN_CODE varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        LAST_UPDATED_ON datetime,
        SIGNATURE_KEY_ALIAS varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_CLIENT_CERT_HEADER varchar(4000)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_CLIENT_KEY_ALIAS varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_PARTC_IDENT_REGEXP varchar(4000)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_SMP_ID varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_SUBDOMAIN varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        SML_REGISTERED bit,
        SML_BLUE_COAT_AUTH bit,
        primary key (ID, REV)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_DOMAIN_SEQ (
       next_val bigint
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 

    create table SMP_OWNERSHIP (
       FK_SG_ID bigint not null,
        FK_USER_ID bigint not null,
        primary key (FK_SG_ID, FK_USER_ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_OWNERSHIP_AUD (
       REV bigint not null,
        FK_SG_ID bigint not null,
        FK_USER_ID bigint not null,
        REVTYPE tinyint,
        primary key (REV, FK_SG_ID, FK_USER_ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_REV_INFO (
       id bigint not null,
        REVISION_DATE datetime,
        timestamp bigint not null,
        USERNAME varchar(255)  CHARACTER SET utf8 COLLATE utf8_bin,
        primary key (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_REVISION_SEQ (
       next_val bigint
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


    create table SMP_SERVICE_GROUP (
       ID bigint not null auto_increment,
        CREATED_ON datetime not null,
        LAST_UPDATED_ON datetime not null,
        PARTICIPANT_IDENTIFIER varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin not null,
        PARTICIPANT_SCHEME varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin not null,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SERVICE_GROUP_AUD (
       ID bigint not null,
        REV bigint not null,
        REVTYPE tinyint,
        CREATED_ON datetime,
        LAST_UPDATED_ON datetime,
        PARTICIPANT_IDENTIFIER varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        PARTICIPANT_SCHEME varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        primary key (ID, REV)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SERVICE_GROUP_DOMAIN (
       ID bigint not null auto_increment,
        CREATED_ON datetime not null,
        LAST_UPDATED_ON datetime not null,
        SML_REGISTERED bit not null,
        FK_DOMAIN_ID bigint,
        FK_SG_ID bigint,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SERVICE_GROUP_DOMAIN_AUD (
       ID bigint not null,
        REV bigint not null,
        REVTYPE tinyint,
        CREATED_ON datetime,
        LAST_UPDATED_ON datetime,
        SML_REGISTERED bit,
        FK_DOMAIN_ID bigint,
        FK_SG_ID bigint,
        primary key (ID, REV)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SERVICE_GROUP_DOMAIN_SEQ (
       next_val bigint
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;



    create table SMP_SERVICE_GROUP_SEQ (
       next_val bigint
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SERVICE_METADATA (
       ID bigint not null auto_increment,
        CREATED_ON datetime not null,
        DOCUMENT_IDENTIFIER varchar(500)  CHARACTER SET utf8 COLLATE utf8_bin not null,
        DOCUMENT_SCHEME varchar(500)  CHARACTER SET utf8 COLLATE utf8_bin,
        LAST_UPDATED_ON datetime not null,
        FK_SG_DOM_ID bigint not null,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SERVICE_METADATA_AUD (
       ID bigint not null,
        REV bigint not null,
        REVTYPE tinyint,
        CREATED_ON datetime,
        DOCUMENT_IDENTIFIER varchar(500)  CHARACTER SET utf8 COLLATE utf8_bin,
        DOCUMENT_SCHEME varchar(500)  CHARACTER SET utf8 COLLATE utf8_bin,
        LAST_UPDATED_ON datetime,
        FK_SG_DOM_ID bigint,
        primary key (ID, REV)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SERVICE_METADATA_SEQ (
       next_val bigint
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


    create table SMP_SERVICE_METADATA_XML (
       ID bigint not null,
        CREATED_ON datetime not null,
        LAST_UPDATED_ON datetime not null,
        XML_CONTENT longblob,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SERVICE_METADATA_XML_AUD (
       ID bigint not null,
        REV bigint not null,
        REVTYPE tinyint,
        CREATED_ON datetime,
        LAST_UPDATED_ON datetime,
        XML_CONTENT longblob,
        primary key (ID, REV)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SG_EXTENSION (
       ID bigint not null,
        CREATED_ON datetime not null,
        EXTENSION longblob,
        LAST_UPDATED_ON datetime not null,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_SG_EXTENSION_AUD (
       ID bigint not null,
        REV bigint not null,
        REVTYPE tinyint,
        CREATED_ON datetime,
        EXTENSION longblob,
        LAST_UPDATED_ON datetime,
        primary key (ID, REV)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_USER (
       ID bigint not null auto_increment,
        ACTIVE bit not null,
        CREATED_ON datetime not null,
        EMAIL varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        LAST_UPDATED_ON datetime not null,
        PASSWORD varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        PASSWORD_CHANGED datetime,
        ROLE varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        USERNAME varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_USER_AUD (
       ID bigint not null,
        REV bigint not null,
        REVTYPE tinyint,
        ACTIVE bit,
        CREATED_ON datetime,
        EMAIL varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        LAST_UPDATED_ON datetime,
        PASSWORD varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        PASSWORD_CHANGED datetime,
        ROLE varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        USERNAME varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin,
        primary key (ID, REV)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    create table SMP_USER_SEQ (
       next_val bigint
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------------------------------------------------------- 
-- migrate data 
-- --------------------------------------------------------------------------------------------------------- 


-- migrate domains
INSERT INTO SMP_DOMAIN ( DOMAIN_CODE, SIGNATURE_KEY_ALIAS,SML_CLIENT_CERT_HEADER, SML_PARTC_IDENT_REGEXP, SML_SMP_ID, SML_SUBDOMAIN, LAST_UPDATED_ON, CREATED_ON )
   SELECT  domainid, signaturecertalias, bdmslclientcertheader, '', bdmslsmpid,domainid, NOW(), NOW()
      FROM SMP_DOMAIN_BCK;

-- migrate users
INSERT INTO SMP_USER (EMAIL,ACTIVE,CREATED_ON,LAST_UPDATED_ON,USERNAME, PASSWORD,PASSWORD_CHANGED,ROLE)
    SELECT null, 1, NOW(), NOW(), USERNAME, PASSWORD, null, if(isAdmin=0,'SERVICE_GROUP_ADMIN','SMP_ADMIN') 
        FROM SMP_USER_BCK;
-- create certificate records 
INSERT INTO SMP_CERTIFICATE (ID,CERTIFICATE_ID,CREATED_ON,LAST_UPDATED_ON) SELECT ID ,USERNAME, CREATED_ON, LAST_UPDATED_ON  FROM SMP_USER where PASSWORD ='';

-- migrate service groups
INSERT INTO  SMP_SERVICE_GROUP ( CREATED_ON, LAST_UPDATED_ON, PARTICIPANT_IDENTIFIER, PARTICIPANT_SCHEME)
    select  NOW(), NOW(), businessidentifier, businessidentifierscheme from SMP_SERVICE_GROUP_BCK;
-- insert extensions
INSERT INTO SMP_SG_EXTENSION (ID, CREATED_ON, LAST_UPDATED_ON, EXTENSION) 
    select sg.id, NOW(),NOW(), sgb.xmlcontent  from SMP_SERVICE_GROUP sg INNER JOIN  SMP_SERVICE_GROUP_BCK sgb 
    ON sg.PARTICIPANT_IDENTIFIER= sgb.businessidentifier 
        and sg.PARTICIPANT_SCHEME= sgb.businessidentifierscheme WHERE sgb.xmlcontent != '';

-- insert service group domains 
INSERT INTO SMP_SERVICE_GROUP_DOMAIN ( CREATED_ON, LAST_UPDATED_ON, SML_REGISTERED, FK_DOMAIN_ID, FK_SG_ID )
    select  NOW(), NOW(), 0, D.ID, SG.ID from SMP_SERVICE_GROUP_BCK SGB INNER JOIN  SMP_SERVICE_GROUP SG ON
        SGB.businessidentifier = SG.PARTICIPANT_IDENTIFIER
        and SGB.businessidentifierscheme = SG.PARTICIPANT_SCHEME
        INNER JOIN SMP_DOMAIN D ON
         SGB.domainid = D.DOMAIN_CODE;


-- migrate service metadata (on migration there could be only one domain per service group therefore no need for domain)
INSERT INTO  SMP_SERVICE_METADATA ( CREATED_ON, LAST_UPDATED_ON, DOCUMENT_IDENTIFIER, DOCUMENT_SCHEME, FK_SG_DOM_ID)
    select  NOW(), NOW(), MD.documentidentifier,  MD.documentidentifierscheme, SGD.ID
        from SMP_SERVICE_METADATA_BCK MD INNER JOIN SMP_SERVICE_GROUP SG
            ON  MD.businessidentifier = SG.PARTICIPANT_IDENTIFIER and MD.businessidentifierscheme = SG.PARTICIPANT_SCHEME
              INNER JOIN SMP_SERVICE_GROUP_DOMAIN SGD ON SGD.FK_SG_ID = SG.id;
                
-- update service metadata xml
INSERT INTO  SMP_SERVICE_METADATA_XML ( ID, CREATED_ON, LAST_UPDATED_ON, XML_CONTENT)
    select MD.ID, NOW(), NOW(),  MDB.xmlcontent
        from SMP_SERVICE_METADATA_BCK MDB, SMP_SERVICE_GROUP SG, SMP_SERVICE_GROUP_DOMAIN SGD, SMP_SERVICE_METADATA MD
            where MDB.businessidentifier = SG.PARTICIPANT_IDENTIFIER and MDB.businessidentifierscheme = SG.PARTICIPANT_SCHEME
                 and SGD.FK_SG_ID = SG.id -- only one service group domain at migration time
                 and MD.FK_SG_DOM_ID = SGD.id
                 and MDB.documentidentifier = MD.DOCUMENT_IDENTIFIER
                 and MDB.documentidentifierscheme = MD.DOCUMENT_SCHEME;

-- owners
INSERT INTO SMP_OWNERSHIP (FK_SG_ID, FK_USER_ID)
    select SG.ID, U.ID FROM SMP_OWNERSHIP_BCK OB INNER JOIN SMP_SERVICE_GROUP SG ON
       OB.businessidentifier = SG.PARTICIPANT_IDENTIFIER
            and OB.businessidentifierscheme = SG.PARTICIPANT_SCHEME
    INNER JOIN SMP_USER U ON OB.USERNAME =U.USERNAME;
            

 -- we do not need certificate DN in USERNAME so remove it from username columns
UPDATE SMP_USER set USERNAME=null where PASSWORD  ='';

UPDATE SMP_DOMAIN set SML_REGISTERED=0 where SML_REGISTERED IS NULL;
UPDATE SMP_DOMAIN set SML_BLUE_COAT_AUTH=0 where SML_BLUE_COAT_AUTH IS NULL;


-- -------------------------------------------------------------------------------------------------------- 
-- update sequences and remove auto_increment
-- --------------------------------------------------------------------------------------------------------
    alter table SMP_DOMAIN modify column id bigint not null;
    alter table SMP_SERVICE_GROUP modify column id bigint not null;
    alter table SMP_SERVICE_GROUP_DOMAIN modify column id bigint not null;
    alter table SMP_SERVICE_METADATA modify column id bigint not null;
    alter table SMP_USER modify column id bigint not null;

    insert into SMP_USER_SEQ select count(id) +1 from SMP_USER;
    insert into SMP_SERVICE_METADATA_SEQ select count(id) +1 from SMP_SERVICE_METADATA;
    insert into SMP_SERVICE_GROUP_SEQ select count(id) +1 from SMP_SERVICE_GROUP;
    insert into SMP_SERVICE_GROUP_DOMAIN_SEQ select count(id) +1 from SMP_SERVICE_GROUP_DOMAIN;
    insert into SMP_DOMAIN_SEQ select count(id) +1 from SMP_DOMAIN;
    insert into SMP_REVISION_SEQ values ( 1 );
-- -------------------------------------------------------------------------------------------------------- 
-- set indexes
-- --------------------------------------------------------------------------------------------------------


 alter table SMP_CERTIFICATE 
       add constraint UK_3x3rvf6hkim9fg16caurkgg6f unique (CERTIFICATE_ID (300));

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
       references SMP_USER (ID);

    alter table SMP_CERTIFICATE_AUD 
       add constraint FKnrwm8en8vv10li8ihwnurwd9e 
       foreign key (REV) 
       references SMP_REV_INFO (id);

    alter table SMP_DOMAIN_AUD 
       add constraint FK35qm8xmi74kfenugeonijodsg 
       foreign key (REV) 
       references SMP_REV_INFO (id);

    alter table SMP_OWNERSHIP 
       add constraint FKrnqwq06lbfwciup4rj8nvjpmy 
       foreign key (FK_USER_ID) 
       references SMP_USER (ID);

    alter table SMP_OWNERSHIP 
       add constraint FKgexq5n6ftsid8ehqljvjh8p4i 
       foreign key (FK_SG_ID) 
       references SMP_SERVICE_GROUP (ID);

    alter table SMP_OWNERSHIP_AUD 
       add constraint FK1lqynlbk8ow1ouxetf5wybk3k 
       foreign key (REV) 
       references SMP_REV_INFO (id);

    alter table SMP_SERVICE_GROUP_AUD 
       add constraint FKj3caimhegwyav1scpwrxoslef 
       foreign key (REV) 
       references SMP_REV_INFO (id);

    alter table SMP_SERVICE_GROUP_DOMAIN 
       add constraint FKo186xtefda6avl5p1tuqchp3n 
       foreign key (FK_DOMAIN_ID) 
       references SMP_DOMAIN (ID);

    alter table SMP_SERVICE_GROUP_DOMAIN 
       add constraint FKgcvhnk2n34d3c6jhni5l3s3x3 
       foreign key (FK_SG_ID) 
       references SMP_SERVICE_GROUP (ID);

    alter table SMP_SERVICE_GROUP_DOMAIN_AUD 
       add constraint FK6uc9r0eqw16baooxtmqjkih0j 
       foreign key (REV) 
       references SMP_REV_INFO (id);

    alter table SMP_SERVICE_METADATA 
       add constraint FKfvcml6b8x7kn80m30h8pxs7jl 
       foreign key (FK_SG_DOM_ID) 
       references SMP_SERVICE_GROUP_DOMAIN (ID);

    alter table SMP_SERVICE_METADATA_AUD 
       add constraint FKbqr9pdnik1qxx2hi0xn4n7f61 
       foreign key (REV) 
       references SMP_REV_INFO (id);

    alter table SMP_SERVICE_METADATA_XML 
       add constraint FK4b1x06xlavcgbjnuilgksi7nm 
       foreign key (ID) 
       references SMP_SERVICE_METADATA (ID);

    alter table SMP_SERVICE_METADATA_XML_AUD 
       add constraint FKevatmlvvwoxfnjxkvmokkencb 
       foreign key (REV) 
       references SMP_REV_INFO (id);

    alter table SMP_SG_EXTENSION 
       add constraint FKtf0mfonugp2jbkqo2o142chib 
       foreign key (ID) 
       references SMP_SERVICE_GROUP (ID);

    alter table SMP_SG_EXTENSION_AUD 
       add constraint FKmdo9v2422adwyebvl34qa3ap6 
       foreign key (REV) 
       references SMP_REV_INFO (id);

    alter table SMP_USER_AUD 
       add constraint FK2786r5minnkai3d22b191iiiq 
       foreign key (REV) 
       references SMP_REV_INFO (id);

-- -------------------------------------------------------------------------------------------------------- 
-- set indexes
-- --------------------------------------------------------------------------------------------------------

-- remove backup if migration succeeded- do in manually
-- drop table SMP_DOMAIN_BCK;
-- drop table SMP_OWNERSHIP_BCK;
-- drop table SMP_SERVICE_METADATA_BCK;
-- drop table SMP_SERVICE_GROUP_BCK;
-- drop table SMP_USER_BCK;









