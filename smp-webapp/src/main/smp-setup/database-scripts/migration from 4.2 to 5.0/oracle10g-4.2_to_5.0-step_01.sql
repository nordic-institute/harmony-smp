-- drop indexes/constraints
alter table SMP_CERTIFICATE drop constraint UK_3x3rvf6hkim9fg16caurkgg6f;
alter table SMP_DOMAIN drop constraint UK_djrwqd4luj5i7w4l7fueuaqbj;
alter table SMP_DOMAIN drop constraint UK_likb3jn0nlxlekaws0xx10uqc;
drop index SMP_SG_PART_ID_IDX;
drop index SMP_SG_PART_SCH_IDX;
alter table SMP_SERVICE_GROUP drop constraint SMP_SG_UNIQ_PARTC_IDX;
drop index SMP_SMD_DOC_ID_IDX;
drop index SMP_SMD_DOC_SCH_IDX;
alter table SMP_SERVICE_METADATA drop constraint SMP_MT_UNIQ_SG_DOC_IDX;
alter table SMP_USER drop constraint UK_tk9bjsmd2mevgt3b997i6pl27;
alter table SMP_USER drop constraint UK_rt1f0anklfo05lt0my05fqq6;
alter table SMP_ALERT_AUD drop constraint FKrw0qnto448ojlirpfmfntd8v2;
alter table SMP_ALERT_PROPERTY drop constraint FK15r37w3r5ty5f6074ykr2o4i6;
alter table SMP_ALERT_PROPERTY_AUD drop constraint FKod33qjx87ih1a0skxl2sgddar;
alter table SMP_CERTIFICATE drop constraint FKayqgpj5ot3o8vrpduul7sstta;
alter table SMP_CERTIFICATE_AUD drop constraint FKnrwm8en8vv10li8ihwnurwd9e;
alter table SMP_CONFIGURATION_AUD drop constraint FKd4yhbdlusovfbdti1fjkuxp9m;
alter table SMP_DOMAIN_AUD drop constraint FK35qm8xmi74kfenugeonijodsg;
alter table SMP_OWNERSHIP drop constraint FKrnqwq06lbfwciup4rj8nvjpmy;
alter table SMP_OWNERSHIP drop constraint FKgexq5n6ftsid8ehqljvjh8p4i;
alter table SMP_OWNERSHIP_AUD drop constraint FK1lqynlbk8ow1ouxetf5wybk3k;
alter table SMP_SERVICE_GROUP_AUD drop constraint FKj3caimhegwyav1scpwrxoslef;
alter table SMP_SERVICE_GROUP_DOMAIN drop constraint FKo186xtefda6avl5p1tuqchp3n;
alter table SMP_SERVICE_GROUP_DOMAIN drop constraint FKgcvhnk2n34d3c6jhni5l3s3x3;
alter table SMP_SERVICE_GROUP_DOMAIN_AUD drop constraint FK6uc9r0eqw16baooxtmqjkih0j;
alter table SMP_SERVICE_METADATA drop constraint FKfvcml6b8x7kn80m30h8pxs7jl;
alter table SMP_SERVICE_METADATA_AUD drop constraint FKbqr9pdnik1qxx2hi0xn4n7f61;
alter table SMP_SERVICE_METADATA_XML drop constraint FK4b1x06xlavcgbjnuilgksi7nm;
alter table SMP_SERVICE_METADATA_XML_AUD drop constraint FKevatmlvvwoxfnjxkvmokkencb;
alter table SMP_SG_EXTENSION drop constraint FKtf0mfonugp2jbkqo2o142chib;
alter table SMP_SG_EXTENSION_AUD drop constraint FKmdo9v2422adwyebvl34qa3ap6;
alter table SMP_USER_AUD drop constraint FK2786r5minnkai3d22b191iiiq;

