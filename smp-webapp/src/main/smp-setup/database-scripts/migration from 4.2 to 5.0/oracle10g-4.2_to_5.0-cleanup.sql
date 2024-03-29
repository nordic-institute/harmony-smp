drop table BCK_ALERT cascade constraints;
drop table BCK_ALERT_AUD cascade constraints;
drop table BCK_ALERT_PROPERTY cascade constraints;
drop table BCK_ALERT_PROPERTY_AUD cascade constraints;
drop table BCK_CERTIFICATE cascade constraints;
drop table BCK_CERTIFICATE_AUD cascade constraints;
drop table BCK_CONFIGURATION cascade constraints;
drop table BCK_CONFIGURATION_AUD cascade constraints;
drop table BCK_DOMAIN cascade constraints;
drop table BCK_DOMAIN_AUD cascade constraints;
drop table BCK_OWNERSHIP cascade constraints;
drop table BCK_OWNERSHIP_AUD cascade constraints;
drop table BCK_REV_INFO cascade constraints;
drop table BCK_SERVICE_GROUP cascade constraints;
drop table BCK_SERVICE_GROUP_AUD cascade constraints;
drop table BCK_SERVICE_GROUP_DOMAIN cascade constraints;
drop table BCK_SERVICE_GROUP_DOMAIN_AUD cascade constraints;
drop table BCK_SERVICE_METADATA cascade constraints;
drop table BCK_SERVICE_METADATA_AUD cascade constraints;
drop table BCK_SERVICE_METADATA_XML cascade constraints;
drop table BCK_SERVICE_METADATA_XML_AUD cascade constraints;
drop table BCK_SG_EXTENSION cascade constraints;
drop table BCK_SG_EXTENSION_AUD cascade constraints;
drop table BCK_USER cascade constraints;
drop table BCK_USER_AUD cascade constraints;
-- the only unused sequence
drop sequence SMP_SERVICE_GROUP_DOMAIN_SEQ;
drop sequence SMP_SERVICE_GROUP_SEQ;
