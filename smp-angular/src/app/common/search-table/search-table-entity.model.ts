import {EntityStatus} from '../model/entity-status.model';

export interface SearchTableEntity {
  id?: number;
  index?: number;
  status: EntityStatus;
  deleted?: boolean;

  actionMessage?: string;
}
