import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SecurityService} from '../security/security.service';
import {AlertMessageService} from '../common/alert-message/alert-message.service';
import {SecurityEventService} from '../security/security-event.service';
import {User} from '../security/user.model';
import {MatDialog} from '@angular/material/dialog';
import {DefaultPasswordDialogComponent} from 'app/security/default-password-dialog/default-password-dialog.component';
import {Subscription} from 'rxjs';
import {
  ExpiredPasswordDialogComponent
} from '../common/dialogs/expired-password-dialog/expired-password-dialog.component';
import {GlobalLookups} from "../common/global-lookups";
import {PasswordChangeDialogComponent} from "../common/dialogs/password-change-dialog/password-change-dialog.component";
import {InformationDialogComponent} from "../common/dialogs/information-dialog/information-dialog.component";
import {formatDate} from "@angular/common";
import {EntityStatus} from "../common/enums/entity-status.enum";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  loginForm: FormGroup;
  resetForm: FormGroup;
  loading = false;
  returnUrl: string;
  sub: Subscription;


  constructor(private route: ActivatedRoute,
              private router: Router,
              public lookups: GlobalLookups,
              private securityService: SecurityService,
              private alertService: AlertMessageService,
              private securityEventService: SecurityEventService,
              private dialog: MatDialog) {
  }


  ngOnInit() {
    this.initForm();
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

    this.sub = this.securityEventService.onLoginSuccessEvent().subscribe(
      user => {
        if (user && user.passwordExpired) {
          if (user.forceChangeExpiredPassword) {
            this.dialog.open(PasswordChangeDialogComponent, {
              data: {
                user: user,
                adminUser: false
              }
            }).afterClosed().subscribe(res =>
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
            this.loginForm['password'].setValue('');
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
                this.loginForm['password'].setValue('');
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

  private initForm() {
    this.loginForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    });

    this.resetForm = new FormGroup({
      resetUsername: new FormControl('', Validators.required),
    });

  }

  login() {
    this.alertService.clearAlert();
    if (this.loginForm.valid) {
      this.securityService.login(
        this.loginForm.get('username').value,
        this.loginForm.get('password').value
      );
    } else {
      this.alertService.error('Please enter a valid username and password.');
    }
  }

  requestCredentialReset() {
    this.alertService.clearAlert();
    if (this.resetForm.valid) {
      this.securityService.requestCredentialReset(
        this.resetForm.get('resetUsername').value
      );
    } else {
      this.alertService.error('Please enter a valid username.');
    }
  }

  showWarningBeforeExpire(user: User) {
    this.dialog.open(InformationDialogComponent, {
      data: {
        title: "Warning! Your password is about to expire",
        description: "Your password is about to expire on " + formatDate(user.passwordExpireOn, "longDate", "en-US") + "! Please change the password before the expiration date!"
      }
    }).afterClosed().subscribe(() => this.router.navigate([this.returnUrl]));
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
