import {SearchTableEntity} from '../common/search-table/search-table-entity.model';

export interface AlertRo extends SearchTableEntity {
  sid: string;
  alertType: string;
  alertStatus: string;
  alertStatusDesc?:string;
  alertLevel: string;
  processedTime?: Date;
  reportingTime: Date;
  mailTo?:string;
  alertDetails?: Object;
}

