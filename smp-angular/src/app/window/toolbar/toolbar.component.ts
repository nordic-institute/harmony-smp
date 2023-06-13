import {Component} from '@angular/core';

import {SecurityService} from '../../security/security.service';
import {Authority} from "../../security/authority.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {UserService} from "../../system-settings/user/user.service";
import {UserController} from "../../system-settings/user/user-controller";
import {HttpClient} from "@angular/common/http";
import {GlobalLookups} from "../../common/global-lookups";
import {NavigationService} from "../sidenav/navigation-model.service";

/**
 * Expanded side navigation panel of the DomiSMP. The component shows all tools/pages according to user role and permissions
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component({
  moduleId: module.id,
  selector: 'window-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})

export class ToolbarComponent {

  fullMenu: boolean = true;
  userController: UserController;


  constructor(private alertService: AlertMessageService,
              private securityService: SecurityService,
              private userService: UserService,
              private navigation: NavigationService,
              private http: HttpClient,
              private dialog: MatDialog,
              private lookups: GlobalLookups) {
    this.userController = new UserController(this.http, this.lookups, this.dialog);
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

  get currentUserRoleDescription(): string {
    if (this.securityService.isCurrentUserSystemAdmin()) {
      return "System administrator";
    }
    return "SMP user";
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
