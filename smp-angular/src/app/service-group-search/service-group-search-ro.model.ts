import { ServiceMetadataSearchRo } from './service-metadata-search-ro.model';
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export interface ServiceGroupSearchRo extends SearchTableEntity {
  participantIdentifier: string;
  participantScheme: string;
  domainCode?:string;
  resourceDefUrlSegment?:string;
  serviceMetadata: Array<ServiceMetadataSearchRo>;
}
