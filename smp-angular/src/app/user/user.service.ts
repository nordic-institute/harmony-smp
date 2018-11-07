import {Injectable} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Role} from '../security/role.model';
import {SmpConstants} from "../smp.constants";
import {User} from "../security/user.model";
import {AlertService} from "../alert/alert.service";

@Injectable()
export class UserService {

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
  ) { }

  updateUser(user: User) {
    this.http.put(`${SmpConstants.REST_USER}/${user.id}`, user).subscribe(res => {
      this.alertService.success('The operation \'update user\' completed successfully.');
    }, err => {
      this.alertService.exception('The operation \'update user\' not completed successfully.', err);
    });
  }
}