-- backup tables
 ALTER TABLE SMP_ALERT RENAME TO BCK_ALERT;
 ALTER TABLE SMP_ALERT_AUD RENAME TO BCK_ALERT_AUD;
 ALTER TABLE SMP_ALERT_PROPERTY RENAME TO BCK_ALERT_PROPERTY;
 ALTER TABLE SMP_ALERT_PROPERTY_AUD RENAME TO BCK_ALERT_PROPERTY_AUD;
 ALTER TABLE SMP_CERTIFICATE RENAME TO BCK_CERTIFICATE;
 ALTER TABLE SMP_CERTIFICATE_AUD RENAME TO BCK_CERTIFICATE_AUD;
 ALTER TABLE SMP_CONFIGURATION RENAME TO BCK_CONFIGURATION;
 ALTER TABLE SMP_CONFIGURATION_AUD RENAME TO BCK_CONFIGURATION_AUD;
 ALTER TABLE SMP_DOMAIN RENAME TO BCK_DOMAIN;
 ALTER TABLE SMP_DOMAIN_AUD RENAME TO BCK_DOMAIN_AUD;
 ALTER TABLE SMP_OWNERSHIP RENAME TO BCK_OWNERSHIP;
 ALTER TABLE SMP_OWNERSHIP_AUD RENAME TO BCK_OWNERSHIP_AUD;
 ALTER TABLE SMP_REV_INFO RENAME TO BCK_REV_INFO;
 ALTER TABLE SMP_SERVICE_GROUP RENAME TO BCK_SERVICE_GROUP;
 ALTER TABLE SMP_SERVICE_GROUP_AUD RENAME TO BCK_SERVICE_GROUP_AUD;
 ALTER TABLE SMP_SERVICE_GROUP_DOMAIN RENAME TO BCK_SERVICE_GROUP_DOMAIN;
 ALTER TABLE SMP_SERVICE_GROUP_DOMAIN_AUD RENAME TO BCK_SERVICE_GROUP_DOMAIN_AUD;
 ALTER TABLE SMP_SERVICE_METADATA RENAME TO BCK_SERVICE_METADATA;
 ALTER TABLE SMP_SERVICE_METADATA_AUD RENAME TO BCK_SERVICE_METADATA_AUD;
 ALTER TABLE SMP_SERVICE_METADATA_XML RENAME TO BCK_SERVICE_METADATA_XML;
 ALTER TABLE SMP_SERVICE_METADATA_XML_AUD RENAME TO BCK_SERVICE_METADATA_XML_AUD;
 ALTER TABLE SMP_SG_EXTENSION RENAME TO BCK_SG_EXTENSION;
 ALTER TABLE SMP_SG_EXTENSION_AUD RENAME TO BCK_SG_EXTENSION_AUD;
 ALTER TABLE SMP_USER RENAME TO BCK_USER;
 ALTER TABLE SMP_USER_AUD RENAME TO BCK_USER_AUD;

-- create new sequences
create sequence SMP_CREDENTIAL_SEQ start with 1 increment by  1;
create sequence SMP_DOCUMENT_SEQ start with 1 increment by  1;
create sequence SMP_DOCUMENT_VERSION_SEQ start with 1 increment by  1;
create sequence SMP_DOMAIN_MEMBER_SEQ start with 1 increment by  1;
create sequence SMP_GROUP_MEMBER_SEQ start with 1 increment by  1;
create sequence SMP_RESOURCE_MEMBER_SEQ start with 1 increment by  1;
-- set manual inserts in v2 script -start with 10
create sequence SMP_EXTENSION_SEQ start with 10 increment by  1;
create sequence SMP_RESOURCE_DEF_SEQ start with 10 increment by  1;
create sequence SMP_SUBRESOURCE_DEF_SEQ start with 10 increment by  1;
-- set SMP_DOMAIN_RESOURCE_DEF_SEQ to start from SMP_DOMAIN_SEQ!
declare
    l_new_seq INTEGER;
    l_new_seqDom INTEGER;
begin
   select SMP_SERVICE_GROUP_DOMAIN_SEQ.nextval into l_new_seq from dual;
   select SMP_DOMAIN_SEQ.nextval into l_new_seqDom from dual;
   execute immediate 'create sequence SMP_DOMAIN_RESOURCE_DEF_SEQ start with ' || l_new_seq || ' increment by 1';
   execute immediate 'create sequence SMP_GROUP_SEQ start with ' || l_new_seqDom || ' increment by 1';
end;
/

--SMP_SERVICE_GROUP_DOMAIN_SEQ
rename SMP_SERVICE_GROUP_SEQ TO SMP_RESOURCE_SEQ;
rename SMP_SERVICE_METADATA_SEQ TO SMP_SUBRESOURCE_SEQ;


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
    PROPERTY_NAME varchar2(255 char),
    PROPERTY_VALUE varchar2(1024 char),
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
    PROPERTY_NAME varchar2(255 char),
    PROPERTY_VALUE varchar2(1024 char),
    FK_ALERT_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_CERTIFICATE (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    CERTIFICATE_ID varchar2(1024 char),
    CRL_URL varchar2(4000 char),
    ISSUER varchar2(1024 char),
    PEM_ENCODED_CERT clob,
    SERIALNUMBER varchar2(128 char),
    SUBJECT varchar2(1024 char),
    VALID_FROM timestamp,
    VALID_TO timestamp,
    primary key (ID)
);

comment on table SMP_CERTIFICATE is
    'SMP user certificates';

comment on column SMP_CERTIFICATE.ID is
    'Shared primary key with master table SMP_CREDENTIAL';

comment on column SMP_CERTIFICATE.CERTIFICATE_ID is
    'Formatted Certificate id using tags: cn, o, c:serialNumber';

