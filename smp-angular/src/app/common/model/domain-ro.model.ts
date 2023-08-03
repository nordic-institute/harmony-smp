import {SearchTableEntity} from '../search-table/search-table-entity.model';
import {VisibilityEnum} from "../enums/visibility.enum";

export interface DomainRo extends SearchTableEntity {
  domainId?: string;
  domainCode?: string;
  smlSubdomain?: string;
  smlSmpId?: string;
  smlParticipantIdentifierRegExp?: string;
  smlClientKeyAlias?: string;
  signatureKeyAlias?: string;
  smlRegistered?: boolean;
  smlClientCertAuth?: boolean;
  visibility?:VisibilityEnum;
  defaultResourceTypeIdentifier?:string;
  resourceDefinitions?: string[]
}

