import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

import {HttpClient} from '@angular/common/http';
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

  public getUserAdminGroups() {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<GroupRo[]>(SmpConstants.REST_PUBLIC_GROUP_EDIT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId))
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
