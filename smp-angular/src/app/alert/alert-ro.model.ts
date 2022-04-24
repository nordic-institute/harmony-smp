import {SearchTableEntity} from '../common/search-table/search-table-entity.model';

export interface AlertRo extends SearchTableEntity {
  sid: string;
  processed: boolean;
  alertType: string;
  alertStatus: string;
  alertLevel: string;
  processedTime: Date;
  reportingTime: Date;
}

