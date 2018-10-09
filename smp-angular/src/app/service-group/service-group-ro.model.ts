import { ServiceGroupROId } from './service-group-ro-id.model';
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export interface ServiceGroupRo extends SearchTableEntity {
  serviceGroupROId: ServiceGroupROId;
  domain: string;
}
