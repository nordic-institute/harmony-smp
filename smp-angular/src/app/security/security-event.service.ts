import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

@Injectable()
export class SecurityEventService {

  private loginSuccessSubject = new Subject<any>();
  private loginErrorSubject = new Subject<any>();
  private logoutSuccessSubject = new Subject<any>();
  private logoutErrorSubject = new Subject<any>();

  notifyLoginSuccessEvent(res) {
    this.loginSuccessSubject.next(res);
  }

  onLoginSuccessEvent(): Observable<any> {
    return this.loginSuccessSubject.asObservable();
  }

  notifyLoginErrorEvent(error: any) {
    this.loginErrorSubject.next(error);
  }

  onLoginErrorEvent(): Observable<any> {
    return this.loginErrorSubject.asObservable();
  }

  notifyLogoutSuccessEvent(res) {
    this.logoutSuccessSubject.next(res);
  }

  onLogoutSuccessEvent(): Observable<any> {
    return this.logoutSuccessSubject.asObservable();
  }

  notifyLogoutErrorEvent(error: any) {
    this.logoutErrorSubject.next(error);
  }

  onLogoutErrorEvent(): Observable<any> {
    return this.logoutErrorSubject.asObservable();
  }
}
