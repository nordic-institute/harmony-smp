import {SearchTableEntity} from '../search-table/search-table-entity.model';
import {DocumentLevelType} from "../enums/documetn-reference-type.enum";

export interface DomainDocumentTemplateRo extends SearchTableEntity {

  templateId?: string;
  domainCode?: string;
  resourceDefIdentifier?: string;
  subresourceDefIdentifier?: string;
  documentLevel?: DocumentLevelType;
}
