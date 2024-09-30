import {Component, OnDestroy} from '@angular/core';

import {SecurityService} from '../../security/security.service';
import {Authority} from "../../security/authority.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import {GlobalLookups} from "../../common/global-lookups";
import {NavigationService} from "../sidenav/navigation-model.service";
import {UserController} from "../../common/services/user-controller";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom, Subscription} from "rxjs";
import {SecurityEventService} from "../../security/security-event.service";

/**
 * Expanded side navigation panel of the DomiSMP. The component shows all tools/pages according to user role and permissions
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component({
  selector: 'window-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})

export class ToolbarComponent implements OnDestroy{

  fullMenu: boolean = true;
  userController: UserController;
  currentUserRoleDescription = "";
  private sub: Subscription;

  constructor(private alertService: AlertMessageService,
              private securityService: SecurityService,
              private navigation: NavigationService,
              private http: HttpClient,
              private dialog: MatDialog,
              private lookups: GlobalLookups,
              private securityEventService: SecurityEventService,
              private translateService: TranslateService) {
    this.userController = new UserController(this.http, this.lookups, this.dialog);

    this.sub = this.securityEventService.onLoginSuccessEvent().subscribe(async user => await this.updateCurrentUserRoleDescription());
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  clearWarning() {
    this.alertService.clearAlert();
  }


  isCurrentUserSystemAdmin(): boolean {
    return this.securityService.isCurrentUserInRole([Authority.SYSTEM_ADMIN]);
  }

  isCurrentUserSMPAdmin(): boolean {
    return this.securityService.isCurrentUserInRole([Authority.SMP_ADMIN]);
  }

  isCurrentUserServiceGroupAdmin(): boolean {
    return this.securityService.isCurrentUserInRole([Authority.SERVICE_GROUP_ADMIN]);
  }

  navigateToLogin(): void {
    this.clearWarning();
    this.navigation.navigateToLogin();
  }

  logout(event: Event): void {
    event.preventDefault();
    this.securityService.logout();
  }

  get currentUser(): string {
    let userDesc = this.userTitle;
    return (userDesc.length > 25) ? userDesc.slice(0, 25) + "..." : userDesc
  }

  get userTitle() {
    let user = this.securityService.getCurrentUser();
    if (!user) {
      return ""
    }
    return !!user.fullName ? user.fullName + " [" + user.username + "]" : user.username;
  }

  editCurrentUser() {
    this.navigation.navigateToUserDetails();
  }

  async updateCurrentUserRoleDescription() {
    if (this.securityService.isCurrentUserSystemAdmin()) {
      this.currentUserRoleDescription = await lastValueFrom(this.translateService.get("toolbar.component.label.system.administrator.role"));
    } else {
      this.currentUserRoleDescription = await lastValueFrom(this.translateService.get("toolbar.component.label.user.role"));
    }
  }

  openCurrentCasUserData() {
    window.open(this.securityService.getCurrentUser().casUserDataUrl, "_blank");
  }

  get isWebServiceUserTokenAuthPasswdEnabled(): boolean {
    return this.lookups.cachedApplicationConfig?.webServiceAuthTypes?.includes('TOKEN');
  }

  get isUserAuthPasswdEnabled(): boolean {
    return this.lookups.cachedApplicationInfo?.authTypes.includes('PASSWORD');
  }

  get isUserAuthSSOEnabled(): boolean {
    return this.lookups.cachedApplicationInfo?.authTypes?.includes('SSO');
  }

  changeCurrentUserPassword() {
    this.userController.changePasswordDialog({
      data: {user: this.securityService.getCurrentUser(), adminUser: false}
    });
  }

  showExpanded(expand: boolean) {
    this.fullMenu = expand;
  }

}
