import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

import {HttpClient, HttpParams} from '@angular/common/http';
import {SecurityService} from "../../security/security.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {User} from "../../security/user.model";
import {SmpConstants} from "../../smp.constants";
import {DomainRo} from "../../common/model/domain-ro.model";
import {GroupRo} from "../../common/model/group-ro.model";

@Injectable()
export class EditDomainService {
  private domainUpdateSubject = new Subject<DomainRo[]>();
  private domainEntryUpdateSubject = new Subject<DomainRo>();

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService) {
  }

  /**
   * Method fetches all domains where logged user is admin
   */
  public getDomainsForDomainAdminUser() {
    return this.getDomainsForType("domain-admin")
  }

  /**
   * Method fetches all domains where logged user is admin
   */
  public getDomainsForGroupAdminUser() {
    return this.getDomainsForType("group-admin")
  }

  public getDomainsForType(type: string) {
    let params: HttpParams = new HttpParams()
      .set(SmpConstants.PATH_QUERY_FILTER_TYPE, type);

    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<DomainRo[]>(SmpConstants.REST_PUBLIC_DOMAIN_EDIT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId), {params})
      .subscribe((result: DomainRo[]) => {
        this.notifyDomainsUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  public getDomainGroupsObservable(domainId: string): Observable<GroupRo[]> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<GroupRo[]>(SmpConstants.REST_PUBLIC_GROUP_DOMAIN
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId));
  }

  public deleteDomainGroupObservable(domainId: string, groupId: string): Observable<GroupRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.delete<GroupRo>(SmpConstants.REST_PUBLIC_GROUP_DOMAIN_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, groupId));
  }

  public createDomainGroupObservable(domainId: string, group: GroupRo): Observable<GroupRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<GroupRo>(SmpConstants.REST_PUBLIC_GROUP_DOMAIN_CREATE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId)
      , group);
  }

  public saveDomainGroupObservable(domainId: string, group: GroupRo): Observable<GroupRo> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<GroupRo>(SmpConstants.REST_PUBLIC_GROUP_DOMAIN_UPDATE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId)
        .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, group.groupId)
      , group);
  }

  notifyDomainsUpdated(res: DomainRo[]) {
    this.domainUpdateSubject.next(res);
  }

  notifyDomainEntryUpdated(res: DomainRo) {
    this.domainEntryUpdateSubject.next(res);
  }

  onDomainUpdatedEvent(): Observable<DomainRo[]> {
    return this.domainUpdateSubject.asObservable();
  }

  onDomainEntryUpdatedEvent(): Observable<DomainRo> {
    return this.domainEntryUpdateSubject.asObservable();
  }

}
