import {SearchTableEntity} from '../common/search-table/search-table-entity.model';

export interface PropertyValidationRo  {
  property: string;
  value?: string;
  propertyValid : boolean;
  errorMessage?: string;
}
