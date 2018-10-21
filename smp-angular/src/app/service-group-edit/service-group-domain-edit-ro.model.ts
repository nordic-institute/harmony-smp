import { ServiceMetadataEditRo } from './service-metadata-edit-ro.model';
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {UserRo} from "../user/user-ro.model";
import {DomainRo} from "../domain/domain-ro.model";

export interface ServiceGroupEditRo extends SearchTableEntity {
  id: number;
  participantIdentifier: string;
  participantScheme: string;
  serviceMetadata: Array<ServiceMetadataEditRo>;
  serviceGroupDomains: Array<ServiceGroupDomainRo>;
  users: Array<UserRo>;
  extension?: string;
}
