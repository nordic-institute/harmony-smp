import {ServiceMetadataEditRo} from './service-metadata-edit-ro.model';
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {UserRo} from "../system-settings/user/user-ro.model";
import {ServiceGroupDomainEditRo} from "./service-group-domain-edit-ro.model";
import {EntityStatus} from "../common/model/entity-status.model";

export interface ServiceGroupEditRo extends SearchTableEntity {
  id: number;
  participantIdentifier: string;
  participantScheme: string;
  serviceMetadata: Array<ServiceMetadataEditRo>;
  serviceGroupDomains: Array<ServiceGroupDomainEditRo>;
  users: Array<UserRo>;
  extension?: string;
  extensionStatus: EntityStatus;
}
