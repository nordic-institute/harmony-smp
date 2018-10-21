import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {User} from './user.model';
import {SecurityEventService} from './security-event.service';
import {DomainService} from './domain.service';
import {Role} from './role.model';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";

@Injectable()
export class SecurityService {

  constructor (private http: HttpClient, private securityEventService: SecurityEventService) {
  }

  login(username: string, password: string) {
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<User>(SmpConstants.REST_SECURITY_AUTHENTICATION,
      JSON.stringify({ username, password }),
      { headers })
      .subscribe((response: User) => {
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
    return JSON.parse(localStorage.getItem('currentUser'));
  }

  private getCurrentUsernameFromServer(): Observable<string> {
    let subject = new ReplaySubject<string>();
    this.http.get(SmpConstants.REST_SECURITY_USER, {responseType: 'text'})
      .subscribe((username: string) => {
        subject.next(username);
      }, (error: any) => {
        subject.next(null);
      });
    return subject.asObservable();
  }

  isAuthenticated(callServer: boolean = false): Observable<boolean> {
    let subject = new ReplaySubject<boolean>();
      if (callServer) {
      //we get the username from the server to trigger the redirection to the login screen in case the user is not authenticated
      this.getCurrentUsernameFromServer()
        .subscribe((username: string) => {
          subject.next(username !== null);
        }, (user: string) => {
          subject.next(false);
        });
    } else {
      let currentUser = this.getCurrentUser();
      subject.next(currentUser !== null);
    }
    return subject.asObservable();
  }

  isCurrentUserSuperAdmin(): boolean {
    return this.isCurrentUserInRole([Role.SYSTEM_ADMIN]);
  }

  isCurrentUserAdmin(): boolean {
    return this.isCurrentUserInRole([Role.SYSTEM_ADMIN, Role.SMP_ADMIN]);
  }

  isCurrentUserInRole(roles: Array<Role>): boolean {
    let hasRole = false;
    const currentUser = this.getCurrentUser();
    if (currentUser && currentUser.authorities) {
      roles.forEach((role: Role) => {
        const roleKey = Object.keys(Role).find(k => Role[k] === role);
        hasRole = !!currentUser.authorities.find(authority => {
          return `${authority}` === `ROLE_${roleKey}`;
        });
      });
    }
    return hasRole;
  }

  isAuthorized(roles: Array<Role>): Observable<boolean> {
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
