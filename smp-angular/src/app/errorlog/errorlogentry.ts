export interface ErrorLogEntry {
  errorSignalMessageId: string,
  mshRole: string;
  messageInErrorId: string;
  errorCode: string;
  timestamp: Date;
  notified: Date;
}
