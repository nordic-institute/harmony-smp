import { SubresourceSearchRo } from './subresource-search-ro.model';
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export interface ResourceSearchRo extends SearchTableEntity {
  participantIdentifier: string;
  participantScheme: string;
  domainCode?:string;
  resourceDefUrlSegment?:string;
  serviceMetadata: Array<SubresourceSearchRo>;
}
