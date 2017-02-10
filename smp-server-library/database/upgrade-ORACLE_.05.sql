ALTER TABLE smp_endpoint MODIFY (documentIdentifierScheme VARCHAR(100));
ALTER TABLE smp_endpoint MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_ownership MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_process MODIFY (documentIdentifierScheme VARCHAR(100));
ALTER TABLE smp_process MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_service_group MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_service_metadata MODIFY (documentIdentifierScheme VARCHAR(100));
ALTER TABLE smp_service_metadata MODIFY (businessIdentifierScheme VARCHAR(100));

ALTER TABLE smp_service_metadata_red MODIFY (documentIdentifierScheme VARCHAR(100));
ALTER TABLE smp_service_metadata_red MODIFY (businessIdentifierScheme VARCHAR(100));