comment on column SMP_CERTIFICATE.CRL_URL is
    'URL to the certificate revocation list (CRL)';

comment on column SMP_CERTIFICATE.ISSUER is
    'Certificate issuer (canonical form)';

comment on column SMP_CERTIFICATE.PEM_ENCODED_CERT is
    'PEM encoded  certificate';

comment on column SMP_CERTIFICATE.SERIALNUMBER is
    'Certificate serial number';

comment on column SMP_CERTIFICATE.SUBJECT is
    'Certificate subject (canonical form)';

comment on column SMP_CERTIFICATE.VALID_FROM is
    'Certificate valid from date.';

comment on column SMP_CERTIFICATE.VALID_TO is
    'Certificate valid to date.';

create table SMP_CERTIFICATE_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    CERTIFICATE_ID varchar2(1024 char),
    CRL_URL varchar2(4000 char),
    ISSUER varchar2(1024 char),
    PEM_ENCODED_CERT clob,
    SERIALNUMBER varchar2(128 char),
    SUBJECT varchar2(1024 char),
    VALID_FROM timestamp,
    VALID_TO timestamp,
    primary key (ID, REV)
);

create table SMP_CONFIGURATION (
   PROPERTY_NAME varchar2(512 char) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DESCRIPTION varchar2(4000 char),
    PROPERTY_VALUE varchar2(4000 char),
    primary key (PROPERTY_NAME)
);

comment on table SMP_CONFIGURATION is
    'SMP user certificates';

comment on column SMP_CONFIGURATION.PROPERTY_NAME is
    'Property name/key';

comment on column SMP_CONFIGURATION.DESCRIPTION is
    'Property description';

comment on column SMP_CONFIGURATION.PROPERTY_VALUE is
    'Property value';

create table SMP_CONFIGURATION_AUD (
   PROPERTY_NAME varchar2(512 char) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar2(4000 char),
    PROPERTY_VALUE varchar2(4000 char),
    primary key (PROPERTY_NAME, REV)
);

create table SMP_CREDENTIAL (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    CREDENTIAL_ACTIVE number(1,0) not null,
    ACTIVE_FROM timestamp,
    CHANGED_ON timestamp,
    CREDENTIAL_TARGET varchar2(255 char) not null,
    CREDENTIAL_TYPE varchar2(255 char) not null,
    CREDENTIAL_DESC varchar2(256 char),
    LAST_ALERT_ON timestamp,
    EXPIRE_ON timestamp,
    LAST_FAILED_LOGIN_ON timestamp,
    CREDENTIAL_NAME varchar2(256 char) not null,
    LOGIN_FAILURE_COUNT number(10,0),
    CREDENTIAL_VALUE varchar2(256 char),
    FK_USER_ID number(19,0) not null,
    primary key (ID)
);

comment on table SMP_CREDENTIAL is
    'Credentials for the users';

comment on column SMP_CREDENTIAL.ID is
    'Unique id';

comment on column SMP_CREDENTIAL.CREDENTIAL_ACTIVE is
    'Is credential active';

comment on column SMP_CREDENTIAL.ACTIVE_FROM is
    'Date when credential starts to be active';

comment on column SMP_CREDENTIAL.CHANGED_ON is
    'Last date when credential was changed';

comment on column SMP_CREDENTIAL.CREDENTIAL_TARGET is
    'Credential target UI, API';

comment on column SMP_CREDENTIAL.CREDENTIAL_TYPE is
    'Credential type:  USERNAME, ACCESS_TOKEN, CERTIFICATE, CAS';

comment on column SMP_CREDENTIAL.CREDENTIAL_DESC is
    'Credential description';

comment on column SMP_CREDENTIAL.LAST_ALERT_ON is
    'Generated last password expire alert';

comment on column SMP_CREDENTIAL.EXPIRE_ON is
    'Date when password will expire';

comment on column SMP_CREDENTIAL.LAST_FAILED_LOGIN_ON is
    'Last failed login attempt';

comment on column SMP_CREDENTIAL.CREDENTIAL_NAME is
    'Unique username identifier. The Username must not be null';

comment on column SMP_CREDENTIAL.LOGIN_FAILURE_COUNT is
    'Sequential login failure count';

comment on column SMP_CREDENTIAL.CREDENTIAL_VALUE is
    'Credential value - it can be encrypted value';

