DROP TABLE sequence_table;
DROP TABLE tb_ebms_payload;
DROP TABLE tb_backend_message;
create table TB_SENDER_WHITELIST (
        ID varchar(255) not null,
        ACTION varchar(255),
        PARTY_ID varchar(255),
        PARTY_ID_TYPE varchar(255),
        SERVICE varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB;
create table TB_TEMP_STORE (
        ID varchar(255) not null,
        ARTIFACT varchar(255),
        BINARY_DATA longblob,
        ATTACHMENT_SET varchar(255),        
        primary key (ID)
    ) ENGINE=InnoDB;
ALTER TABLE TB_TEMP_STORE
		add index (ATTACHMENT_SET);
	
create table TB_RECEIVED_MESSAGE_STATUS (
        ID varchar(255) not null,
        CONSUMED_BY varchar(255),
        DELETED datetime,
        DOWNLOADED datetime,        
    	RECEIVED datetime,
    	RECEIVED_USER_PRIMARY_KEY varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB;
alter table TB_RECEIVED_MESSAGE_STATUS 
        add index FKFB5FEE97C0AFAC1B (RECEIVED_USER_PRIMARY_KEY), 
        add constraint FKFB5FEE97C0AFAC1B 
        foreign key (RECEIVED_USER_PRIMARY_KEY) 
        references TB_RECEIVED_USER_MSG (Id);
		
ALTER TABLE TB_PART_INFO 
		add column PAYLOAD_DATA longblob;
		
ALTER TABLE TB_RECEIVED_USER_MSG 
		add column RAW_XML longtext;
		
ALTER TABLE TB_EBMS3_PAYLOAD 
		CHANGE FILE_NAME TEMP_STORE varchar(255),
		add index (TEMP_STORE);

UPDATE TB_EBMS3_PAYLOAD SET TEMP_STORE = null;

ALTER TABLE TB_MESSAGE_TO_SEND
			add index (MESSAGE_ID);
			
ALTER TABLE TB_MSG_ID_CALLBACK
			add index (MESSAGE_ID);
			
ALTER TABLE TB_RECEIPT
			add index (REF_TO_MESSAGE_ID);
			
ALTER TABLE TB_RECEIPT_TRACKING
			add index (MESSAGE_ID);
			
ALTER TABLE TB_RECEIVED_USER_MSG
			add index (MESSAGE_ID);

ALTER TABLE TB_PART_INFO
      add column BODY bit;

			
			
			