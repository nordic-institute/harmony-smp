ALTER TABLE SMP_DOMAIN
    ADD SML_CLIENT_KEY_CHANGE_ALIAS varchar(256)  CHARACTER SET utf8 COLLATE utf8_bin comment 'Client key alias used to update the certificate for SML integration',
    ADD SML_CLIENT_KEY_CHANGE_DATE datetime comment 'Future date when to update the certificate for SML integration';
