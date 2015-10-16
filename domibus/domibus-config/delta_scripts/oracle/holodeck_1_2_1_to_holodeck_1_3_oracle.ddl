--
-- Domibus: Oracle Database schema update for AS4 Reliability
--

ALTER TABLE Receipts ADD ReplyPattern VARCHAR2(8);
ALTER TABLE Receipts ADD pmode VARCHAR2(255);
ALTER TABLE Receipts ADD ReceiptContent BLOB;
ALTER TABLE Receipts DROP COLUMN Non_Repudiation_Info;
ALTER TABLE Receipt_Tracking ADD Last_Transmission TIMESTAMP;
ALTER TABLE Receipt_Tracking ADD Retransmissions INTEGER;
ALTER TABLE Receipt_Tracking DROP COLUMN Receipt_Received;
ALTER TABLE Receipt_Tracking ADD Status VARCHAR2(16);
ALTER TABLE Receipt_Tracking ADD First_Reception TIMESTAMP;
ALTER TABLE Receipt_Tracking DROP COLUMN request_ID;
ALTER TABLE UserMsg_Push ADD messageId VARCHAR2(255);

CREATE TABLE Receipt_Tracking_Attempts (
  ID VARCHAR2(255) NOT NULL,
  transmission TIMESTAMP,
  receipt_tracking_ID VARCHAR2(255) NOT NULL,
  CONSTRAINT PK_receipt_tracking_attempts PRIMARY KEY (ID)
);

CREATE TABLE Error_Message (
  ID VARCHAR2(255) NOT NULL,
  origin VARCHAR2(32),
  category VARCHAR2(32),
  errorCode VARCHAR2(32),
  severity VARCHAR2(32),
  refToMessageInError VARCHAR2(255),
  shortDescription VARCHAR2(255),
  description CLOB,
  errorDetail CLOB,
  site VARCHAR2(16),
  flow VARCHAR2(16),
  appearance TIMESTAMP,
  delivered NUMBER(1,0),
  toURL VARCHAR2(255),
  CONSTRAINT PK_Error_Message PRIMARY KEY (ID)
);

ALTER TABLE Payload ADD payloadId VARCHAR(256);
ALTER TABLE Payload ADD bodyload number(1,0);
ALTER TABLE Payload ADD contentType VARCHAR(256);
