import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {ServiceGroupDetailsDialogComponent} from "./service-group-details-dialog/service-group-details-dialog.component";

export interface ServiceMetadataEditRo extends SearchTableEntity  {
  documentIdentifier: string;
  documentIdentifierScheme: string;
  smlSubdomain: string;
  domainCode: string;
}
