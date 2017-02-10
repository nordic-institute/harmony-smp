ALTER TABLE smp_user ADD isadmin NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE smp_user ADD CONSTRAINT check_is_admin_value CHECK (isadmin = 0 OR isadmin = 1);