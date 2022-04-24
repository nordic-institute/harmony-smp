import {SearchTableEntity} from '../common/search-table/search-table-entity.model';

export interface PropertyRo extends SearchTableEntity {
  property: string;
  value: string;
  type: string;
  desc: string;
  isEncrypted : boolean;
  newValue?: string;
  updateDate?: Date;
  mandatory?: boolean;
  restartNeeded? : boolean;
}
