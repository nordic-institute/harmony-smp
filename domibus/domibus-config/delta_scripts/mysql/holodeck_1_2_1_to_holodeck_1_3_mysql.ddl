--
-- Domibus: MySQL Database schema update for AS4 Reliability
--

ALTER TABLE Receipts ADD ReplyPattern VARCHAR(8);
ALTER TABLE Receipts ADD pmode VARCHAR(255);
ALTER TABLE Receipts ADD ReceiptContent BLOB;
ALTER TABLE Receipts DROP COLUMN Non_Repudiation_Info;
ALTER TABLE Receipt_Tracking ADD Last_Transmission DATETIME;
ALTER TABLE Receipt_Tracking ADD Retransmissions INTEGER;
ALTER TABLE Receipt_Tracking DROP COLUMN Receipt_Received;
ALTER TABLE Receipt_Tracking ADD Status VARCHAR(16);
ALTER TABLE Receipt_Tracking ADD First_Reception DATETIME;
ALTER TABLE Receipt_Tracking DROP COLUMN request_ID;
ALTER TABLE UserMsg_Push ADD messageId VARCHAR(255);

CREATE TABLE Receipt_Tracking_Attempts (
  ID VARCHAR(255) NOT NULL,
  transmission DATETIME,
  receipt_tracking_ID VARCHAR(255) NOT NULL,
  CONSTRAINT PK_receipt_tracking_attempts PRIMARY KEY (ID)
);

CREATE TABLE Error_Message (
  ID VARCHAR(255) NOT NULL,
  origin VARCHAR(32),
  category VARCHAR(32),
  errorCode VARCHAR(32),
  severity VARCHAR(32),
  refToMessageInError VARCHAR(255),
  shortDescription VARCHAR(255),
  description TEXT,
  errorDetail TEXT,
  site VARCHAR(16),
  flow VARCHAR(16),
  appearance DATETIME,
  delivered BIT,
  toURL VARCHAR(255),
  CONSTRAINT PK_Error_Message PRIMARY KEY (ID)
);

ALTER TABLE Payload ADD payloadId VARCHAR(256);
ALTER TABLE Payload ADD bodyload bit;
ALTER TABLE Payload ADD contentType VARCHAR(256);