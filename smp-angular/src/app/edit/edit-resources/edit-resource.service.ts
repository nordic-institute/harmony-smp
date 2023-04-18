import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {HttpClient, HttpParams} from '@angular/common/http';
import {SecurityService} from "../../security/security.service";
import {User} from "../../security/user.model";
import {SmpConstants} from "../../smp.constants";
import {GroupRo} from "../../common/model/group-ro.model";
import {ResourceRo} from "../../common/model/resource-ro.model";
import {TableResult} from "../../common/model/table-result.model";
import {DomainRo} from "../../common/model/domain-ro.model";
import {SearchTableEntity} from "../../common/search-table/search-table-entity.model";
import {DocumentRo} from "../../common/model/document-ro.model";

@Injectable()
export class EditResourceService {


  _selectedResource:ResourceRo;

  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
  }

  set selectedResource(selected: ResourceRo) {
    this._selectedResource = selected;
  }

  get selectedResource() {
    return this._selectedResource;
  }


  public getGroupResourcesForGroupAdminObservable(group: GroupRo, domain: DomainRo, filter: any, page: number, pageSize: number): Observable<TableResult<ResourceRo>> {
    return this.getGroupResourcesForUserTypeObservable('group-admin', group, domain, filter, page, pageSize);
  }

  public getGroupResourcesForResourceAdminObservable(group: GroupRo, domain: DomainRo, filter: any, page: number, pageSize: number): Observable<TableResult<ResourceRo>> {
    return this.getGroupResourcesForUserTypeObservable('resource-admin', group, domain, filter, page, pageSize);
  }

  public getGroupResourcesForUserTypeObservable(userType: string, group: GroupRo, domain: DomainRo, filter: any, page: number, pageSize: number): Observable<TableResult<ResourceRo>> {

    let params: HttpParams = new HttpParams()
      .set(SmpConstants.PATH_QUERY_FILTER_TYPE, userType)
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());

    if (!!filter) {
      for (let filterProperty in filter) {
        if (filter.hasOwnProperty(filterProperty)) {
          // must encode else problem with + sign
          params = params.set(filterProperty, encodeURIComponent(filter[filterProperty]));
        }
      }
    }


    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<TableResult<ResourceRo>>(SmpConstants.REST_EDIT_RESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain?.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group?.groupId), {params});
  }

  deleteResourceFromGroup(resource: ResourceRo, group: GroupRo, domain: DomainRo): Observable<ResourceRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.delete<ResourceRo>(SmpConstants.REST_EDIT_RESOURCE_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group?.groupId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain?.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource.resourceId));
  }

  createResourceForGroup(resource: ResourceRo, group: GroupRo, domain: DomainRo): Observable<ResourceRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<ResourceRo>(SmpConstants.REST_EDIT_RESOURCE_CREATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain?.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group?.groupId), resource);
  }

  updateResourceForGroup(resource: ResourceRo, group: GroupRo, domain: DomainRo): Observable<ResourceRo> {
    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.post<ResourceRo>(SmpConstants.REST_EDIT_RESOURCE_UPDATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain?.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group?.groupId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), resource);
  }


  public getDocumentObservable(resource: ResourceRo, version:number = null): Observable<DocumentRo> {
    let params: HttpParams = null;
    if (version) {
      params = new HttpParams()
        .set('version',version);
    }
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), {params});
  }
  public saveDocumentObservable(resource: ResourceRo, document:DocumentRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), document);
  }

  public validateDocumentObservable(resource: ResourceRo, document:DocumentRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_VALIDATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), document);
  }
  public generateDocumentObservable(resource: ResourceRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_GENERATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId), null);
  }
}
