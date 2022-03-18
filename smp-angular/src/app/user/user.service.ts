import {Injectable} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Role} from '../security/role.model';
import {SmpConstants} from "../smp.constants";
import {User} from "../security/user.model";
import {AlertService} from "../alert/alert.service";
import {SecurityService} from "../security/security.service";
import {AccessTokenRo} from "./access-token-ro.model";

@Injectable()
export class UserService {

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertService,
  ) { }

  updateUser(user: User) {
    this.http.put<string>(`${SmpConstants.REST_USER}/${user.id}`, user).subscribe(response => {
      this.securityService.updateUserDetails(response);
      this.alertService.success('The operation \'update user\' completed successfully.');
    }, err => {
      this.alertService.exception('The operation \'update user\' not completed successfully.', err);
    });
  }

}
