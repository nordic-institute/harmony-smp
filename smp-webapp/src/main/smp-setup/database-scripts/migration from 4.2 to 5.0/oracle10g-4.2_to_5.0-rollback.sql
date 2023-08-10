-- drop new tables
drop table SMP_ALERT cascade constraints;
drop table SMP_ALERT_AUD cascade constraints;
drop table SMP_ALERT_PROPERTY cascade constraints;
drop table SMP_ALERT_PROPERTY_AUD cascade constraints;
drop table SMP_CERTIFICATE cascade constraints;
drop table SMP_CERTIFICATE_AUD cascade constraints;
drop table SMP_CONFIGURATION cascade constraints;
drop table SMP_CONFIGURATION_AUD cascade constraints;
drop table SMP_CREDENTIAL cascade constraints;
drop table SMP_CREDENTIAL_AUD cascade constraints;
drop table SMP_DOCUMENT cascade constraints;
drop table SMP_DOCUMENT_AUD cascade constraints;
drop table SMP_DOCUMENT_VERSION cascade constraints;
drop table SMP_DOCUMENT_VERSION_AUD cascade constraints;
drop table SMP_DOMAIN cascade constraints;
drop table SMP_DOMAIN_AUD cascade constraints;
drop table SMP_DOMAIN_MEMBER cascade constraints;
drop table SMP_DOMAIN_MEMBER_AUD cascade constraints;
drop table SMP_DOMAIN_RESOURCE_DEF cascade constraints;
drop table SMP_DOMAIN_RESOURCE_DEF_AUD cascade constraints;
drop table SMP_EXTENSION cascade constraints;
drop table SMP_EXTENSION_AUD cascade constraints;
drop table SMP_GROUP cascade constraints;
drop table SMP_GROUP_AUD cascade constraints;
drop table SMP_GROUP_MEMBER cascade constraints;
drop table SMP_GROUP_MEMBER_AUD cascade constraints;
drop table SMP_RESOURCE cascade constraints;
drop table SMP_RESOURCE_AUD cascade constraints;
drop table SMP_RESOURCE_DEF cascade constraints;
drop table SMP_RESOURCE_DEF_AUD cascade constraints;
drop table SMP_RESOURCE_MEMBER cascade constraints;
drop table SMP_RESOURCE_MEMBER_AUD cascade constraints;
drop table SMP_REV_INFO cascade constraints;
drop table SMP_SUBRESOURCE cascade constraints;
drop table SMP_SUBRESOURCE_AUD cascade constraints;
drop table SMP_SUBRESOURCE_DEF cascade constraints;
drop table SMP_SUBRESOURCE_DEF_AUD cascade constraints;
drop table SMP_USER cascade constraints;
drop table SMP_USER_AUD cascade constraints;
-- rename tables
ALTER TABLE BCK_ALERT RENAME TO SMP_ALERT;
ALTER TABLE BCK_ALERT_AUD RENAME TO SMP_ALERT_AUD;
ALTER TABLE BCK_ALERT_PROPERTY RENAME TO SMP_ALERT_PROPERTY;
ALTER TABLE BCK_ALERT_PROPERTY_AUD RENAME TO SMP_ALERT_PROPERTY_AUD;
ALTER TABLE BCK_CERTIFICATE RENAME TO SMP_CERTIFICATE;
ALTER TABLE BCK_CERTIFICATE_AUD RENAME TO SMP_CERTIFICATE_AUD;
ALTER TABLE BCK_CONFIGURATION RENAME TO SMP_CONFIGURATION;
ALTER TABLE BCK_CONFIGURATION_AUD RENAME TO SMP_CONFIGURATION_AUD;
ALTER TABLE BCK_DOMAIN RENAME TO SMP_DOMAIN;
ALTER TABLE BCK_DOMAIN_AUD RENAME TO SMP_DOMAIN_AUD;
ALTER TABLE BCK_OWNERSHIP RENAME TO SMP_OWNERSHIP;
ALTER TABLE BCK_OWNERSHIP_AUD RENAME TO SMP_OWNERSHIP_AUD;
ALTER TABLE BCK_REV_INFO RENAME TO SMP_REV_INFO;
ALTER TABLE BCK_SERVICE_GROUP RENAME TO SMP_SERVICE_GROUP;
ALTER TABLE BCK_SERVICE_GROUP_AUD RENAME TO SMP_SERVICE_GROUP_AUD;
ALTER TABLE BCK_SERVICE_GROUP_DOMAIN RENAME TO SMP_SERVICE_GROUP_DOMAIN;
ALTER TABLE BCK_SERVICE_GROUP_DOMAIN_AUD RENAME TO SMP_SERVICE_GROUP_DOMAIN_AUD;
ALTER TABLE BCK_SERVICE_METADATA RENAME TO SMP_SERVICE_METADATA;
ALTER TABLE BCK_SERVICE_METADATA_AUD RENAME TO SMP_SERVICE_METADATA_AUD;
ALTER TABLE BCK_SERVICE_METADATA_XML RENAME TO SMP_SERVICE_METADATA_XML;
ALTER TABLE BCK_SERVICE_METADATA_XML_AUD RENAME TO SMP_SERVICE_METADATA_XML_AUD;
ALTER TABLE BCK_SG_EXTENSION RENAME TO SMP_SG_EXTENSION;
ALTER TABLE BCK_SG_EXTENSION_AUD RENAME TO SMP_SG_EXTENSION_AUD;
ALTER TABLE BCK_USER RENAME TO SMP_USER;
ALTER TABLE BCK_USER_AUD RENAME TO SMP_USER_AUD;
-- set indexes
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
   add constraint UK_tk9bjsmd2mevgt3b997i6pl27 unique (ACCESS_TOKEN_ID);

alter table SMP_USER
   add constraint UK_rt1f0anklfo05lt0my05fqq6 unique (USERNAME);

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

alter table SMP_CERTIFICATE
   add constraint FKayqgpj5ot3o8vrpduul7sstta
   foreign key (ID)
   references SMP_USER;

alter table SMP_CERTIFICATE_AUD
   add constraint FKnrwm8en8vv10li8ihwnurwd9e
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_CONFIGURATION_AUD
   add constraint FKd4yhbdlusovfbdti1fjkuxp9m
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

drop sequence SMP_CREDENTIAL_SEQ;
drop sequence SMP_DOCUMENT_SEQ;
drop sequence SMP_DOCUMENT_VERSION_SEQ;
drop sequence SMP_DOMAIN_MEMBER_SEQ;
drop sequence SMP_GROUP_MEMBER_SEQ;
drop sequence SMP_RESOURCE_MEMBER_SEQ;
drop sequence SMP_EXTENSION_SEQ;
drop sequence SMP_RESOURCE_DEF_SEQ;
drop sequence SMP_SUBRESOURCE_DEF_SEQ;
drop sequence SMP_DOMAIN_RESOURCE_DEF_SEQ;
drop sequence SMP_GROUP_SEQ;
drop sequence SMP_RESOURCE_SEQ;
rename SMP_SUBRESOURCE_SEQ TO SMP_SERVICE_METADATA_SEQ;
