import {SearchTableEntity} from '../../common/search-table/search-table-entity.model';
import {VisibilityEnum} from "../../common/enums/visibility.enum";

export interface DomainRo extends SearchTableEntity {
  domainId?: string;
  domainCode?: string;
  smlSubdomain?: string;
  smlSmpId?: string;
  smlParticipantIdentifierRegExp?: string;
  smlClientCertHeader?: string;
  smlClientKeyAlias?: string;
  signatureKeyAlias?: string;
  smlRegistered?: boolean;
  smlClientCertAuth?: boolean;
  visibility?:VisibilityEnum;
  defaultResourceTypeIdentifier?:string;
  resourceDefinitions?: string[]

}

