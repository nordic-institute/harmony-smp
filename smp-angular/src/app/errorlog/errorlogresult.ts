import {ErrorLogEntry} from "./errorlogentry";

export interface ErrorLogResult {
  errorLogEntries: Array<ErrorLogEntry>;
  // offset: number;
  pageSize: number;
  // orderBy: string;
  // asc: boolean;
  count: number;
  filter: any;
  mshRoles: Array<string>;
  errorCodes: Array<string>;
}
