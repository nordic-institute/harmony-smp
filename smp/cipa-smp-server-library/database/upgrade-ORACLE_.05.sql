ALTER TABLE smp_endpoint MODIFY (documentIdentifierScheme VARCHAR2(100));
ALTER TABLE smp_endpoint MODIFY (businessIdentifierScheme VARCHAR2(100));

ALTER TABLE smp_ownership MODIFY (businessIdentifierScheme VARCHAR2(100));

ALTER TABLE smp_process MODIFY (documentIdentifierScheme VARCHAR2(100));
ALTER TABLE smp_process MODIFY (businessIdentifierScheme VARCHAR2(100));

ALTER TABLE smp_service_group MODIFY (businessIdentifierScheme VARCHAR2(100));

ALTER TABLE smp_service_metadata MODIFY (documentIdentifierScheme VARCHAR2(100));
ALTER TABLE smp_service_metadata MODIFY (businessIdentifierScheme VARCHAR2(100));

ALTER TABLE smp_service_metadata_red MODIFY (documentIdentifierScheme VARCHAR2(100));
ALTER TABLE smp_service_metadata_red MODIFY (businessIdentifierScheme VARCHAR2(100));
