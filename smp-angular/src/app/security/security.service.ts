﻿import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {User} from './user.model';
import {SecurityEventService} from './security-event.service';
import {DomainService} from './domain.service';
import {Role} from './role.model';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable()
export class SecurityService {

  constructor (private http: HttpClient, private securityEventService: SecurityEventService, private domainService: DomainService) {
  }

  login(username: string, password: string) {
    this.domainService.resetDomain();
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<string>('rest/security/authentication',
      JSON.stringify({
        username: username,
        password: password
      }),
      { headers })
      .subscribe((response: string) => {
          console.log('Login success');
          localStorage.setItem('currentUser', response);
          this.securityEventService.notifyLoginSuccessEvent(response);
        },
        (error: any) => {
          console.log('Login error');
          this.securityEventService.notifyLoginErrorEvent(error);
        });
  }

  logout() {
    console.log('Logging out');
    this.domainService.resetDomain();
    this.http.delete('rest/security/authentication').subscribe((res: Response) => {
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
    this.http.get<string>('rest/security/user')
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

  isCurrentUserSuperAdmin(): boolean {
    return this.isCurrentUserInRole([Role.SYSTEM_ADMINISTRATOR]);
  }

  isCurrentUserAdmin(): boolean {
    return this.isCurrentUserInRole([Role.SYSTEM_ADMINISTRATOR, Role.SMP_ADMINISTRATOR]);
  }

  isCurrentUserInRole(roles: Array<Role>): boolean {
    let hasRole = false;
    const currentUser = this.getCurrentUser();
    if (currentUser && currentUser.authorities) {
      roles.forEach((role: Role) => {
        if (currentUser.authorities.indexOf(role) !== -1) {
          hasRole = true;
        }
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