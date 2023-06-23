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
import {SubresourceRo} from "../../common/model/subresource-ro.model";

@Injectable()
export class EditResourceService {


  selectedResource:ResourceRo;

  selectedSubresource:SubresourceRo;

  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
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
      .set('page', !page?"0":page.toString())
      .set('pageSize', !pageSize?"5":pageSize.toString());

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

  getSubResourcesForResource(resource: ResourceRo): Observable<SubresourceRo[]> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<SubresourceRo[]>(SmpConstants.REST_EDIT_SUBRESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId));
  }

  deleteSubresourceFromResource(subResource: SubresourceRo, resource: ResourceRo): Observable<SubresourceRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.delete<ResourceRo>(SmpConstants.REST_EDIT_SUBRESOURCE_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subResource?.subresourceId));
  }

  createSubResourceForResource(subresource: SubresourceRo, resource: ResourceRo): Observable<SubresourceRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<SubresourceRo>(SmpConstants.REST_EDIT_SUBRESOURCE_CREATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId),
      subresource);
  }


  public getSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, version:number = null): Observable<DocumentRo> {
    let params: HttpParams = null;
    if (version) {
      params = new HttpParams()
        .set('version',version);
    }
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId), {params});
  }

  public saveSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, document:DocumentRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId), document);
  }

  public validateSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo, document:DocumentRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_VALIDATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId),document);
  }
  public generateSubresourceDocumentObservable(subresource: SubresourceRo, resource: ResourceRo): Observable<DocumentRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<DocumentRo>(SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE_GENERATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource?.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID, subresource?.subresourceId), null);
  }

}