create table SMP_CREDENTIAL_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    CREDENTIAL_ACTIVE number(1,0),
    ACTIVE_FROM timestamp,
    CHANGED_ON timestamp,
    CREDENTIAL_TARGET varchar2(255 char),
    CREDENTIAL_TYPE varchar2(255 char),
    CREDENTIAL_DESC varchar2(256 char),
    LAST_ALERT_ON timestamp,
    EXPIRE_ON timestamp,
    LAST_FAILED_LOGIN_ON timestamp,
    CREDENTIAL_NAME varchar2(256 char),
    LOGIN_FAILURE_COUNT number(10,0),
    CREDENTIAL_VALUE varchar2(256 char),
    FK_USER_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_DOCUMENT (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    CURRENT_VERSION number(10,0) not null,
    MIME_TYPE varchar2(128 char),
    NAME varchar2(255 char),
    primary key (ID)
);

comment on table SMP_DOCUMENT is
    'SMP document entity for resources and subresources';

comment on column SMP_DOCUMENT.ID is
    'Unique document id';

create table SMP_DOCUMENT_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    CURRENT_VERSION number(10,0),
    MIME_TYPE varchar2(128 char),
    NAME varchar2(255 char),
    primary key (ID, REV)
);

create table SMP_DOCUMENT_VERSION (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DOCUMENT_CONTENT blob,
    VERSION number(10,0) not null,
    FK_DOCUMENT_ID number(19,0),
    primary key (ID)
);

comment on table SMP_DOCUMENT_VERSION is
    'Document content for the document version.';

comment on column SMP_DOCUMENT_VERSION.ID is
    'Unique version document id';

comment on column SMP_DOCUMENT_VERSION.DOCUMENT_CONTENT is
    'Document content';

create table SMP_DOCUMENT_VERSION_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DOCUMENT_CONTENT blob,
    VERSION number(10,0),
    FK_DOCUMENT_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_DOMAIN (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DEFAULT_RESOURCE_IDENTIFIER varchar2(255 char),
    DOMAIN_CODE varchar2(256 char) not null,
    SIGNATURE_ALGORITHM varchar2(256 char),
    SIGNATURE_DIGEST_METHOD varchar2(256 char),
    SIGNATURE_KEY_ALIAS varchar2(256 char),
    SML_CLIENT_CERT_AUTH number(1,0) not null,
    SML_CLIENT_KEY_ALIAS varchar2(256 char),
    SML_REGISTERED number(1,0) not null,
    SML_SMP_ID varchar2(256 char),
    SML_SUBDOMAIN varchar2(256 char),
    VISIBILITY varchar2(64 char),
    primary key (ID)
);

comment on table SMP_DOMAIN is
    'SMP can handle multiple domains. This table contains domain specific data';

comment on column SMP_DOMAIN.ID is
    'Unique domain id';

comment on column SMP_DOMAIN.DEFAULT_RESOURCE_IDENTIFIER is
    'Default resourceType code';

comment on column SMP_DOMAIN.DOMAIN_CODE is
    'Domain code used as http parameter in rest webservices';

comment on column SMP_DOMAIN.SIGNATURE_ALGORITHM is
    'Set signature algorithm. Ex.: http://www.w3.org/2001/04/xmldsig-more#rsa-sha256';

comment on column SMP_DOMAIN.SIGNATURE_DIGEST_METHOD is
    'Set signature hash method. Ex.: http://www.w3.org/2001/04/xmlenc#sha256';

comment on column SMP_DOMAIN.SIGNATURE_KEY_ALIAS is
    'Signature key alias used for SML integration';

comment on column SMP_DOMAIN.SML_CLIENT_CERT_AUTH is
    'Flag for SML authentication type - use ClientCert header or  HTTPS ClientCertificate (key)';

comment on column SMP_DOMAIN.SML_CLIENT_KEY_ALIAS is
    'Client key alias used for SML integration';

comment on column SMP_DOMAIN.SML_REGISTERED is
    'Flag for: Is domain registered in SML';

comment on column SMP_DOMAIN.SML_SMP_ID is
    'SMP ID used for SML integration';

comment on column SMP_DOMAIN.SML_SUBDOMAIN is
    'SML subdomain';

comment on column SMP_DOMAIN.VISIBILITY is
    'The visibility of the domain: PUBLIC, INTERNAL';

create table SMP_DOMAIN_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DEFAULT_RESOURCE_IDENTIFIER varchar2(255 char),
    DOMAIN_CODE varchar2(256 char),
    SIGNATURE_ALGORITHM varchar2(256 char),
    SIGNATURE_DIGEST_METHOD varchar2(256 char),
    SIGNATURE_KEY_ALIAS varchar2(256 char),
    SML_CLIENT_CERT_AUTH number(1,0),
    SML_CLIENT_KEY_ALIAS varchar2(256 char),
    SML_REGISTERED number(1,0),
    SML_SMP_ID varchar2(256 char),
    SML_SUBDOMAIN varchar2(256 char),
    VISIBILITY varchar2(64 char),
    primary key (ID, REV)
);

