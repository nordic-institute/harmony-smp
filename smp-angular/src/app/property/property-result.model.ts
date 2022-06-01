import {PropertyRo} from './property-ro.model';

export interface PropertyResult {
  serviceEntities: Array<PropertyRo>;
  pageSize: number;
  count: number;
  filter: any;
}
