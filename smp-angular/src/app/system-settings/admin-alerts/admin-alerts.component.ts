import {Component, OnDestroy, OnInit,} from '@angular/core';
import {SecurityService} from "../../security/security.service";
import {User} from "../../security/user.model";
import {MatDialog} from "@angular/material/dialog";
import {SecurityEventService} from "../../security/security-event.service";
import {Subscription} from "rxjs";
import {SmpConstants} from "../../smp.constants";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {UserRo} from "../../common/model/user-ro.model";


@Component({
  templateUrl: './admin-alerts.component.html',
  styleUrls: ['./admin-alerts.component.scss']
})
export class AdminAlertsComponent implements OnInit, OnDestroy, BeforeLeaveGuard {

  currentUserData: UserRo;
  loggedInUser: User;

  private securityEventServiceSub: Subscription = Subscription.EMPTY;
  private onProfileDataChangedEventSub: Subscription = Subscription.EMPTY;

  constructor(
    private securityService: SecurityService,
    private securityEventService: SecurityEventService,
    public dialog: MatDialog) {


    this.securityEventServiceSub = this.securityEventService.onLoginSuccessEvent().subscribe(() => {
        this.updateUserData(this.securityService.getCurrentUser())
      }
    );
  }

  get adminAlertsUrl(): string {
    return SmpConstants.REST_INTERNAL_ALERT_MANAGE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, this.loggedInUser?.userId);
  }

  ngOnInit(): void {
    this.updateUserData(this.securityService.getCurrentUser())
  }

  ngOnDestroy(): void {
    this.securityEventServiceSub.unsubscribe();
    this.onProfileDataChangedEventSub.unsubscribe();
  }

  /**
   * This is a Readonly components, and it always returns false.
   */
  isDirty(): boolean {
    return false;
  }

  private updateUserData(user: User) {
    this.currentUserData = this.convert(user);
    this.loggedInUser = user;
  }


  private convert(user: User): UserRo {
    return {
      ...user,
      active: true,
      status: undefined,
      statusPassword: 0
    } as UserRo;
  }
}