create table SMP_DOMAIN_MEMBER (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    MEMBERSHIP_ROLE varchar2(64 char),
    FK_DOMAIN_ID number(19,0),
    FK_USER_ID number(19,0),
    primary key (ID)
);

create table SMP_DOMAIN_MEMBER_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    MEMBERSHIP_ROLE varchar2(64 char),
    FK_DOMAIN_ID number(19,0),
    FK_USER_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_DOMAIN_RESOURCE_DEF (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    FK_DOMAIN_ID number(19,0),
    FK_RESOURCE_DEF_ID number(19,0),
    primary key (ID)
);

create table SMP_DOMAIN_RESOURCE_DEF_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    FK_DOMAIN_ID number(19,0),
    FK_RESOURCE_DEF_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_EXTENSION (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DESCRIPTION varchar2(512 char),
    IDENTIFIER varchar2(128 char),
    IMPLEMENTATION_NAME varchar2(512 char),
    NAME varchar2(128 char),
    VERSION varchar2(128 char),
    primary key (ID)
);

comment on table SMP_EXTENSION is
    'SMP extension definitions';

comment on column SMP_EXTENSION.ID is
    'Unique extension id';

create table SMP_EXTENSION_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar2(512 char),
    IDENTIFIER varchar2(128 char),
    IMPLEMENTATION_NAME varchar2(512 char),
    NAME varchar2(128 char),
    VERSION varchar2(128 char),
    primary key (ID, REV)
);

create table SMP_GROUP (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DESCRIPTION varchar2(4000 char),
    NAME varchar2(512 char) not null,
    VISIBILITY varchar2(128 char),
    FK_DOMAIN_ID number(19,0) not null,
    primary key (ID)
);

comment on table SMP_GROUP is
    'The group spans the resources belonging to the domain group.';

comment on column SMP_GROUP.ID is
    'Unique domain group id';

comment on column SMP_GROUP.DESCRIPTION is
    'Domain Group description';

comment on column SMP_GROUP.NAME is
    'Domain Group name';

create table SMP_GROUP_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar2(4000 char),
    NAME varchar2(512 char),
    VISIBILITY varchar2(128 char),
    FK_DOMAIN_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_GROUP_MEMBER (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    MEMBERSHIP_ROLE varchar2(64 char),
    FK_GROUP_ID number(19,0),
    FK_USER_ID number(19,0),
    primary key (ID)
);

create table SMP_GROUP_MEMBER_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    MEMBERSHIP_ROLE varchar2(64 char),
    FK_GROUP_ID number(19,0),
    FK_USER_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_RESOURCE (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    IDENTIFIER_SCHEME varchar2(256 char),
    IDENTIFIER_VALUE varchar2(256 char) not null,
    SML_REGISTERED number(1,0) not null,
    VISIBILITY varchar2(128 char),
    FK_DOCUMENT_ID number(19,0) not null,
    FK_DOREDEF_ID number(19,0) not null,
    FK_GROUP_ID number(19,0),
    primary key (ID)
);

comment on table SMP_RESOURCE is
    'SMP resource Identifier and scheme';

comment on column SMP_RESOURCE.ID is
    'Unique ServiceGroup id';

create table SMP_RESOURCE_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    IDENTIFIER_SCHEME varchar2(256 char),
    IDENTIFIER_VALUE varchar2(256 char),
    SML_REGISTERED number(1,0),
    VISIBILITY varchar2(128 char),
    FK_DOCUMENT_ID number(19,0),
    FK_DOREDEF_ID number(19,0),
    FK_GROUP_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_RESOURCE_DEF (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DESCRIPTION varchar2(512 char),
    HANDLER_IMPL_NAME varchar2(512 char),
    IDENTIFIER varchar2(128 char),
    MIME_TYPE varchar2(128 char),
    NAME varchar2(128 char),
    URL_SEGMENT varchar2(128 char),
    FK_EXTENSION_ID number(19,0),
    primary key (ID)
);

comment on table SMP_RESOURCE_DEF is
    'SMP extension resource definitions';

comment on column SMP_RESOURCE_DEF.ID is
    'Unique id';

comment on column SMP_RESOURCE_DEF.URL_SEGMENT is
    'resources are published under url_segment.';

create table SMP_RESOURCE_DEF_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar2(512 char),
    HANDLER_IMPL_NAME varchar2(512 char),
    IDENTIFIER varchar2(128 char),
    MIME_TYPE varchar2(128 char),
    NAME varchar2(128 char),
    URL_SEGMENT varchar2(128 char),
    FK_EXTENSION_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_RESOURCE_MEMBER (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    MEMBERSHIP_ROLE varchar2(64 char),
    FK_RESOURCE_ID number(19,0),
    FK_USER_ID number(19,0),
    primary key (ID)
);

