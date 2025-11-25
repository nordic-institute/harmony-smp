-- This is [CREATE] database script for DomiSML version: [5.2-RC2-SNAPSHOT].
-- This file was generated using hibernate version [6.6.37.Final] with dialect [org.hibernate.dialect.OracleDialect].
-- For more information, refer to the Hibernate dialect documentation.

    create sequence SMP_ALERT_PROP_SEQ start with 1 increment by 1;

    create sequence SMP_ALERT_SEQ start with 1 increment by 1;

    create sequence SMP_CREDENTIAL_SEQ start with 1 increment by 1;

    create sequence SMP_DOC_PROP_SEQ start with 1 increment by 1;

    create sequence SMP_DOCUMENT_SEQ start with 1 increment by 1;

    create sequence SMP_DOCUMENT_VERSION_SEQ start with 1 increment by 1;

    create sequence SMP_DOCVER_EVENT_SEQ start with 1 increment by 1;

    create sequence SMP_DOMAIN_CONF_SEQ start with 1 increment by 1;

    create sequence SMP_DOMAIN_DOC_TMPL_SEQ start with 1 increment by 1;

    create sequence SMP_DOMAIN_MEMBER_SEQ start with 1 increment by 1;

    create sequence SMP_DOMAIN_RESOURCE_DEF_SEQ start with 1 increment by 1;

    create sequence SMP_DOMAIN_SEQ start with 1 increment by 1;

    create sequence SMP_EXTENSION_SEQ start with 1 increment by 1;

    create sequence SMP_GROUP_MEMBER_SEQ start with 1 increment by 1;

    create sequence SMP_GROUP_SEQ start with 1 increment by 1;

    create sequence SMP_PERIODICAL_ALERT_SEQ start with 1 increment by 1;

    create sequence SMP_RESOURCE_DEF_SEQ start with 1 increment by 1;

    create sequence SMP_RESOURCE_MEMBER_SEQ start with 1 increment by 1;

    create sequence SMP_RESOURCE_SEQ start with 1 increment by 1;

    create sequence SMP_REVISION_SEQ start with 1 increment by 1;

    create sequence SMP_SUBRESOURCE_DEF_SEQ start with 1 increment by 1;

    create sequence SMP_SUBRESOURCE_SEQ start with 1 increment by 1;

    create sequence SMP_USER_SEQ start with 1 increment by 1;

    create table SMP_ALERT (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        ALERT_LEVEL varchar2(255 char) check (ALERT_LEVEL in ('HIGH','MEDIUM','LOW')),
        ALERT_STATUS varchar2(255 char) check (ALERT_STATUS in ('PROCESS','SUCCESS','FAILED')),
        ALERT_STATUS_DESC varchar2(1024 char),
        ALERT_TYPE varchar2(255 char) check (ALERT_TYPE in ('TEST_ALERT','CREDENTIAL_IMMINENT_EXPIRATION','CREDENTIAL_EXPIRED','CREDENTIAL_SUSPENDED','CREDENTIAL_VERIFICATION_FAILED','CREDENTIAL_REQUEST_RESET','CREDENTIAL_CHANGED','SYSTEM_CERTIFICATE_IMMINENT_EXPIRATION','SYSTEM_CERTIFICATE_EXPIRED','USER_CREATED_CONFIRMATION','USER_CREATED','USER_UPDATED','USER_CREATED_EU_LOGIN','RESOURCE_DOCUMENT_ACTION','SUBRESOURCE_DOCUMENT_ACTION','RESOURCE_DOCUMENT_REVIEW_ACTION','SUBRESOURCE_DOCUMENT_REVIEW_ACTION')),
        MAIL_SUBJECT varchar2(1024 char),
        MAIL_TO varchar2(1024 char),
        PROCESSED_TIME timestamp(6) with time zone,
        REPORTING_TIME timestamp(6) with time zone,
        FOR_USERNAME varchar2(64 char),
        primary key (ID)
    );

    comment on column SMP_ALERT.ID is
        'Unique alert id';

    create table SMP_ALERT_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        ALERT_LEVEL varchar2(255 char) check (ALERT_LEVEL in ('HIGH','MEDIUM','LOW')),
        ALERT_STATUS varchar2(255 char) check (ALERT_STATUS in ('PROCESS','SUCCESS','FAILED')),
        ALERT_STATUS_DESC varchar2(1024 char),
        ALERT_TYPE varchar2(255 char) check (ALERT_TYPE in ('TEST_ALERT','CREDENTIAL_IMMINENT_EXPIRATION','CREDENTIAL_EXPIRED','CREDENTIAL_SUSPENDED','CREDENTIAL_VERIFICATION_FAILED','CREDENTIAL_REQUEST_RESET','CREDENTIAL_CHANGED','SYSTEM_CERTIFICATE_IMMINENT_EXPIRATION','SYSTEM_CERTIFICATE_EXPIRED','USER_CREATED_CONFIRMATION','USER_CREATED','USER_UPDATED','USER_CREATED_EU_LOGIN','RESOURCE_DOCUMENT_ACTION','SUBRESOURCE_DOCUMENT_ACTION','RESOURCE_DOCUMENT_REVIEW_ACTION','SUBRESOURCE_DOCUMENT_REVIEW_ACTION')),
        MAIL_SUBJECT varchar2(1024 char),
        MAIL_TO varchar2(1024 char),
        PROCESSED_TIME timestamp(6) with time zone,
        REPORTING_TIME timestamp(6) with time zone,
        FOR_USERNAME varchar2(64 char),
        primary key (REV, ID)
    );

    create table SMP_ALERT_PROPERTY (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
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
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        PROPERTY_NAME varchar2(255 char),
        PROPERTY_VALUE varchar2(1024 char),
        FK_ALERT_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_CERTIFICATE (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        CERTIFICATE_ID varchar2(1024 char) unique,
        CRL_URL varchar2(4000 char),
        ISSUER varchar2(1024 char),
        PEM_ENCODED_CERT clob,
        SERIALNUMBER varchar2(128 char),
        SUBJECT varchar2(1024 char),
        VALID_FROM timestamp(6) with time zone,
        VALID_TO timestamp(6) with time zone,
        primary key (ID)
    );

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
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        CERTIFICATE_ID varchar2(1024 char),
        CRL_URL varchar2(4000 char),
        ISSUER varchar2(1024 char),
        PEM_ENCODED_CERT clob,
        SERIALNUMBER varchar2(128 char),
        SUBJECT varchar2(1024 char),
        VALID_FROM timestamp(6) with time zone,
        VALID_TO timestamp(6) with time zone,
        primary key (REV, ID)
    );

    create table SMP_CONFIGURATION (
        PROPERTY_NAME varchar2(512 char) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DESCRIPTION varchar2(4000 char),
        PROPERTY_VALUE varchar2(4000 char),
        primary key (PROPERTY_NAME)
    );

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
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DESCRIPTION varchar2(4000 char),
        PROPERTY_VALUE varchar2(4000 char),
        primary key (REV, PROPERTY_NAME)
    );

    create table SMP_CREDENTIAL (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        CREDENTIAL_ACTIVE number(1,0) not null check (CREDENTIAL_ACTIVE in (0,1)),
        ACTIVE_FROM timestamp(6) with time zone,
        CHANGED_ON timestamp(6) with time zone,
        CREDENTIAL_TARGET varchar2(255 char) not null check (CREDENTIAL_TARGET in ('UI','REST_API')),
        CREDENTIAL_TYPE varchar2(255 char) not null check (CREDENTIAL_TYPE in ('USERNAME_PASSWORD','ACCESS_TOKEN','CERTIFICATE','CAS')),
        CREDENTIAL_DESC varchar2(256 char),
        LAST_ALERT_ON timestamp(6) with time zone,
        EXPIRE_ON timestamp(6) with time zone,
        LAST_FAILED_LOGIN_ON timestamp(6) with time zone,
        CREDENTIAL_NAME varchar2(256 char) not null,
        RESET_EXPIRE_ON timestamp(6) with time zone,
        RESET_TOKEN varchar2(256 char),
        LOGIN_FAILURE_COUNT number(10,0),
        CREDENTIAL_VALUE varchar2(256 char),
        FK_USER_ID number(19,0) not null,
        primary key (ID),
        constraint SMP_CRD_USER_NAME_TYPE_IDX unique (CREDENTIAL_NAME, CREDENTIAL_TYPE, CREDENTIAL_TARGET)
    );

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

    comment on column SMP_CREDENTIAL.RESET_EXPIRE_ON is
        'Date time when reset token will expire';

    comment on column SMP_CREDENTIAL.RESET_TOKEN is
        'Reset token for credential reset';

    comment on column SMP_CREDENTIAL.LOGIN_FAILURE_COUNT is
        'Sequential login failure count';

    comment on column SMP_CREDENTIAL.CREDENTIAL_VALUE is
        'Credential value - it can be encrypted value';

    create table SMP_CREDENTIAL_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        CREDENTIAL_ACTIVE number(1,0) check (CREDENTIAL_ACTIVE in (0,1)),
        ACTIVE_FROM timestamp(6) with time zone,
        CHANGED_ON timestamp(6) with time zone,
        CREDENTIAL_TARGET varchar2(255 char) check (CREDENTIAL_TARGET in ('UI','REST_API')),
        CREDENTIAL_TYPE varchar2(255 char) check (CREDENTIAL_TYPE in ('USERNAME_PASSWORD','ACCESS_TOKEN','CERTIFICATE','CAS')),
        CREDENTIAL_DESC varchar2(256 char),
        LAST_ALERT_ON timestamp(6) with time zone,
        EXPIRE_ON timestamp(6) with time zone,
        LAST_FAILED_LOGIN_ON timestamp(6) with time zone,
        CREDENTIAL_NAME varchar2(256 char),
        RESET_EXPIRE_ON timestamp(6) with time zone,
        RESET_TOKEN varchar2(256 char),
        LOGIN_FAILURE_COUNT number(10,0),
        CREDENTIAL_VALUE varchar2(256 char),
        FK_USER_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_DOCUMENT (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        CURRENT_VERSION number(10,0) not null,
        MIME_TYPE varchar2(128 char),
        NAME varchar2(255 char),
        REF_DOCUMENT_URL varchar2(1024 char),
        SHARING_ENABLED number(1,0) check (SHARING_ENABLED in (0,1)),
        FK_REF_DOCUMENT_ID number(19,0),
        primary key (ID)
    );

    comment on column SMP_DOCUMENT.ID is
        'Unique document id';

    create table SMP_DOCUMENT_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        CURRENT_VERSION number(10,0),
        MIME_TYPE varchar2(128 char),
        NAME varchar2(255 char),
        REF_DOCUMENT_URL varchar2(1024 char),
        SHARING_ENABLED number(1,0) check (SHARING_ENABLED in (0,1)),
        FK_REF_DOCUMENT_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_DOCUMENT_CERTIFICATE (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        CERTIFICATE_ID varchar2(1024 char) unique,
        ISSUER varchar2(1024 char),
        PEM_ENCODED_CERT clob,
        SERIALNUMBER varchar2(128 char),
        SUBJECT varchar2(1024 char),
        VALID_FROM timestamp(6) with time zone,
        VALID_TO timestamp(6) with time zone,
        primary key (ID)
    );

    comment on column SMP_DOCUMENT_CERTIFICATE.ID is
        'Shared primary key with master table SMP_DOC_PROP_SEQ';

    comment on column SMP_DOCUMENT_CERTIFICATE.CERTIFICATE_ID is
        'Formatted Certificate id using tags: cn, o, c and serialNumber';

    comment on column SMP_DOCUMENT_CERTIFICATE.ISSUER is
        'Certificate issuer (canonical form)';

    comment on column SMP_DOCUMENT_CERTIFICATE.PEM_ENCODED_CERT is
        'PEM encoded certificate';

    comment on column SMP_DOCUMENT_CERTIFICATE.SERIALNUMBER is
        'Certificate serial number';

    comment on column SMP_DOCUMENT_CERTIFICATE.SUBJECT is
        'Certificate subject (canonical form)';

    comment on column SMP_DOCUMENT_CERTIFICATE.VALID_FROM is
        'Certificate valid from date.';

    comment on column SMP_DOCUMENT_CERTIFICATE.VALID_TO is
        'Certificate valid to date.';

    create table SMP_DOCUMENT_CERTIFICATE_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        CERTIFICATE_ID varchar2(1024 char),
        ISSUER varchar2(1024 char),
        PEM_ENCODED_CERT clob,
        SERIALNUMBER varchar2(128 char),
        SUBJECT varchar2(1024 char),
        VALID_FROM timestamp(6) with time zone,
        VALID_TO timestamp(6) with time zone,
        primary key (REV, ID)
    );

    create table SMP_DOCUMENT_PROPERTY (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DESCRIPTION varchar2(4000 char),
        PROPERTY_NAME varchar2(255 char),
        PROPERTY_TYPE varchar2(64 char) check (PROPERTY_TYPE in ('STRING','DATETIME','LIST_STRING','MAP_STRING','INTEGER','BOOLEAN','REGEXP','CRON_EXPRESSION','EMAIL','FILENAME','PATH','URL','CERTIFICATE')),
        PROPERTY_VALUE varchar2(4000 char),
        FK_DOCUMENT_ID number(19,0),
        primary key (ID),
        constraint SMP_DOC_PROP_IDX unique (FK_DOCUMENT_ID, PROPERTY_NAME)
    );

    comment on column SMP_DOCUMENT_PROPERTY.ID is
        'Unique document property id';

    comment on column SMP_DOCUMENT_PROPERTY.DESCRIPTION is
        'Property description';

    create table SMP_DOCUMENT_PROPERTY_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DESCRIPTION varchar2(4000 char),
        PROPERTY_NAME varchar2(255 char),
        PROPERTY_TYPE varchar2(64 char) check (PROPERTY_TYPE in ('STRING','DATETIME','LIST_STRING','MAP_STRING','INTEGER','BOOLEAN','REGEXP','CRON_EXPRESSION','EMAIL','FILENAME','PATH','URL','CERTIFICATE')),
        PROPERTY_VALUE varchar2(4000 char),
        FK_DOCUMENT_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_DOCUMENT_VERSION (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DOCUMENT_CONTENT blob,
        STATUS varchar2(255 char) not null check (STATUS in ('DRAFT','PUBLISHED','RETIRED','UNDER_REVIEW','APPROVED','REJECTED')),
        VERSION number(10,0) not null,
        FK_DOCUMENT_ID number(19,0),
        primary key (ID),
        constraint SMP_DOCVER_UNIQ_VERSION_IDX unique (FK_DOCUMENT_ID, VERSION)
    );

    comment on column SMP_DOCUMENT_VERSION.ID is
        'Unique version document id';

    comment on column SMP_DOCUMENT_VERSION.DOCUMENT_CONTENT is
        'Document content';

    comment on column SMP_DOCUMENT_VERSION.STATUS is
        'Document version status';

    create table SMP_DOCUMENT_VERSION_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DOCUMENT_CONTENT blob,
        STATUS varchar2(255 char) check (STATUS in ('DRAFT','PUBLISHED','RETIRED','UNDER_REVIEW','APPROVED','REJECTED')),
        VERSION number(10,0),
        FK_DOCUMENT_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_DOCUMENT_VERSION_EVENT (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DETAILS varchar2(1024 char),
        EVENT_ON timestamp(6) with time zone,
        EVENT_SOURCE varchar2(255 char) not null check (EVENT_SOURCE in ('UI','REST_API','CRON','PLUGIN','OTHER')),
        EVENT_TYPE varchar2(255 char) not null check (EVENT_TYPE in ('CREATE','UPDATE','PUBLISH','REQUEST_REVIEW','RETIRE','APPROVE','REJECT','ERROR','SETTINGS_CHANGE')),
        EVENT_STATUS varchar2(255 char) not null check (EVENT_STATUS in ('DRAFT','PUBLISHED','RETIRED','UNDER_REVIEW','APPROVED','REJECTED')),
        EVENT_BY_USERNAME varchar2(64 char),
        FK_DOCUMENT_VERSION_ID number(19,0),
        primary key (ID)
    );

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

    create table SMP_DOMAIN (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DEFAULT_RESOURCE_IDENTIFIER varchar2(255 char),
        DOMAIN_CODE varchar2(256 char) not null unique,
        ENABLE_DOMAIN_TRUSTSTORE number(1,0) check (ENABLE_DOMAIN_TRUSTSTORE in (0,1)),
        SIGNATURE_ALGORITHM varchar2(256 char),
        SIGNATURE_DIGEST_METHOD varchar2(256 char),
        SIGNATURE_KEY_ALIAS varchar2(256 char),
        SML_CLIENT_CERT_AUTH number(1,0) not null check (SML_CLIENT_CERT_AUTH in (0,1)),
        SML_CLIENT_KEY_ALIAS varchar2(256 char),
        SML_CLIENT_KEY_CHANGE_ALIAS varchar2(255 char),
        SML_CLIENT_KEY_CHANGE_DATE timestamp(6) with time zone,
        SML_REGISTERED number(1,0) not null check (SML_REGISTERED in (0,1)),
        SML_SMP_ID varchar2(256 char),
        SML_SUBDOMAIN varchar2(256 char),
        SML_ENABLE_URL_DOMAIN_CODE_SUFFIX number(1,0) check (SML_ENABLE_URL_DOMAIN_CODE_SUFFIX in (0,1)),
        VISIBILITY varchar2(64 char) check (VISIBILITY in ('PUBLIC','INTERNAL','PRIVATE')),
        primary key (ID)
    );

    comment on column SMP_DOMAIN.ID is
        'Unique domain id';

    comment on column SMP_DOMAIN.DEFAULT_RESOURCE_IDENTIFIER is
        'Default resourceType code';

    comment on column SMP_DOMAIN.DOMAIN_CODE is
        'Domain code used as http parameter in rest webservices';

    comment on column SMP_DOMAIN.ENABLE_DOMAIN_TRUSTSTORE is
        'If enabled use the domain custom truststore to validate domain certificates, else it uses the system truststore';

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

    comment on column SMP_DOMAIN.SML_CLIENT_KEY_CHANGE_ALIAS is
        'Client key alias used to update the certificate for SML integration';

    comment on column SMP_DOMAIN.SML_CLIENT_KEY_CHANGE_DATE is
        'Future date when to update the certificate for SML integration';

    comment on column SMP_DOMAIN.SML_REGISTERED is
        'Flag for: Is domain registered in SML';

    comment on column SMP_DOMAIN.SML_SMP_ID is
        'SMP ID used for SML integration';

    comment on column SMP_DOMAIN.SML_SUBDOMAIN is
        'SML subdomain';

    comment on column SMP_DOMAIN.SML_ENABLE_URL_DOMAIN_CODE_SUFFIX is
        'Append the domain code to SMP url when registering the SMP entry';

    comment on column SMP_DOMAIN.VISIBILITY is
        'The visibility of the domain: PUBLIC, INTERNAL';

    create table SMP_DOMAIN_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DEFAULT_RESOURCE_IDENTIFIER varchar2(255 char),
        DOMAIN_CODE varchar2(256 char),
        ENABLE_DOMAIN_TRUSTSTORE number(1,0) check (ENABLE_DOMAIN_TRUSTSTORE in (0,1)),
        SIGNATURE_ALGORITHM varchar2(256 char),
        SIGNATURE_DIGEST_METHOD varchar2(256 char),
        SIGNATURE_KEY_ALIAS varchar2(256 char),
        SML_CLIENT_CERT_AUTH number(1,0) check (SML_CLIENT_CERT_AUTH in (0,1)),
        SML_CLIENT_KEY_ALIAS varchar2(256 char),
        SML_CLIENT_KEY_CHANGE_ALIAS varchar2(255 char),
        SML_CLIENT_KEY_CHANGE_DATE timestamp(6) with time zone,
        SML_REGISTERED number(1,0) check (SML_REGISTERED in (0,1)),
        SML_SMP_ID varchar2(256 char),
        SML_SUBDOMAIN varchar2(256 char),
        SML_ENABLE_URL_DOMAIN_CODE_SUFFIX number(1,0) check (SML_ENABLE_URL_DOMAIN_CODE_SUFFIX in (0,1)),
        VISIBILITY varchar2(64 char) check (VISIBILITY in ('PUBLIC','INTERNAL','PRIVATE')),
        primary key (REV, ID)
    );

    create table SMP_DOMAIN_CONFIGURATION (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DESCRIPTION varchar2(4000 char),
        PROPERTY_NAME varchar2(512 char) not null,
        SYSTEM_DEFAULT number(1,0) not null check (SYSTEM_DEFAULT in (0,1)),
        PROPERTY_VALUE varchar2(4000 char),
        FK_DOMAIN_ID number(19,0) not null,
        primary key (ID),
        constraint SMP_DOMAIN_CONF_IDX unique (ID, PROPERTY_NAME, FK_DOMAIN_ID)
    );

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
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DESCRIPTION varchar2(4000 char),
        PROPERTY_NAME varchar2(512 char),
        SYSTEM_DEFAULT number(1,0) check (SYSTEM_DEFAULT in (0,1)),
        PROPERTY_VALUE varchar2(4000 char),
        FK_DOMAIN_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_DOMAIN_DOC_TMPL (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DOCUMENT_LEVEL varchar2(255 char) not null check (DOCUMENT_LEVEL in ('RESOURCE','SUBRESOURCE')),
        FK_DOCUMENT_ID number(19,0) not null,
        FK_DOREDEF_ID number(19,0) not null,
        FK_SUREDEF_ID number(19,0),
        primary key (ID)
    );

    comment on column SMP_DOMAIN_DOC_TMPL.ID is
        'Unique domain document template id';

    comment on column SMP_DOMAIN_DOC_TMPL.DOCUMENT_LEVEL is
        'Document level type - resource or subresource';

    create table SMP_DOMAIN_DOC_TMPL_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DOCUMENT_LEVEL varchar2(255 char) check (DOCUMENT_LEVEL in ('RESOURCE','SUBRESOURCE')),
        FK_DOCUMENT_ID number(19,0),
        FK_DOREDEF_ID number(19,0),
        FK_SUREDEF_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_DOMAIN_MEMBER (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        MEMBERSHIP_ROLE varchar2(64 char) check (MEMBERSHIP_ROLE in ('VIEWER','ADMIN')),
        FK_DOMAIN_ID number(19,0),
        FK_USER_ID number(19,0),
        primary key (ID),
        constraint SMP_DOM_MEM_IDX unique (FK_DOMAIN_ID, FK_USER_ID)
    );

    create table SMP_DOMAIN_MEMBER_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        MEMBERSHIP_ROLE varchar2(64 char) check (MEMBERSHIP_ROLE in ('VIEWER','ADMIN')),
        FK_DOMAIN_ID number(19,0),
        FK_USER_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_DOMAIN_RESOURCE_DEF (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        FK_DOMAIN_ID number(19,0),
        FK_RESOURCE_DEF_ID number(19,0),
        primary key (ID),
        constraint SMP_DOREDEF_UNIQ_DOM_RD_IDX unique (FK_RESOURCE_DEF_ID, FK_DOMAIN_ID)
    );

    create table SMP_DOMAIN_RESOURCE_DEF_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        FK_DOMAIN_ID number(19,0),
        FK_RESOURCE_DEF_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_EXTENSION (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DESCRIPTION varchar2(512 char),
        IDENTIFIER varchar2(128 char) unique,
        IMPLEMENTATION_NAME varchar2(512 char),
        NAME varchar2(128 char),
        VERSION varchar2(128 char),
        primary key (ID),
        constraint SMP_EXT_UNIQ_NAME_IDX unique (IMPLEMENTATION_NAME)
    );

    comment on column SMP_EXTENSION.ID is
        'Unique extension id';

    create table SMP_EXTENSION_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DESCRIPTION varchar2(512 char),
        IDENTIFIER varchar2(128 char),
        IMPLEMENTATION_NAME varchar2(512 char),
        NAME varchar2(128 char),
        VERSION varchar2(128 char),
        primary key (REV, ID)
    );

    create table SMP_GROUP (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DESCRIPTION varchar2(4000 char),
        NAME varchar2(512 char) not null,
        VISIBILITY varchar2(128 char) check (VISIBILITY in ('PUBLIC','INTERNAL','PRIVATE')),
        FK_DOMAIN_ID number(19,0) not null,
        primary key (ID),
        constraint SMP_GRP_UNIQ_DOM_IDX unique (NAME, FK_DOMAIN_ID)
    );

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
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DESCRIPTION varchar2(4000 char),
        NAME varchar2(512 char),
        VISIBILITY varchar2(128 char) check (VISIBILITY in ('PUBLIC','INTERNAL','PRIVATE')),
        FK_DOMAIN_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_GROUP_MEMBER (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        MEMBERSHIP_ROLE varchar2(64 char) check (MEMBERSHIP_ROLE in ('VIEWER','ADMIN')),
        FK_GROUP_ID number(19,0),
        FK_USER_ID number(19,0),
        primary key (ID),
        constraint SMP_GRP_MEM_IDX unique (FK_GROUP_ID, FK_USER_ID)
    );

    create table SMP_GROUP_MEMBER_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        MEMBERSHIP_ROLE varchar2(64 char) check (MEMBERSHIP_ROLE in ('VIEWER','ADMIN')),
        FK_GROUP_ID number(19,0),
        FK_USER_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_PERIODICAL_ALERT (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        ALERT_SCOPE varchar2(255 char) check (ALERT_SCOPE in ('USER_CREDENTIAL','SYSTEM_TRUSTSTORE','SYSTEM_KEYSTORE')),
        ENTITY_IDENTIFIER varchar2(255 char),
        ENTITY_TYPE varchar2(255 char) check (ENTITY_TYPE in ('USERNAME_PASSWORD','ACCESS_TOKEN','CERTIFICATE','SYSTEM_CERTIFICATE')),
        LAST_ALERT_ON timestamp(6) with time zone,
        primary key (ID),
        constraint SMP_ALERT_COMPOSITE_IDX unique (ENTITY_IDENTIFIER, ENTITY_TYPE, ALERT_SCOPE)
    );

    comment on column SMP_PERIODICAL_ALERT.ID is
        'Unique periodical alert id';

    comment on column SMP_PERIODICAL_ALERT.ENTITY_IDENTIFIER is
        'Entity identifier for which the alert is sent, credential database id, certificate alias, etc.';

    comment on column SMP_PERIODICAL_ALERT.LAST_ALERT_ON is
        'Date and time when the last alert was sent for this entity';

    create table SMP_PERIODICAL_ALERT_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        ALERT_SCOPE varchar2(255 char) check (ALERT_SCOPE in ('USER_CREDENTIAL','SYSTEM_TRUSTSTORE','SYSTEM_KEYSTORE')),
        ENTITY_IDENTIFIER varchar2(255 char),
        ENTITY_TYPE varchar2(255 char) check (ENTITY_TYPE in ('USERNAME_PASSWORD','ACCESS_TOKEN','CERTIFICATE','SYSTEM_CERTIFICATE')),
        LAST_ALERT_ON timestamp(6) with time zone,
        primary key (REV, ID)
    );

    create table SMP_RESOURCE (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        IDENTIFIER_SCHEME varchar2(256 char),
        IDENTIFIER_VALUE varchar2(256 char) not null,
        REVIEW_ENABLED number(1,0) check (REVIEW_ENABLED in (0,1)),
        SML_REGISTERED number(1,0) check (SML_REGISTERED in (0,1)),
        VISIBILITY varchar2(128 char) check (VISIBILITY in ('PUBLIC','INTERNAL','PRIVATE')),
        FK_DOCUMENT_ID number(19,0) not null,
        FK_DOREDEF_ID number(19,0) not null,
        FK_GROUP_ID number(19,0),
        primary key (ID),
        constraint SMP_RS_UNIQ_IDENT_DOREDEF_IDX unique (IDENTIFIER_SCHEME, IDENTIFIER_VALUE, FK_DOREDEF_ID)
    );

    comment on column SMP_RESOURCE.ID is
        'Unique ServiceGroup id';

    create table SMP_RESOURCE_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        IDENTIFIER_SCHEME varchar2(256 char),
        IDENTIFIER_VALUE varchar2(256 char),
        REVIEW_ENABLED number(1,0) check (REVIEW_ENABLED in (0,1)),
        SML_REGISTERED number(1,0) check (SML_REGISTERED in (0,1)),
        VISIBILITY varchar2(128 char) check (VISIBILITY in ('PUBLIC','INTERNAL','PRIVATE')),
        FK_DOCUMENT_ID number(19,0),
        FK_DOREDEF_ID number(19,0),
        FK_GROUP_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_RESOURCE_DEF (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DESCRIPTION varchar2(512 char),
        HANDLER_IMPL_NAME varchar2(512 char),
        IDENTIFIER varchar2(128 char) unique,
        MIME_TYPE varchar2(128 char),
        NAME varchar2(128 char),
        URL_SEGMENT_OPTIONAL varchar2(128 char),
        URL_SEGMENT varchar2(128 char) unique,
        FK_EXTENSION_ID number(19,0),
        primary key (ID),
        constraint SMP_RESDEF_UNIQ_EXTID_CODE_IDX unique (FK_EXTENSION_ID, IDENTIFIER)
    );

    comment on column SMP_RESOURCE_DEF.ID is
        'Unique id';

    comment on column SMP_RESOURCE_DEF.URL_SEGMENT_OPTIONAL is
        'Comma separated optional resources url_segment.';

    comment on column SMP_RESOURCE_DEF.URL_SEGMENT is
        'resources are published under url_segment.';

    create table SMP_RESOURCE_DEF_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DESCRIPTION varchar2(512 char),
        HANDLER_IMPL_NAME varchar2(512 char),
        IDENTIFIER varchar2(128 char),
        MIME_TYPE varchar2(128 char),
        NAME varchar2(128 char),
        URL_SEGMENT_OPTIONAL varchar2(128 char),
        URL_SEGMENT varchar2(128 char),
        FK_EXTENSION_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_RESOURCE_MEMBER (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        PERMISSION_REVIEW number(1,0) check (PERMISSION_REVIEW in (0,1)),
        MEMBERSHIP_ROLE varchar2(64 char) check (MEMBERSHIP_ROLE in ('VIEWER','ADMIN')),
        FK_RESOURCE_ID number(19,0),
        FK_USER_ID number(19,0),
        primary key (ID),
        constraint SMP_RES_MEM_IDX unique (FK_RESOURCE_ID, FK_USER_ID)
    );

    comment on column SMP_RESOURCE_MEMBER.PERMISSION_REVIEW is
        'User permission to review the resource document';

    create table SMP_RESOURCE_MEMBER_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        PERMISSION_REVIEW number(1,0) check (PERMISSION_REVIEW in (0,1)),
        MEMBERSHIP_ROLE varchar2(64 char) check (MEMBERSHIP_ROLE in ('VIEWER','ADMIN')),
        FK_RESOURCE_ID number(19,0),
        FK_USER_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_REV_INFO (
        id number(19,0) not null,
        REVISION_DATE timestamp(6) with time zone,
        timestamp number(19,0) not null,
        USERNAME varchar2(255 char),
        primary key (id)
    );

    create table SMP_SUBRESOURCE (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        IDENTIFIER_SCHEME varchar2(500 char),
        IDENTIFIER_VALUE varchar2(500 char) not null,
        FK_DOCUMENT_ID number(19,0),
        FK_RESOURCE_ID number(19,0) not null,
        FK_SUREDEF_ID number(19,0) not null,
        primary key (ID),
        constraint SMP_SRS_UNIQ_ID_RES_SRT_IDX unique (FK_RESOURCE_ID, IDENTIFIER_VALUE, IDENTIFIER_SCHEME)
    );

    comment on column SMP_SUBRESOURCE.ID is
        'Shared primary key with master table SMP_SUBRESOURCE';

    create table SMP_SUBRESOURCE_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        IDENTIFIER_SCHEME varchar2(500 char),
        IDENTIFIER_VALUE varchar2(500 char),
        FK_DOCUMENT_ID number(19,0),
        FK_RESOURCE_ID number(19,0),
        FK_SUREDEF_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_SUBRESOURCE_DEF (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        DESCRIPTION varchar2(128 char),
        HANDLER_IMPL_NAME varchar2(512 char),
        IDENTIFIER varchar2(128 char) unique,
        MIME_TYPE varchar2(128 char),
        NAME varchar2(128 char),
        URL_SEGMENT_OPTIONAL varchar2(128 char),
        URL_SEGMENT varchar2(64 char),
        FK_RESOURCE_DEF_ID number(19,0),
        primary key (ID),
        constraint SMP_RD_UNIQ_RDID_UCTX_IDX unique (FK_RESOURCE_DEF_ID, URL_SEGMENT)
    );

    comment on column SMP_SUBRESOURCE_DEF.ID is
        'Unique id';

    comment on column SMP_SUBRESOURCE_DEF.URL_SEGMENT_OPTIONAL is
        'Comma separated optional subresources url_segment.';

    comment on column SMP_SUBRESOURCE_DEF.URL_SEGMENT is
        'Subresources are published under url_segment. It must be unique for resource type';

    create table SMP_SUBRESOURCE_DEF_AUD (
        ID number(19,0) not null,
        REV number(19,0) not null,
        REVTYPE number(3,0),
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        DESCRIPTION varchar2(128 char),
        HANDLER_IMPL_NAME varchar2(512 char),
        IDENTIFIER varchar2(128 char),
        MIME_TYPE varchar2(128 char),
        NAME varchar2(128 char),
        URL_SEGMENT_OPTIONAL varchar2(128 char),
        URL_SEGMENT varchar2(64 char),
        FK_RESOURCE_DEF_ID number(19,0),
        primary key (REV, ID)
    );

    create table SMP_USER (
        ID number(19,0) not null,
        CREATED_ON timestamp(6) with time zone not null,
        LAST_UPDATED_ON timestamp(6) with time zone not null,
        ACTIVE number(1,0) not null check (ACTIVE in (0,1)),
        APPLICATION_ROLE varchar2(256 char) check (APPLICATION_ROLE in ('USER','SYSTEM_ADMIN')),
        EMAIL varchar2(128 char),
        FULL_NAME varchar2(128 char),
        SMP_LOCALE varchar2(64 char),
        SMP_THEME varchar2(64 char),
        USERNAME varchar2(64 char) not null unique,
        primary key (ID)
    );

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
        CREATED_ON timestamp(6) with time zone,
        LAST_UPDATED_ON timestamp(6) with time zone,
        ACTIVE number(1,0) check (ACTIVE in (0,1)),
        APPLICATION_ROLE varchar2(256 char) check (APPLICATION_ROLE in ('USER','SYSTEM_ADMIN')),
        EMAIL varchar2(128 char),
        FULL_NAME varchar2(128 char),
        SMP_LOCALE varchar2(64 char),
        SMP_THEME varchar2(64 char),
        USERNAME varchar2(64 char),
        primary key (REV, ID)
    );

    create index SMP_DOCVER_DOCUMENT_IDX 
       on SMP_DOCUMENT_VERSION (FK_DOCUMENT_ID);

    create index SMP_DOCVEREVNT_DOCVER_IDX 
       on SMP_DOCUMENT_VERSION_EVENT (FK_DOCUMENT_VERSION_ID);

    create index SMP_RS_ID_IDX 
       on SMP_RESOURCE (IDENTIFIER_VALUE);

    create index SMP_RS_SCH_IDX 
       on SMP_RESOURCE (IDENTIFIER_SCHEME);

    create index SMP_SMD_DOC_ID_IDX 
       on SMP_SUBRESOURCE (IDENTIFIER_VALUE);

    create index SMP_SMD_DOC_SCH_IDX 
       on SMP_SUBRESOURCE (IDENTIFIER_SCHEME);

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

    alter table SMP_DOCUMENT 
       add constraint FKbytp2kp8g3pj8qfp1g6a2g7p 
       foreign key (FK_REF_DOCUMENT_ID) 
       references SMP_DOCUMENT;

    alter table SMP_DOCUMENT_AUD 
       add constraint FKh9epnme26i271eixtvrpqejvi 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_DOCUMENT_CERTIFICATE 
       add constraint FKdo996u5n5vqp9950jbrd32tpv 
       foreign key (ID) 
       references SMP_DOCUMENT_PROPERTY;

    alter table SMP_DOCUMENT_CERTIFICATE_AUD 
       add constraint FKlfwn1ehct3domxnwc1dr3mx4g 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_DOCUMENT_PROPERTY 
       add constraint FKfag3795e9mrvfvesd00yis9yh 
       foreign key (FK_DOCUMENT_ID) 
       references SMP_DOCUMENT;

    alter table SMP_DOCUMENT_PROPERTY_AUD 
       add constraint FK81057kcrugb1cfm0io5vkxtin 
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

    alter table SMP_DOCUMENT_VERSION_EVENT 
       add constraint FK6es2svpoxyrnt1h05c9junmdn 
       foreign key (FK_DOCUMENT_VERSION_ID) 
       references SMP_DOCUMENT_VERSION;

    alter table SMP_DOMAIN_AUD 
       add constraint FK35qm8xmi74kfenugeonijodsg 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_DOMAIN_CONFIGURATION 
       add constraint FK4303vstoigqtmeo3t2i034gm3 
       foreign key (FK_DOMAIN_ID) 
       references SMP_DOMAIN;

    alter table SMP_DOMAIN_CONFIGURATION_AUD 
       add constraint FKkelcga805bleh5x256hy5e1xb 
       foreign key (REV) 
       references SMP_REV_INFO;

    alter table SMP_DOMAIN_DOC_TMPL 
       add constraint FKg4ci2nee5nvm2tbdbpkeom53m 
       foreign key (FK_DOCUMENT_ID) 
       references SMP_DOCUMENT;

    alter table SMP_DOMAIN_DOC_TMPL 
       add constraint FK8dwm6w0x0rdiouvt74i07s9u5 
       foreign key (FK_DOREDEF_ID) 
       references SMP_DOMAIN_RESOURCE_DEF;

    alter table SMP_DOMAIN_DOC_TMPL 
       add constraint FK45eaf7nmo1dem40af2dw27jh5 
       foreign key (FK_SUREDEF_ID) 
       references SMP_SUBRESOURCE_DEF;

    alter table SMP_DOMAIN_DOC_TMPL_AUD 
       add constraint FKb1dw4r0rpj3jdtff4jc7gn046 
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

    alter table SMP_PERIODICAL_ALERT_AUD 
       add constraint FK7qyb720heygkwc5mmpaer2iou 
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
