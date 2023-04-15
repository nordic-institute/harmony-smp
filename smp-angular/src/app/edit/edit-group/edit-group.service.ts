import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

import {HttpClient, HttpParams} from '@angular/common/http';
import {SecurityService} from "../../security/security.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {User} from "../../security/user.model";
import {SmpConstants} from "../../smp.constants";
import {GroupRo} from "../../common/model/group-ro.model";

@Injectable()
export class EditGroupService {
  private groupsUpdateSubject = new Subject<GroupRo[]>();


  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService) {
  }

  public getDomainGroupsForGroupAdmin(domainId:string) {
    let params: HttpParams = new HttpParams()
      .set(SmpConstants.PATH_QUERY_FILTER_TYPE, 'group-admin');

    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<GroupRo[]>(SmpConstants.REST_PUBLIC_GROUP_DOMAIN
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domainId),{params})
      .subscribe((result: GroupRo[]) => {
        this.notifyGroupUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  notifyGroupUpdated(res: GroupRo[]) {
    this.groupsUpdateSubject.next(res);
  }

  onGroupUpdatedEvent(): Observable<GroupRo[]> {
    return this.groupsUpdateSubject.asObservable();
  }
}
