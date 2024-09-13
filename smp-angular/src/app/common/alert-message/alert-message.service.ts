import {Injectable} from '@angular/core';
import {NavigationEnd, NavigationStart, Router} from '@angular/router';
import {lastValueFrom, Observable, Subject} from 'rxjs';
import {HttpErrorResponse} from "@angular/common/http";
import {TranslateService} from "@ngx-translate/core";

/**
 * AlertMessageRO is the object that will be used to display the message in the SMP alert component in overlay.
 * These messages are not the same to SMP alerts, which are used to display alert the page.
 * It contains the type of the message (success, error, info, warning), the text of the message and the timeout in seconds.
 */
export interface AlertMessageRO {
  type: string,
  text: string,
  timeoutInSeconds?: number
}

@Injectable()
export class AlertMessageService {

  // the default timeout duration
  readonly DEFAULT_TIMEOUT: number = 3;

  private subject = new Subject<any>();
  private previousRoute:string = '';
  private keepAfterNavigationChange:boolean = false;
  private message: AlertMessageRO;

  constructor(private router: Router,
              private translateService: TranslateService) {
    // clear alert message on route change
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (this.isRouteChanged(event.url)) {
          this.clearAlert();
        }
      } else if (event instanceof NavigationEnd) {
        this.previousRoute = event.url;
        if (this.keepAfterNavigationChange) {
          this.displayCurrentMessage();
          this.reset();
        }
      }
    });
  }

  private reset() {
    this.keepAfterNavigationChange = false;
    this.message = null;
  }

  getPath(url: string): string {
    const parser = document.createElement('a');
    parser.href = url;
    return parser.pathname;
  }

  isRouteChanged(currentRoute: string): boolean {
    let previousRoutePath = this.getPath(this.previousRoute);
    let currentRoutePath = this.getPath(currentRoute);
    return previousRoutePath !== currentRoutePath;
  }

  clearAlert(force = false): void {
    if (!force && this.keepAfterNavigationChange) {
      return;
    }
    this.subject.next(null);
  }

  setKeepAfterNavigationChange(keepAfterNavigation: boolean) {
    this.keepAfterNavigationChange = keepAfterNavigation;
  }

  displayCurrentMessage() {
    this.subject.next(this.message);
  }

  /**
   * Extract the message from the object return it as a string. The object can be a string or an HttpErrorResponse
   *
   * @param messageObject
   */
  getObjectMessage(messageObject: any): string {
    if (typeof messageObject === 'string') {
      return messageObject;
    }
    if (messageObject instanceof HttpErrorResponse) {
      return this.getHttpErrorResponseMessage(messageObject);
    }
  }

  /**
   * Extract the message from the HttpErrorResponse and return it as a string
   * @param httpErrorResponse
   */
  getHttpErrorResponseMessage(httpErrorResponse: HttpErrorResponse): string {
    let message: string;
    if (httpErrorResponse.error) {
      if (httpErrorResponse.error.errorDescription) {
        message = httpErrorResponse.error.errorDescription;
      } else if (httpErrorResponse.error.message) {
        message = httpErrorResponse.error.message;
      }
    } else {
      message = httpErrorResponse.statusText + ': ' + httpErrorResponse.message;
    }
    return message;
  }

  /**
   * Show a message of a given type
   * @param messageObject The message to display
   * @param type The type of the message (success, error, info, warning)
   * @param keepAfterNavigationChange If true, the message will be displayed after a navigation changed
   */
  showMessage(messageObject: any, type: string, keepAfterNavigationChange:boolean = false, timeoutInSeconds: number = null) {
    this.setKeepAfterNavigationChange(keepAfterNavigationChange);
    this.message = {
      type: type,
      text: this.getObjectMessage(messageObject),
      // if the timeoutInSeconds is not set, we use the default timeout for success messages
      timeoutInSeconds: !timeoutInSeconds && type =="success" ?this.DEFAULT_TIMEOUT: timeoutInSeconds
    };
    this.displayCurrentMessage();
  }

  async showMessageForTranslation(translationCode: string,type: string, keepAfterNavigationChange = false, timeoutInSeconds: number = null) : Promise<void> {
      let message = await lastValueFrom(this.translateService.get(translationCode))
      this.showMessage(message, type, keepAfterNavigationChange, timeoutInSeconds);
  }

  successForTranslation(translationCode: string, keepAfterNavigationChange = false, timeoutInSeconds: number = null) {
    this.showMessageForTranslation(translationCode, 'success', keepAfterNavigationChange, timeoutInSeconds);
  }

  success(message: string, keepAfterNavigationChange = false, timeoutInSeconds: number = null) {
    this.showMessage(message, 'success', keepAfterNavigationChange, timeoutInSeconds);
  }

  warning(message: string, keepAfterNavigationChange = false, timeoutInSeconds: number = null) {
    this.showMessage(message, 'warning', keepAfterNavigationChange, timeoutInSeconds);
  }

  error(message: any, keepAfterNavigationChange = false, timeoutInSeconds: number = null) {
    this.showMessage(message, 'error', keepAfterNavigationChange, timeoutInSeconds);
  }

  exception(message: string, error: any, keepAfterNavigationChange = false): void {
    const errMsg = error.message || (error.json ? error.json().message : error);
    this.error(message + ' \n' + errMsg, keepAfterNavigationChange);
  }

  getMessage(): Observable<any> {
    return this.subject.asObservable();
  }
}
