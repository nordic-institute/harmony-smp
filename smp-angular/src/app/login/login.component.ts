import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {SecurityService} from '../security/security.service';
import {HttpEventService} from '../http/http-event.service';
import {AlertMessageService} from '../common/alert-message/alert-message.service';
import {SecurityEventService} from '../security/security-event.service';
import {User} from '../security/user.model';
import {MatDialogRef, MatDialog} from '@angular/material/dialog';
import {DefaultPasswordDialogComponent} from 'app/security/default-password-dialog/default-password-dialog.component';
import {Subscription} from 'rxjs';
import {ExpiredPasswordDialogComponent} from '../common/expired-password-dialog/expired-password-dialog.component';
import {GlobalLookups} from "../common/global-lookups";

@Component({
  moduleId: module.id,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  model: any = {};
  loading = false;
  returnUrl: string;
  sub: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              public lookups: GlobalLookups,
              private securityService: SecurityService,
              private httpEventService: HttpEventService,
              private alertService: AlertMessageService,
              private securityEventService: SecurityEventService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

    this.sub = this.securityEventService.onLoginSuccessEvent().subscribe(
      data => {
        if (data && data.passwordExpired) {
          this.dialog.open(ExpiredPasswordDialogComponent).afterClosed().subscribe(() => this.router.navigate([this.returnUrl]));
        } else {
          this.router.navigate([this.returnUrl]);
        }
        this.lookups.refreshApplicationConfiguration();
      });

    this.securityEventService.onLoginErrorEvent().subscribe(
      error => {
        let message;
        const HTTP_UNAUTHORIZED = 401;
        const HTTP_FORBIDDEN = 403;
        const HTTP_NOTFOUND = 404;
        const HTTP_GATEWAY_TIMEOUT = 504;
        const USER_INACTIVE = 'Inactive';
        switch (error.status) {
          case HTTP_UNAUTHORIZED:
            message =error.error.errorDescription;
            this.model.password = '';
            break;
          case HTTP_FORBIDDEN:
            const forbiddenCode = error.message;
            switch (forbiddenCode) {
              case USER_INACTIVE:
                message = 'The user is inactive. Please contact your administrator.';
                break;
              default:
                message = error.status + ' The username/password combination you provided are not valid. Please try again or contact your administrator.';
                // clear the password
                this.model.password = '';
                break;
            }
            break;
          case HTTP_GATEWAY_TIMEOUT:
          case HTTP_NOTFOUND:
            message = 'Unable to login. SMP is not running.';
            break;
          default:
            message = 'Default error (' + error.status + ') occurred during login.';
            break;
        }
        this.alertService.error(message);
      });
  }

  login() {
    // clear alerts
    this.alertService.clearAlert();
    this.securityService.login(this.model.username, this.model.password);
  }

  verifyDefaultLoginUsed() {
    const currentUser: User = this.securityService.getCurrentUser();
    if (currentUser.defaultPasswordUsed) {
      this.dialog.open(DefaultPasswordDialogComponent);
    }
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  isUserAuthSSOEnabled(): boolean {
     return this.lookups.cachedApplicationInfo?.authTypes.includes('SSO');
  }

  isUserAuthPasswdEnabled():boolean {
    return this.lookups.cachedApplicationInfo?.authTypes.includes('PASSWORD');
  }
}
