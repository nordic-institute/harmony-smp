import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {SmpConstants} from "../../smp.constants";
import {
  SearchReferenceDocument
} from "../model/search-reference-document-ro.model";
import {TableResult} from "../model/table-result.model";
import {User} from "../../security/user.model";
import {SecurityService} from "../../security/security.service";
import {ResourceRo} from "../model/resource-ro.model";
import {SubresourceRo} from "../model/subresource-ro.model";

@Injectable()
export class ReferenceDocumentService {

  constructor(private http: HttpClient,
              private securityService: SecurityService) {
  }

  getSearchResourceDocumentReferencesObservable$(filter: SearchReferenceDocument, resource: ResourceRo): Observable<TableResult<SearchReferenceDocument>> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<TableResult<SearchReferenceDocument>>(SmpConstants.REST_EDIT_DOCUMENT_RESOURCE_SEARCH_REFERENCES
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId),
      filter);
  }

  getSearchSubresourceDocumentReferencesObservable$(filter: SearchReferenceDocument, resource: ResourceRo, subresource: SubresourceRo): Observable<TableResult<SearchReferenceDocument>> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<TableResult<SearchReferenceDocument>>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_SEARCH_REFERENCES
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
        .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId),
      filter);
  }
}
