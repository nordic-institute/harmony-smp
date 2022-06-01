import {Injectable} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Role} from '../security/role.model';
import {SmpConstants} from "../smp.constants";
import {User} from "../security/user.model";
import {AlertMessageService} from "../common/alert-message/alert-message.service";
import {SecurityService} from "../security/security.service";
import {AccessTokenRo} from "../common/dialogs/access-token-generation-dialog/access-token-ro.model";

@Injectable()
export class UserService {

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService,
  ) { }

  updateUser(user: User) {
    this.http.put<User>(SmpConstants.REST_PUBLIC_USER_UPDATE.replace('{user-id}', user.userId), user).subscribe(response => {
      this.securityService.updateUserDetails(response);
      this.alertService.success('The operation \'update user\' completed successfully.');
    }, err => {
      this.alertService.exception('The operation \'update user\' not completed successfully.', err);
    });
  }

}