create table SMP_RESOURCE_MEMBER_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    MEMBERSHIP_ROLE varchar2(64 char),
    FK_RESOURCE_ID number(19,0),
    FK_USER_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_REV_INFO (
   id number(19,0) not null,
    REVISION_DATE timestamp,
    timestamp number(19,0) not null,
    USERNAME varchar2(255 char),
    primary key (id)
);

create table SMP_SUBRESOURCE (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    IDENTIFIER_SCHEME varchar2(500 char),
    IDENTIFIER_VALUE varchar2(500 char) not null,
    FK_DOCUMENT_ID number(19,0),
    FK_RESOURCE_ID number(19,0) not null,
    FK_SUREDEF_ID number(19,0) not null,
    primary key (ID)
);

comment on table SMP_SUBRESOURCE is
    'Service metadata';

comment on column SMP_SUBRESOURCE.ID is
    'Shared primary key with master table SMP_SUBRESOURCE';

create table SMP_SUBRESOURCE_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    IDENTIFIER_SCHEME varchar2(500 char),
    IDENTIFIER_VALUE varchar2(500 char),
    FK_DOCUMENT_ID number(19,0),
    FK_RESOURCE_ID number(19,0),
    FK_SUREDEF_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_SUBRESOURCE_DEF (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    DESCRIPTION varchar2(128 char),
    HANDLER_IMPL_NAME varchar2(512 char),
    IDENTIFIER varchar2(128 char),
    MIME_TYPE varchar2(128 char),
    NAME varchar2(128 char),
    URL_SEGMENT varchar2(64 char),
    FK_RESOURCE_DEF_ID number(19,0),
    primary key (ID)
);

comment on table SMP_SUBRESOURCE_DEF is
    'SMP extension subresource definitions';

comment on column SMP_SUBRESOURCE_DEF.ID is
    'Unique id';

comment on column SMP_SUBRESOURCE_DEF.URL_SEGMENT is
    'Subresources are published under url_segment. It must be unique for resource type';

create table SMP_SUBRESOURCE_DEF_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    DESCRIPTION varchar2(128 char),
    HANDLER_IMPL_NAME varchar2(512 char),
    IDENTIFIER varchar2(128 char),
    MIME_TYPE varchar2(128 char),
    NAME varchar2(128 char),
    URL_SEGMENT varchar2(64 char),
    FK_RESOURCE_DEF_ID number(19,0),
    primary key (ID, REV)
);

create table SMP_USER (
   ID number(19,0) not null,
    CREATED_ON timestamp not null,
    LAST_UPDATED_ON timestamp not null,
    ACTIVE number(1,0) not null,
    APPLICATION_ROLE varchar2(256 char),
    EMAIL varchar2(128 char),
    FULL_NAME varchar2(128 char),
    SMP_LOCALE varchar2(64 char),
    SMP_THEME varchar2(64 char),
    USERNAME varchar2(256 char) not null,
    primary key (ID)
);

comment on table SMP_USER is
    'SMP can handle multiple domains. This table contains domain specific data';

comment on column SMP_USER.ID is
    'Unique user id';

comment on column SMP_USER.ACTIVE is
    'Is user active';

comment on column SMP_USER.APPLICATION_ROLE is
    'User application role as USER, SYSTEM_ADMIN';

comment on column SMP_USER.EMAIL is
    'User email';

comment on column SMP_USER.FULL_NAME is
    'User full name (name and lastname)';

comment on column SMP_USER.SMP_LOCALE is
    'DomiSMP settings: locale for the user';

comment on column SMP_USER.SMP_THEME is
    'DomiSMP settings: theme for the user';

comment on column SMP_USER.USERNAME is
    'Unique username identifier. The Username must not be null';

create table SMP_USER_AUD (
   ID number(19,0) not null,
    REV number(19,0) not null,
    REVTYPE number(3,0),
    CREATED_ON timestamp,
    LAST_UPDATED_ON timestamp,
    ACTIVE number(1,0),
    APPLICATION_ROLE varchar2(256 char),
    EMAIL varchar2(128 char),
    FULL_NAME varchar2(128 char),
    SMP_LOCALE varchar2(64 char),
    SMP_THEME varchar2(64 char),
    USERNAME varchar2(256 char),
    primary key (ID, REV)
);

alter table SMP_CERTIFICATE
   add constraint UK_3x3rvf6hkim9fg16caurkgg6f unique (CERTIFICATE_ID);

alter table SMP_CREDENTIAL
   add constraint SMP_CRD_USER_NAME_TYPE_IDX unique (CREDENTIAL_NAME, CREDENTIAL_TYPE, CREDENTIAL_TARGET);
create index SMP_DOCVER_DOCUMENT_IDX on SMP_DOCUMENT_VERSION (FK_DOCUMENT_ID);

