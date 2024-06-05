import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, Subscription} from "rxjs";
import {Injectable, OnDestroy, OnInit} from "@angular/core";
import {SecurityEventService} from "../security/security-event.service";
import {SecurityService} from "../security/security.service";
import {AlertMessageService} from "../common/alert-message/alert-message.service";

@Injectable({
  providedIn: 'root'
})
export class HttpSessionInterceptor implements HttpInterceptor, OnInit, OnDestroy {

  private securityEventService: SecurityEventService;

  private securityService: SecurityService;

  private alertMessageService: AlertMessageService;

  private loginSubscription: Subscription;

  private timerId: number;

  private sessionExpiringSoon = false;

  constructor(securityService: SecurityService,
              securityEventService: SecurityEventService,
              alertMessageService: AlertMessageService) {
    this.securityService = securityService;
    this.securityEventService = securityEventService;
    this.alertMessageService = alertMessageService;
  }

  ngOnInit() {
    this.loginSubscription = this.securityEventService.onLoginSuccessEvent().subscribe(() => this.sessionExpiringSoon = false);
  }

  ngOnDestroy() {
    this.loginSubscription.unsubscribe();
  }

  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    clearTimeout(this.timerId);
    let user = this.securityService.getCurrentUser();
    if (user && user.sessionMaxIntervalTimeoutInSeconds && user.sessionMaxIntervalTimeoutInSeconds > 60) {
      let timeout = (user.sessionMaxIntervalTimeoutInSeconds - 60) * 1000;
      this.timerId = setTimeout(() => this.alertMessageService.warning("Your current session is about to expire!"), timeout);
    }
    return next.handle(req);
  }

}
