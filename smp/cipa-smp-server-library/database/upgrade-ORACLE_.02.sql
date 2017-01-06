ALTER TABLE smp_service_metadata ADD xmlcontent CLOB;

ALTER TABLE smp_process DISABLE CONSTRAINT FK_smp_proc_docIdScheme;