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
import {PasswordChangeDialogComponent} from "../common/password-change-dialog/password-change-dialog.component";
import {UserDetailsDialogMode} from "../user/user-details-dialog/user-details-dialog.component";
import {InformationDialogComponent} from "../common/information-dialog/information-dialog.component";
import {DatePipe, formatDate} from "@angular/common";

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
      user => {
        if (user && user.passwordExpired) {
          if (user.forceChangeExpiredPassword) {
            this.dialog.open(PasswordChangeDialogComponent, {data: user}).afterClosed().subscribe(res =>
              this.securityService.finalizeLogout(res)
            );
          } else {
            this.dialog.open(ExpiredPasswordDialogComponent).afterClosed().subscribe(() => this.router.navigate([this.returnUrl]));
          }
        } else if (user?.showPasswordExpirationWarning) {
          this.showWarningBeforeExpire(user);
        } else {
          this.router.navigate([this.returnUrl]);
        }
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
            message = error.error.errorDescription;
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

  showWarningBeforeExpire(user: User) {
    this.dialog.open(InformationDialogComponent, {
      data: {
        title: "Warning! Your password is about to expire!",
        description: "Your password is about to expire on " + formatDate(user.passwordExpireOn,"longDate","en-US")+"! Please change the password before the expiration date!"
      }
    }).afterClosed().subscribe(() => this.router.navigate([this.returnUrl]));
  }

  verifyDefaultLoginUsed() {
    const currentUser: User = this.securityService.getCurrentUser();
    if (currentUser.defaultPasswordUsed) {
      this.dialog.open(DefaultPasswordDialogComponent);
    }
  }

  private convertWithMode(config) {
    return (config && config.data)
      ? {
        ...config,
        data: {
          ...config.data,
          mode: config.data.mode || (config.data.edit ? UserDetailsDialogMode.EDIT_MODE : UserDetailsDialogMode.NEW_MODE)
        }
      }
      : config;
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  isUserAuthSSOEnabled(): boolean {
    return this.lookups.cachedApplicationInfo?.authTypes.includes('SSO');
  }

  isUserAuthPasswdEnabled(): boolean {
    return this.lookups.cachedApplicationInfo?.authTypes.includes('PASSWORD');
  }
}
