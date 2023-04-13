import {EntityStatus} from '../enums/entity-status.enum';

export interface SearchTableEntity {
  id?: number;
  index?: number;
  status?: EntityStatus;
  deleted?: boolean;

  actionMessage?: string;
}
