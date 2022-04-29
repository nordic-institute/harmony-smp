import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {User} from './user.model';
import {SecurityEventService} from './security-event.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {Authority} from "./authority.model";
import {AlertMessageService} from "../common/alert-message/alert-message.service";

@Injectable()
export class SecurityService {

  readonly LOCAL_STORAGE_KEY_CURRENT_USER = 'currentUser';

  constructor (
    private http: HttpClient,
    private alertService: AlertMessageService,
    private securityEventService: SecurityEventService,
  ) {
    this.securityEventService.onLogoutSuccessEvent().subscribe(() => window.location.reload());
    this.securityEventService.onLogoutErrorEvent().subscribe((error) => this.alertService.error(error));
  }

  login(username: string, password: string) {
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<User>(SmpConstants.REST_PUBLIC_SECURITY_AUTHENTICATION,
      JSON.stringify({
        username: username,
        password: password
      }),
      { headers })
      .subscribe((response: User) => {
          this.updateUserDetails(response);
        },
        (error: any) => {
          this.securityEventService.notifyLoginErrorEvent(error);
        });
  }

  refreshLoggedUserFromServer() {
    let subject = new ReplaySubject<User>();

    this.getCurrentUsernameFromServer().subscribe((res: User) => {

        this.updateUserDetails(res);
      }, (error: any) => {
        //console.log('getCurrentUsernameFromServer:' + error);
        this.securityEventService.notifyLoginErrorEvent(error);
      });
  }

  logout() {
    this.http.delete(SmpConstants.REST_PUBLIC_SECURITY_AUTHENTICATION).subscribe((res: Response) => {
        this.finalizeLogout(res);
      },
      (error) => {
        this.securityEventService.notifyLogoutErrorEvent(error);
      });
  }

  finalizeLogout(res){
    this.clearLocalStorage();
    this.securityEventService.notifyLogoutSuccessEvent(res);
  }


  getCurrentUser(): User {
    return JSON.parse(this.readLocalStorage());
  }

  private getCurrentUsernameFromServer(): Observable<User> {
    let subject = new ReplaySubject<User>();
    this.http.get<User>(SmpConstants.REST_PUBLIC_SECURITY_USER)
      .subscribe((res: User) => {
        subject.next(res);
      }, (error: any) => {
        //console.log('getCurrentUsernameFromServer:' + error);
        subject.next(null);
      });
    return subject.asObservable();
  }

  isAuthenticated(callServer: boolean = false): Observable<boolean> {
    let subject = new ReplaySubject<boolean>();
    if (callServer) {
      //we get the username from the server to trigger the redirection to the login screen in case the user is not authenticated
      this.getCurrentUsernameFromServer().subscribe((user: User) => {
          if(!user) {
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
    return this.isCurrentUserInRole([ Authority.SMP_ADMIN]);
  }

  isCurrentUserServiceGroupAdmin(): boolean {
    return this.isCurrentUserInRole([ Authority.SERVICE_GROUP_ADMIN]);
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

  updateUserDetails(userDetails:User) {
      this.populateLocalStorage(JSON.stringify(userDetails));
      this.securityEventService.notifyLoginSuccessEvent(userDetails);
  }

  private populateLocalStorage(userDetails: string) {
    localStorage.setItem(this.LOCAL_STORAGE_KEY_CURRENT_USER, userDetails);
  }

  private readLocalStorage(): string {
    return localStorage.getItem(this.LOCAL_STORAGE_KEY_CURRENT_USER);
  }

  private clearLocalStorage() {
    localStorage.removeItem(this.LOCAL_STORAGE_KEY_CURRENT_USER);
  }
}
