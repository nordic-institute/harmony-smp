import {Injectable} from '@angular/core';
import {lastValueFrom, Observable, ReplaySubject} from 'rxjs';
import {User} from './user.model';
import {SecurityEventService} from './security-event.service';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {Authority} from "./authority.model";
import {
  AlertMessageService
} from "../common/alert-message/alert-message.service";
import {
  PasswordChangeDialogComponent
} from "../common/dialogs/password-change-dialog/password-change-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";

@Injectable()
export class SecurityService {

  readonly LOCAL_STORAGE_KEY_CURRENT_USER = 'currentUser';

  constructor(
    private http: HttpClient,
    private alertService: AlertMessageService,
    private securityEventService: SecurityEventService,
    private dialog: MatDialog,
    private router: Router,
    private translateService: TranslateService
  ) {
    this.securityEventService.onLogoutSuccessEvent().subscribe(() => {
      this.dialog.closeAll();
      this.router.navigateByUrl('/');
    });
    this.securityEventService.onLogoutErrorEvent().subscribe((error) => this.alertService.error(error));
  }

  login(username: string, password: string) {
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<User>(SmpConstants.REST_PUBLIC_SECURITY_AUTHENTICATION,
      JSON.stringify({
        username: username,
        password: password
      }),
      {headers})
      .subscribe({
        next: (response: User) => {
          this.updateUserDetails(response);
          this.securityEventService.notifyLoginSuccessEvent(response);
        },
        error: (error: any) => {
          this.alertService.error(error.error?.errorDescription)
          this.securityEventService.notifyLoginErrorEvent(error);
        }
      });
  }

  requestCredentialReset(userid: string) {
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<User>(SmpConstants.REST_PUBLIC_SECURITY_RESET_CREDENTIALS_REQUEST,
      JSON.stringify({
        credentialName: userid,
        credentialType: 'USERNAME_PASSWORD',
      }),
      {headers})
      .subscribe({
        complete: () => {
        }, // completeHandler
        error: (error: any) => {
          this.alertService.error(error)
        },    // errorHandler
        next: async () => {
          this.alertService.success(await lastValueFrom(this.translateService.get("login.success.confirmation.email.sent", {userId: userid})),
            true, -1);
          this.router.navigate(['/search']);
        }
      });
  }

  credentialReset(userid: string, token: string, newPassword: string) {
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<User>(SmpConstants.REST_PUBLIC_SECURITY_RESET_CREDENTIALS,
      JSON.stringify({
        credentialName: userid,
        credentialValue: newPassword,
        credentialType: 'USERNAME_PASSWORD',
        resetToken: token,
      }),
      {headers})
      .subscribe({
        complete: () => {
          this.router.navigate(['/login']);
        }, // completeHandler
        error: (error: any) => {
          this.alertService.error(error);
          this.router.navigate(['/login']);
        },    // errorHandler
        next: async () => {
          this.alertService.success(await lastValueFrom(this.translateService.get("reset.credentials.success.password.reset")), true, -1);
        }
      });
  }

  refreshLoggedUserFromServer() {
    this.getCurrentUsernameFromServer().subscribe((userDetails: User) => {
      this.updateUserDetails(userDetails);
      this.securityEventService.notifyLoginSuccessEvent(userDetails);
      if (userDetails?.forceChangeExpiredPassword) {
        this.dialog.open(PasswordChangeDialogComponent, {
          data: {
            user: userDetails,
            adminUser: false
          }
        }).afterClosed().subscribe(res =>
          this.finalizeLogout(res)
        );
      }
    }, (error: any) => {
      // just clean local storage
      this.clearLocalStorage();
    });
  }

  logout() {
    this.http.delete(SmpConstants.REST_PUBLIC_SECURITY_AUTHENTICATION)
      .subscribe({
        next: (res: Response) => {
          this.finalizeLogout(res);
        },
        error: (err: any) => {
          if (err instanceof HttpErrorResponse && err.status === 401) {
            this.finalizeLogout(err);
          } else {
            this.securityEventService.notifyLogoutErrorEvent(err);
          }
        }
      });
  }

  finalizeLogout(res) {
    this.clearLocalStorage();
    this.securityEventService.notifyLogoutSuccessEvent(res);
  }


  getCurrentUser(): User {
    return JSON.parse(this.readLocalStorage());
  }

  private getCurrentUsernameFromServer(): Observable<User> {
    let subject = new ReplaySubject<User>();
    this.http.get<User>(SmpConstants.REST_PUBLIC_SECURITY_USER)
      .subscribe({
        next: (res: User) => {
          subject.next(res);
        }, error: (error: any) => {
          subject.next(null);
        }
      });
    return subject.asObservable();
  }

  isAuthenticated(callServer: boolean = false): Observable<boolean> {
    let subject = new ReplaySubject<boolean>();
    if (callServer) {
      //we get the username from the server to trigger the redirection to the login screen in case the user is not authenticated
      this.getCurrentUsernameFromServer().subscribe((user: User) => {
        if (!user) {
          this.clearLocalStorage();
        }
        subject.next(user !== null);
      }, (user: string) => {
        subject.next(false);
      });

    } else {
      let currentUser = this.getCurrentUser();
      subject.next(currentUser !== null);
    }
    return subject.asObservable();
  }

  isCurrentUserSystemAdmin(): boolean {
    return this.isCurrentUserInRole([Authority.SYSTEM_ADMIN]);
  }

  isCurrentUserSMPAdmin(): boolean {
    return this.isCurrentUserInRole([Authority.SMP_ADMIN]);
  }

  isCurrentUserServiceGroupAdmin(): boolean {
    return this.isCurrentUserInRole([Authority.SERVICE_GROUP_ADMIN]);
  }

  isCurrentUserInRole(roles: Array<Authority>): boolean {
    let hasRole = false;
    const currentUser = this.getCurrentUser();
    if (currentUser && currentUser.authorities) {
      roles.forEach((role: Authority) => {
        if (currentUser.authorities.indexOf(role) !== -1) {
          hasRole = true;
        }
      });
    }
    return hasRole;
  }

  isAuthorized(roles: Array<Authority>): Observable<boolean> {
    let subject = new ReplaySubject<boolean>();

    this.isAuthenticated(false).subscribe((isAuthenticated: boolean) => {
      if (isAuthenticated && roles) {
        let hasRole = this.isCurrentUserInRole(roles);
        subject.next(hasRole);
      }
    });
    return subject.asObservable();
  }

  updateUserDetails(userDetails: User) {
    // store user data to local storage!
    this.populateLocalStorage(JSON.stringify(userDetails));
  }

  private populateLocalStorage(userDetails: string) {
    localStorage.setItem(this.LOCAL_STORAGE_KEY_CURRENT_USER, userDetails);
  }

  private readLocalStorage(): string {
    return localStorage.getItem(this.LOCAL_STORAGE_KEY_CURRENT_USER);
  }

  public clearLocalStorage() {
    localStorage.removeItem(this.LOCAL_STORAGE_KEY_CURRENT_USER);
  }
}
