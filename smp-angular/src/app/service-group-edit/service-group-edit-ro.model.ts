import { ServiceMetadataEditRo } from './service-metadata-edit-ro.model';
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {UserRo} from "../user/user-ro.model";

export interface ServiceGroupEditRo extends SearchTableEntity {
  id: number;
  participantIdentifier: string;
  participantScheme: string;
  domainCode:'',
  smlSubdomain:'',
  serviceMetadata: Array<ServiceMetadataEditRo>;
  users: Array<UserRo>;
}
