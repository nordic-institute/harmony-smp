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
 * An custom interceptor that handles session expiration before it happens.
 *
 * Users are prompted 60 seconds before their HTTP sessions are about to expire
 * and asked whether they would like to logout or extend the session time again.
 */
@Injectable({
  providedIn: 'root'
})
export class HttpSessionInterceptor implements HttpInterceptor {

  private readonly TIME_BEFORE_EXPIRATION_IN_SECONDS = 60;

  private timerId: number;

  constructor(public securityService: SecurityService,
              public alertMessageService: AlertMessageService,
              private dialog: MatDialog) {
  }

  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    clearTimeout(this.timerId);
    let user = this.securityService.getCurrentUser();
    if (user && user.sessionMaxIntervalTimeoutInSeconds && user.sessionMaxIntervalTimeoutInSeconds > this.TIME_BEFORE_EXPIRATION_IN_SECONDS) {
      let timeout = (user.sessionMaxIntervalTimeoutInSeconds - this.TIME_BEFORE_EXPIRATION_IN_SECONDS) * 1000;
      this.timerId = setTimeout(() => this.sessionExpiringSoon(user.sessionMaxIntervalTimeoutInSeconds), timeout);
    }
    return next.handle(req);
  }

  private sessionExpiringSoon(timeout) {
    this.dialog.open(SessionExpirationDialogComponent, {
      data: {
        timeLeft: this.TIME_BEFORE_EXPIRATION_IN_SECONDS,
        timeout
      }
    });
  }
}