alter table SMP_DOCUMENT_VERSION
   add constraint SMP_DOCVER_UNIQ_VERSION_IDX unique (FK_DOCUMENT_ID, VERSION);

alter table SMP_DOMAIN
   add constraint UK_djrwqd4luj5i7w4l7fueuaqbj unique (DOMAIN_CODE);

alter table SMP_DOMAIN
   add constraint UK_likb3jn0nlxlekaws0xx10uqc unique (SML_SUBDOMAIN);

alter table SMP_DOMAIN_MEMBER
   add constraint SMP_DOM_MEM_IDX unique (FK_DOMAIN_ID, FK_USER_ID);

alter table SMP_DOMAIN_RESOURCE_DEF
   add constraint SMP_DOREDEF_UNIQ_DOM_RD_IDX unique (FK_RESOURCE_DEF_ID, FK_DOMAIN_ID);

alter table SMP_EXTENSION
   add constraint SMP_EXT_UNIQ_NAME_IDX unique (IMPLEMENTATION_NAME);

alter table SMP_EXTENSION
   add constraint UK_p4vfhgs7fvuo6uebjsuqxrglg unique (IDENTIFIER);

alter table SMP_GROUP
   add constraint SMP_GRP_UNIQ_DOM_IDX unique (NAME, FK_DOMAIN_ID);

alter table SMP_GROUP_MEMBER
   add constraint SMP_GRP_MEM_IDX unique (FK_GROUP_ID, FK_USER_ID);
create index SMP_RS_ID_IDX on SMP_RESOURCE (IDENTIFIER_VALUE);
create index SMP_RS_SCH_IDX on SMP_RESOURCE (IDENTIFIER_SCHEME);

alter table SMP_RESOURCE
   add constraint SMP_RS_UNIQ_IDENT_DOREDEF_IDX unique (IDENTIFIER_SCHEME, IDENTIFIER_VALUE, FK_DOREDEF_ID);

alter table SMP_RESOURCE_DEF
   add constraint SMP_RESDEF_UNIQ_EXTID_CODE_IDX unique (FK_EXTENSION_ID, IDENTIFIER);

alter table SMP_RESOURCE_DEF
   add constraint UK_k7l5fili2mmhgslv77afg4myo unique (IDENTIFIER);

alter table SMP_RESOURCE_DEF
   add constraint UK_jjbctkhd4h0u9whb1i9wbxwoe unique (URL_SEGMENT);

alter table SMP_RESOURCE_MEMBER
   add constraint SMP_RES_MEM_IDX unique (FK_RESOURCE_ID, FK_USER_ID);
create index SMP_SMD_DOC_ID_IDX on SMP_SUBRESOURCE (IDENTIFIER_VALUE);
create index SMP_SMD_DOC_SCH_IDX on SMP_SUBRESOURCE (IDENTIFIER_SCHEME);

alter table SMP_SUBRESOURCE
   add constraint SMP_SRS_UNIQ_ID_RES_SRT_IDX unique (FK_RESOURCE_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME);

alter table SMP_SUBRESOURCE_DEF
   add constraint SMP_RD_UNIQ_RDID_UCTX_IDX unique (FK_RESOURCE_DEF_ID, URL_SEGMENT);

alter table SMP_SUBRESOURCE_DEF
   add constraint UK_pmdcnfwm5in2q9ky0b6dlgqvi unique (IDENTIFIER);

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
   add constraint FK25b9apuupvmjp18wnn2b2gfg8
   foreign key (ID)
   references SMP_CREDENTIAL;

alter table SMP_CERTIFICATE_AUD
   add constraint FKnrwm8en8vv10li8ihwnurwd9e
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_CONFIGURATION_AUD
   add constraint FKd4yhbdlusovfbdti1fjkuxp9m
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_CREDENTIAL
   add constraint FK89it2lyqvi2bl9bettx66n8n1
   foreign key (FK_USER_ID)
   references SMP_USER;

alter table SMP_CREDENTIAL_AUD
   add constraint FKqjh6vxvb5tg0tvbkvi3k3xhe6
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_DOCUMENT_AUD
   add constraint FKh9epnme26i271eixtvrpqejvi
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_DOCUMENT_VERSION
   add constraint FKalsuoqx4csyp9mygvng911do
   foreign key (FK_DOCUMENT_ID)
   references SMP_DOCUMENT;

alter table SMP_DOCUMENT_VERSION_AUD
   add constraint FK4glqiu73939kpyyb6bhw822k3
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_DOMAIN_AUD
   add constraint FK35qm8xmi74kfenugeonijodsg
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_DOMAIN_MEMBER
   add constraint FK1tdwy9oiyrk6tl4mk0fakhkf5
   foreign key (FK_DOMAIN_ID)
   references SMP_DOMAIN;

