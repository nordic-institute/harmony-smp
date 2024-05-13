import {SearchTableEntity} from '../../common/search-table/search-table-entity.model';

export interface DomainPropertyRo extends SearchTableEntity {
  property: string;
  value: string;
  type: string;
  desc: string;
  newValue?: string;
  valuePattern?:string;
  systemDefault:boolean;
  systemDefaultValue?:string;
}
