import {SearchTableEntity} from '../../common/search-table/search-table-entity.model';
import {PropertyValueTypeEnum} from "../enums/property-value-type.enum";

export interface DocumentPropertyRo extends SearchTableEntity {
  property: string;
  value: string;
  type?: PropertyValueTypeEnum;
  desc: string
  readonly?: boolean;
}
