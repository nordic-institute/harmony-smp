import {SearchTableEntity} from "../common/search-table/search-table-entity.model";


export interface ServiceMetadataEditRo extends SearchTableEntity  {
  documentIdentifier: string;
  documentIdentifierScheme : string;
  smlSubdomain: string;
  domainCode: string;
  processSchema: string;
  processIdentifier: string;
  endpointUrl: string;
  endpointCertificate: string;

}
