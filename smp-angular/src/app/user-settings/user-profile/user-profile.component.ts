import {Component, OnDestroy, OnInit, ViewChild,} from '@angular/core';
import {SecurityService} from "../../security/security.service";
import {User} from "../../security/user.model";
import {UserService} from "../../system-settings/user/user.service";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {UserRo} from "../../system-settings/user/user-ro.model";
import {UserProfilePanelComponent} from "../../common/panels/user-settings-panel/user-profile-panel.component";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {
  PasswordChangeDialogComponent
} from "../../common/dialogs/password-change-dialog/password-change-dialog.component";
import {UserDetailsDialogMode} from "../../system-settings/user/user-details-dialog/user-details-dialog.component";
import {SecurityEventService} from "../../security/security-event.service";
import {Subscription} from "rxjs";


@Component({
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit, OnDestroy, BeforeLeaveGuard {

  @ViewChild('userProfilePanel') userProfilePanel: UserProfilePanelComponent;
  currentUserData: UserRo;
  loggedInUser: User;

  private securityEventServiceSub: Subscription = Subscription.EMPTY;
  private onProfileDataChangedEventSub: Subscription = Subscription.EMPTY;

  constructor(
    private userService: UserService,
    private securityService: SecurityService,
    private securityEventService: SecurityEventService,
    public dialog: MatDialog) {


    this.securityEventServiceSub = this.securityEventService.onLoginSuccessEvent().subscribe(() => {
        this.updateUserData(this.securityService.getCurrentUser())
      }
    );
    this.onProfileDataChangedEventSub = userService.onProfileDataChangedEvent().subscribe(updatedUser => {
        this.updateUserData(updatedUser);
      }
    );
  }

  ngOnInit(): void {
    this.updateUserData(this.securityService.getCurrentUser())
  }

  ngOnDestroy(): void {
    this.securityEventServiceSub.unsubscribe();
    this.onProfileDataChangedEventSub.unsubscribe();
  }


  private updateUserData(user: User) {
    this.currentUserData = this.convert(user);
    this.loggedInUser = user;
  }

  onSaveUserEvent(user: UserRo) {
    let userData: User = {...this.loggedInUser};
    // change only allowed data
    userData.emailAddress = user.emailAddress;
    userData.fullName = user.fullName;
    userData.smpTheme = user.smpTheme;
    userData.smpLocale = user.smpLocale;

    this.userService.updateUser(userData);

  }

  isDirty(): boolean {
    return this.userProfilePanel.isDirty()
  }

  changeUserPasswordEvent(user: UserRo) {
    const formRef: MatDialogRef<any> = this.changePasswordDialog({
      data: {
        user: user,
        adminUser: false
      },
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        this.currentUserData.passwordExpireOn = result.passwordExpireOn;
        this.currentUserData = {...this.currentUserData}
      }
    });
  }

  public changePasswordDialog(config?: MatDialogConfig): MatDialogRef<PasswordChangeDialogComponent> {
    return this.dialog.open(PasswordChangeDialogComponent, this.convertConfig(config));
  }


  private convertConfig(config) {
    return (config && config.data)
      ? {
        ...config,
        data: {
          ...config.data,
          mode: config.data.mode || (config.data.edit ? UserDetailsDialogMode.EDIT_MODE : UserDetailsDialogMode.NEW_MODE)
        }
      }
      : config;
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
