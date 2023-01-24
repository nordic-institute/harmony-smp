import {AlertRo} from './alert-ro.model';

export interface AlertResult {
  serviceEntities: Array<AlertRo>;
  pageSize: number;
  count: number;
  filter: any;
}
