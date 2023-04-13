import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

import {HttpClient} from '@angular/common/http';
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

  public getDomains() {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<DomainRo[]>(SmpConstants.REST_PUBLIC_DOMAIN_EDIT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId))
      .subscribe((result: DomainRo[]) => {
        this.notifyDomainsUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }


  public getDomainGroupsObservable(domainId: string): Observable<GroupRo[]>
  {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.get<GroupRo[]>(SmpConstants.REST_PUBLIC_GROUP_DOMAIN
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId));
  }

  public deleteDomainGroupObservable(domainId: string, groupId:string ): Observable<GroupRo>
  {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.delete<GroupRo>(SmpConstants.REST_PUBLIC_GROUP_DOMAIN_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId)
      .replace(SmpConstants.PATH_PARAM_ENC_GROUP_ID, groupId));
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
