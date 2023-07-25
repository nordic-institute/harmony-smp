import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {SearchTableResult} from "../../search-table/search-table-result.model";
import {User} from "../../../security/user.model";
import {HttpClient, HttpParams} from "@angular/common/http";
import {SmpConstants} from "../../../smp.constants";
import {SecurityService} from "../../../security/security.service";
import {AlertMessageService} from "../../alert-message/alert-message.service";
import {MemberRo} from "../../model/member-ro.model";
import {TableResult} from "../../model/table-result.model";
import {SearchUserRo} from "../../model/search-user-ro.model";
import {ResourceRo} from "../../model/resource-ro.model";
import {GroupRo} from "../../model/group-ro.model";
import {DomainRo} from "../../model/domain-ro.model";


@Injectable()
export class MembershipService {


  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService) {
  }


  getDomainMembersObservable(domainID: string, filter: any, page: number, pageSize: number): Observable<SearchTableResult> {
    const currentUser: User = this.securityService.getCurrentUser();

    let params: HttpParams = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());

    for (let filterProperty in filter) {
      if (filter.hasOwnProperty(filterProperty)) {
        // must encode else problem with + sign
        params = params.set(filterProperty, encodeURIComponent(filter[filterProperty]));
      }
    }

    return this.http.get<TableResult<MemberRo>>(SmpConstants.REST_EDIT_DOMAIN_MEMBER
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainID), {params});
  }

  getGroupMembersObservable(groupId: string, domainId: string, filter: any, page: number, pageSize: number): Observable<SearchTableResult> {
    const currentUser: User = this.securityService.getCurrentUser();

    let params: HttpParams = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());

    for (let filterProperty in filter) {
      if (filter.hasOwnProperty(filterProperty)) {
        // must encode else problem with + sign
        params = params.set(filterProperty, encodeURIComponent(filter[filterProperty]));
      }
    }
    return this.http.get<TableResult<MemberRo>>(SmpConstants.REST_EDIT_GROUP_MEMBER
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, groupId), {params});
  }

  getResourceMembersObservable(resource: ResourceRo, group: GroupRo, domain: DomainRo, filter: any, page: number, pageSize: number): Observable<SearchTableResult> {
    const currentUser: User = this.securityService.getCurrentUser();

    let params: HttpParams = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());

    for (let filterProperty in filter) {
      if (filter.hasOwnProperty(filterProperty)) {
        // must encode else problem with + sign
        params = params.set(filterProperty, encodeURIComponent(filter[filterProperty]));
      }
    }
    return this.http.get<TableResult<MemberRo>>(SmpConstants.REST_EDIT_RESOURCE_MEMBER
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group.groupId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource.resourceId), {params});
  }

  getUserLookupObservable(filter: string): Observable<SearchUserRo[]> {
    const currentUser: User = this.securityService.getCurrentUser();
    let params: HttpParams = new HttpParams()
      .set('filter', filter);
    return this.http.get<SearchUserRo[]>(SmpConstants.REST_PUBLIC_USER_SEARCH
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId), {params});
  }


  addEditMemberToDomain(domainId: string, member: MemberRo): Observable<MemberRo> {
    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.put<MemberRo>(SmpConstants.REST_EDIT_DOMAIN_MEMBER_PUT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId), member);
  }

  addEditMemberToGroup(groupId: string, domainId: string, member: MemberRo): Observable<MemberRo> {
    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.put<MemberRo>(SmpConstants.REST_EDIT_GROUP_MEMBER_PUT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, groupId), member);
  }

  addEditMemberToResource(resource: ResourceRo, group: GroupRo, domain: DomainRo, member: MemberRo): Observable<MemberRo> {
    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.put<MemberRo>(SmpConstants.REST_EDIT_RESOURCE_MEMBER_PUT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group.groupId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource.resourceId), member);
  }

  deleteMemberFromDomain(domainId: string, member: MemberRo): Observable<MemberRo> {
    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.delete<MemberRo>(SmpConstants.REST_EDIT_DOMAIN_MEMBER_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_MEMBER_ID, member.memberId));
  }

  deleteMemberFromGroup(groupId: string, domainId: string, member: MemberRo): Observable<MemberRo> {
    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.delete<MemberRo>(SmpConstants.REST_EDIT_GROUP_MEMBER_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, groupId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_MEMBER_ID, member.memberId));
  }

  deleteMemberFromResource(resource:ResourceRo, group:GroupRo, domain: DomainRo, member: MemberRo): Observable<MemberRo> {
    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.delete<MemberRo>(SmpConstants.REST_EDIT_RESOURCE_MEMBER_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group.groupId)
      .replace(SmpConstants.PATH_PARAM_ENC_RESOURCE_ID, resource.resourceId)
      .replace(SmpConstants.PATH_PARAM_ENC_MEMBER_ID, member.memberId));
  }

}
