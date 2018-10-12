import {AlertsEntry} from './alerts-entry.model';

export interface AlertsResult {
  alertsEntries: Array<AlertsEntry>;
  pageSize: number;
  count: number;
  filter: any;
  alertsType: Array<string>;
  alertsLevels: Array<string>;
}
