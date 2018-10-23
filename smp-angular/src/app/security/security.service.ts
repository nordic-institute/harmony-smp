import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {User} from './user.model';
import {SecurityEventService} from './security-event.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {Authority} from "./authority.model";

@Injectable()
export class SecurityService {

  constructor (private http: HttpClient, private securityEventService: SecurityEventService) {
  }

  login(username: string, password: string) {

    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<string>(SmpConstants.REST_SECURITY_AUTHENTICATION,
      JSON.stringify({
        username: username,
        password: password
      }),
      { headers })
      .subscribe((response: string) => {
          console.log('Login success');
          localStorage.setItem('currentUser', JSON.stringify(response));
          this.securityEventService.notifyLoginSuccessEvent(response);
        },
        (error: any) => {
          console.log('Login error');
          this.securityEventService.notifyLoginErrorEvent(error);
        });
  }

  logout() {
    console.log('Logging out');
   // this.domainService.resetDomain();
    this.http.delete(SmpConstants.REST_SECURITY_AUTHENTICATION).subscribe((res: Response) => {
        localStorage.removeItem('currentUser');
        this.securityEventService.notifyLogoutSuccessEvent(res);
      },
      (error: any) => {
        console.debug('error logging out [' + error + ']');
        this.securityEventService.notifyLogoutErrorEvent(error);
      });
  }

  getCurrentUser(): User {
    let userObj = localStorage.getItem('currentUser');
    return JSON.parse(localStorage.getItem('currentUser'));
  }

  private getCurrentUsernameFromServer(): Observable<string> {
    let subject = new ReplaySubject<string>();
    this.http.get<string>(SmpConstants.REST_SECURITY_USER)
      .subscribe((res: string) => {
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
      this.getCurrentUsernameFromServer()
        .subscribe((user: string) => {
          console.log('isAuthenticated: getCurrentUsernameFromServer [' + user + ']');
          subject.next(user !== null);
        }, (user: string) => {
          console.log('isAuthenticated error' + user);
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
}
