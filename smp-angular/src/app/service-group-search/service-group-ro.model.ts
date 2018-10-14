import { ServiceMetadataRo } from './service-metadata-ro.model';
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export interface ServiceGroupRo extends SearchTableEntity {
  participantIdentifier: string;
  participantScheme: string;
  serviceMetadata: Array<ServiceMetadataRo>;
}
