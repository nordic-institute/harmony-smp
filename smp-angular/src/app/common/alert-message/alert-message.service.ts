import {Injectable} from '@angular/core';
import {NavigationEnd, NavigationStart, Router} from '@angular/router';
import {Observable, Subject} from 'rxjs';

@Injectable()
export class AlertMessageService {
  private subject = new Subject<any>();

  private previousRoute = '';

  private sticky = false;

  private message: { type: string, text: string };

  //TODO move the logic in the ngInit block
  constructor (private router: Router) {
    // clear alert message on route change
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (this.isRouteChanged(event.url)) {
          this.clearAlert();
        } else {
          console.log('Alert after when navigating from [' + this.previousRoute + '] to [' + event.url + ']');
        }
      } else if (event instanceof NavigationEnd) {
        this.previousRoute = event.url;
        if (this.sticky) {
          this.displayCurrentMessage();
          this.reset();
        }
      }
    });
  }

  private reset () {
    this.sticky = false;
    this.message = null;
  }

  getPath (url: string): string {
    const parser = document.createElement('a');
    parser.href = url;
    return parser.pathname;
  }

  isRouteChanged (currentRoute: string): boolean {
    let previousRoutePath = this.getPath(this.previousRoute);
    let currentRoutePath = this.getPath(currentRoute);
    return previousRoutePath !== currentRoutePath;
  }

  clearAlert (force = false): void {
    if (!force && this.sticky) {
      return;
    }
    this.subject.next(null);
  }

  setSticky (sticky: boolean) {
    this.sticky = sticky;
  }

  displayCurrentMessage () {
    this.subject.next(this.message);
  }

  success (message: string, keepAfterNavigationChange = false) {
    this.setSticky(keepAfterNavigationChange);
    this.message = {type: 'success', text: message};
    this.displayCurrentMessage();
  }

  error (message: string, keepAfterNavigationChange = false) {
    this.setSticky(keepAfterNavigationChange);
    this.message = {type: 'error', text: message};
    this.displayCurrentMessage();
  }

  exception (message: string, error: any, keepAfterNavigationChange = false): void {
    const errMsg = error.message || (error.json ? error.json().message : error );
    this.error(message + ' \n' + errMsg, keepAfterNavigationChange);
  }

  getMessage (): Observable<any> {
    return this.subject.asObservable();
  }
}
