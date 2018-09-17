import {AlertsEntry} from "./alertsentry";

export interface AlertsResult {
  alertsEntries: Array<AlertsEntry>;
  pageSize: number;
  count: number;
  filter: any;
  alertsType: Array<string>;
  alertsLevels: Array<string>;
}
