DROP TABLE sequence_table;
DROP TABLE tb_ebms_payload;
DROP TABLE tb_backend_message;

CREATE TABLE TB_SENDER_WHITELIST
(
  ID             VARCHAR2(255 CHAR)             NOT NULL,
  ACTION         VARCHAR2(255 CHAR),
  PARTY_ID       VARCHAR2(255 CHAR),
  PARTY_ID_TYPE  VARCHAR2(255 CHAR),
  SERVICE        VARCHAR2(255 CHAR)
);
ALTER TABLE TB_SENDER_WHITELIST ADD PRIMARY KEY(ID);

CREATE TABLE TB_TEMP_STORE
(
  ID              VARCHAR2(255 CHAR)            NOT NULL,
  ARTIFACT        VARCHAR2(255 CHAR),
  BINARY_DATA     BLOB,
  ATTACHMENT_SET  VARCHAR2(255 CHAR),
  SERVICE         VARCHAR2(255 CHAR)
);
ALTER TABLE TB_TEMP_STORE ADD PRIMARY KEY (ID);

CREATE INDEX TEMP_STORE_IDX ON TB_TEMP_STORE (ATTACHMENT_SET);

CREATE TABLE TB_RECEIVED_MESSAGE_STATUS
(
  ID                         VARCHAR2(255 CHAR) NOT NULL,
  CONSUMED_BY                VARCHAR2(255 CHAR),
  DELETED                    DATE,
  DOWNLOADED                 DATE,
  RECEIVED                   DATE,
  RECEIVED_USER_PRIMARY_KEY  VARCHAR2(255 CHAR)
);

ALTER TABLE TB_RECEIVED_MESSAGE_STATUS ADD PRIMARY KEY (ID);
ALTER TABLE TB_RECEIVED_MESSAGE_STATUS ADD (
  CONSTRAINT FKFB5FEE97C0AFAC1B 
 FOREIGN KEY (RECEIVED_USER_PRIMARY_KEY) 
 REFERENCES TB_RECEIVED_USER_MSG (ID));

ALTER TABLE TB_PART_INFO ADD PAYLOAD_DATA BLOB;
ALTER TABLE TB_RECEIVED_USER_MSG ADD RAW_XML CLOB;
		
ALTER TABLE TB_EBMS3_PAYLOAD rename column FILE_NAME to TEMP_STORE;
UPDATE TB_EBMS3_PAYLOAD SET TEMP_STORE = null;
COMMIT;
CREATE UNIQUE INDEX EBMS3_PAYLOAD_IDX ON TB_EBMS3_PAYLOAD (TEMP_STORE);

CREATE UNIQUE INDEX MSG_TO_SEND_IDX ON TB_MESSAGE_TO_SEND (MESSAGE_ID);
			
CREATE UNIQUE INDEX MSG_ID_CBACK_IDX ON TB_MSG_ID_CALLBACK (MESSAGE_ID);
			
CREATE UNIQUE INDEX RCPT_IDX ON TB_RECEIPT (REF_TO_MESSAGE_ID);
			
CREATE UNIQUE INDEX RCVD_USER_MSG_IDX ON TB_RECEIVED_USER_MSG (MESSAGE_ID);

ALTER TABLE TB_PART_INFO add BODY char(1);