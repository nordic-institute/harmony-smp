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
import {TranslateService} from "@ngx-translate/core";

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
              private dialog: MatDialog,
              private translateService: TranslateService) {
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
                message = this.translateService.instant("login.error.inactive.user");
                break;
              default:
                message = this.translateService.instant("login.error.invalid.credentials", { errorStatus: error.status } );
                // clear the password
                this.loginForm['password'].setValue('');
                break;
            }
            break;
          case HTTP_GATEWAY_TIMEOUT:
          case HTTP_NOTFOUND:
            message = this.translateService.instant("login.error.smp.not.running");
            break;
          default:
            message = this.translateService.instant("login.error.generic.error", { errorStatus: error.status } );
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
      this.alertService.error(this.translateService.instant("login.error.invalid.username.or.password"));
    }
  }

  requestCredentialReset() {
    this.alertService.clearAlert();
    if (this.resetForm.valid) {
      this.securityService.requestCredentialReset(
        this.resetForm.get('resetUsername').value
      );
    } else {
      this.alertService.error(this.translateService.instant("login.error.invalid.username"));
    }
  }

  showWarningBeforeExpire(user: User) {
    this.dialog.open(InformationDialogComponent, {
      data: {
        title: this.translateService.instant("login.dialog.password.expiration.dialog.title"),
        description: this.translateService.instant("login.dialog.password.expiration.dialog.description", {
          expirationDate: formatDate(user.passwordExpireOn, "longDate", user.smpLocale || "en-US")
        })
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
