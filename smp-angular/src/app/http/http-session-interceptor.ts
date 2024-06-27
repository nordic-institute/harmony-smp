import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {SecurityService} from "../security/security.service";
import {AlertMessageService} from "../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {
  SessionExpirationDialogComponent
} from "../common/dialogs/session-expiration-dialog/session-expiration-dialog.component";

/*
 * A custom interceptor that handles session expiration before it happens.
 *
 * Users are prompted 60 seconds before their HTTP sessions are about to expire
 * and asked whether they would like to logout or extend the session time again.
 */
@Injectable({
  providedIn: 'root'
})
export class HttpSessionInterceptor implements HttpInterceptor {

  private readonly TIME_BEFORE_EXPIRATION_IN_SECONDS = 60;
  private readonly MAXIMUM_TIMEOUT_VALUE = 2147483647;

  private timerId: number;
  private timerToLogoutId: number;

  constructor(public securityService: SecurityService,
              public alertMessageService: AlertMessageService,
              private dialog: MatDialog) {
  }

  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    clearTimeout(this.timerId);
    clearTimeout(this.timerToLogoutId);
    let user = this.securityService.getCurrentUser();
    if (user && user.sessionMaxIntervalTimeoutInSeconds && user.sessionMaxIntervalTimeoutInSeconds > this.TIME_BEFORE_EXPIRATION_IN_SECONDS) {
      let timeout = Math.min((user.sessionMaxIntervalTimeoutInSeconds - this.TIME_BEFORE_EXPIRATION_IN_SECONDS) * 1000, this.MAXIMUM_TIMEOUT_VALUE);
      this.timerId = setTimeout(() => this.sessionExpiringSoon(user.sessionMaxIntervalTimeoutInSeconds), timeout);
    }
    return next.handle(req);
  }

  private sessionExpiringSoon(timeout) {
    // Logout the user after the session expires
    this.timerToLogoutId = setTimeout(() => {
      this.securityService.logout();
    }, this.TIME_BEFORE_EXPIRATION_IN_SECONDS * 1000);

    this.dialog.open(SessionExpirationDialogComponent, {
      data: {
        timeLeft: this.TIME_BEFORE_EXPIRATION_IN_SECONDS,
        timeout
      }
    });
  }
}