alter table SMP_DOMAIN_MEMBER
   add constraint FKino2nvj74wc755nyn5mo260qi
   foreign key (FK_USER_ID)
   references SMP_USER;

alter table SMP_DOMAIN_MEMBER_AUD
   add constraint FKijiv1avufqo9iu5u0cj4v3pv7
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_DOMAIN_RESOURCE_DEF
   add constraint FK563xw5tjw4rlr32va9g17cdsq
   foreign key (FK_DOMAIN_ID)
   references SMP_DOMAIN;

alter table SMP_DOMAIN_RESOURCE_DEF
   add constraint FKtppp16v40ll2ch3ly8xusb8hi
   foreign key (FK_RESOURCE_DEF_ID)
   references SMP_RESOURCE_DEF;

alter table SMP_DOMAIN_RESOURCE_DEF_AUD
   add constraint FKpujj9vb097i5w4loa3dxww2nj
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_EXTENSION_AUD
   add constraint FKke7f9wbwvp1bmnlqh9hrfm0r
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_GROUP
   add constraint FKjeomxyxjueaiyt7f0he0ls7vm
   foreign key (FK_DOMAIN_ID)
   references SMP_DOMAIN;

alter table SMP_GROUP_AUD
   add constraint FKeik3quor2dxho7bmyoxc2ug9o
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_GROUP_MEMBER
   add constraint FK3y21chrphgx1dytux0p19btxe
   foreign key (FK_GROUP_ID)
   references SMP_GROUP;

alter table SMP_GROUP_MEMBER
   add constraint FK8ue5gj1rx6gyiqp19dscp85ut
   foreign key (FK_USER_ID)
   references SMP_USER;

alter table SMP_GROUP_MEMBER_AUD
   add constraint FK5pmorcyhwkaysh0a8xm99x6a8
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_RESOURCE
   add constraint FKkc5a6okrvq7dv87itfp7i1vmv
   foreign key (FK_DOCUMENT_ID)
   references SMP_DOCUMENT;

alter table SMP_RESOURCE
   add constraint FK24mw8fiua39nh8rnobhgmujri
   foreign key (FK_DOREDEF_ID)
   references SMP_DOMAIN_RESOURCE_DEF;

alter table SMP_RESOURCE
   add constraint FKft55kasui36i77inf0wh8utv5
   foreign key (FK_GROUP_ID)
   references SMP_GROUP;

alter table SMP_RESOURCE_AUD
   add constraint FKlbbfltxw6qmph5w3i8c9qf6kb
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_RESOURCE_DEF
   add constraint FKruu7v6uig9h333ihv34haw3ob
   foreign key (FK_EXTENSION_ID)
   references SMP_EXTENSION;

alter table SMP_RESOURCE_DEF_AUD
   add constraint FKapswkgbdm9s4wwhx2cjduoniw
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_RESOURCE_MEMBER
   add constraint FKrci5jlgnckwo1mhq2rvmfaptw
   foreign key (FK_RESOURCE_ID)
   references SMP_RESOURCE;

alter table SMP_RESOURCE_MEMBER
   add constraint FKs6jx68jxlx4xfdtxy20f3s6lu
   foreign key (FK_USER_ID)
   references SMP_USER;

alter table SMP_RESOURCE_MEMBER_AUD
   add constraint FKknykp2wcby9fxk234yaaix1pe
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_SUBRESOURCE
   add constraint FK7y1ydnq350mbs3c8yrq2fhnsk
   foreign key (FK_DOCUMENT_ID)
   references SMP_DOCUMENT;

alter table SMP_SUBRESOURCE
   add constraint FK7clbsapruvhkcqgekfxs8prex
   foreign key (FK_RESOURCE_ID)
   references SMP_RESOURCE;

alter table SMP_SUBRESOURCE
   add constraint FKq3wmyy4ieoenuu1s55237qu9k
   foreign key (FK_SUREDEF_ID)
   references SMP_SUBRESOURCE_DEF;

alter table SMP_SUBRESOURCE_AUD
   add constraint FKffihyo233ldee8nejbkyclrov
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_SUBRESOURCE_DEF
   add constraint FKbjqilcym6p3pptva2s4d1gw8o
   foreign key (FK_RESOURCE_DEF_ID)
   references SMP_RESOURCE_DEF;

alter table SMP_SUBRESOURCE_DEF_AUD
   add constraint FK1dd2l0ujtncg9u7hl3c4rte63
   foreign key (REV)
   references SMP_REV_INFO;

alter table SMP_USER_AUD
   add constraint FK2786r5minnkai3d22b191iiiq
   foreign key (REV)
   references SMP_REV_INFO;
