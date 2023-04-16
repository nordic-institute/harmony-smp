import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {HttpClient, HttpParams} from '@angular/common/http';
import {SecurityService} from "../../security/security.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {User} from "../../security/user.model";
import {SmpConstants} from "../../smp.constants";
import {GroupRo} from "../../common/model/group-ro.model";
import {ResourceRo} from "../../common/model/resource-ro.model";
import {TableResult} from "../../common/model/table-result.model";
import {DomainRo} from "../../common/model/domain-ro.model";

@Injectable()
export class EditGroupService {


  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService) {
  }

  public getDomainGroupsForGroupAdmin(domain: DomainRo): Observable<GroupRo[]> {
    let params: HttpParams = new HttpParams()
      .set(SmpConstants.PATH_QUERY_FILTER_TYPE, 'group-admin');

    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<GroupRo[]>(SmpConstants.REST_EDIT_DOMAIN_GROUP
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain?.domainId), {params});
  }

  public getGroupResourcesForGroupAdminObservable(group: GroupRo, domain: DomainRo, filter: any, page: number, pageSize: number): Observable<TableResult<ResourceRo>> {
    let params: HttpParams = new HttpParams()
      .set(SmpConstants.PATH_QUERY_FILTER_TYPE, 'resource-admin')
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());

    for (let filterProperty in filter) {
      if (filter.hasOwnProperty(filterProperty)) {
        // must encode else problem with + sign
        params = params.set(filterProperty, encodeURIComponent(filter[filterProperty]));
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
}
