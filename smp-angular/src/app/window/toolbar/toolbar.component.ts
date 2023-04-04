import {Component, OnInit} from '@angular/core';

import {SecurityService} from '../../security/security.service';
import {Authority} from "../../security/authority.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {UserDetailsDialogMode} from "../../system-settings/user/user-details-dialog/user-details-dialog.component";
import {EntityStatus} from "../../common/model/entity-status.model";
import {UserService} from "../../system-settings/user/user.service";
import {UserController} from "../../system-settings/user/user-controller";
import {HttpClient} from "@angular/common/http";
import {GlobalLookups} from "../../common/global-lookups";

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

export class ToolbarComponent implements OnInit {

  fullMenu: boolean = true;
  userController: UserController;


  constructor(private alertService: AlertMessageService,
              private securityService: SecurityService,
              private userService: UserService,
              private http: HttpClient,
              private dialog: MatDialog,
              private lookups: GlobalLookups) {
    this.userController = new UserController(this.http, this.lookups, this.dialog);
  }

  ngOnInit(): void {

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
    let user = this.securityService.getCurrentUser();
    let userDesc =  user ? (
        user.fullName? user.fullName +" ["+user.username+"]":user.username)
      :"";

    return  (userDesc.length>25)?userDesc.slice(0,25) + "...":userDesc
  }



  editCurrentUser() {
    const formRef: MatDialogRef<any> = this.userController.newDialog({
      data: {mode: UserDetailsDialogMode.PREFERENCES_MODE, row: this.securityService.getCurrentUser()}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const user = {...formRef.componentInstance.getCurrent(), status: EntityStatus.UPDATED};
        this.userService.updateUser(user);
      }
    });
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
    const formRef: MatDialogRef<any> = this.userController.changePasswordDialog({
      data: {user: this.securityService.getCurrentUser(), adminUser: false}
    });
  }

  regenerateCurrentUserAccessToken() {
    const formRef: MatDialogRef<any> = this.userController.generateAccessTokenDialog({
      data: {user: this.securityService.getCurrentUser(), adminUser: false}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        let user = {...formRef.componentInstance.getCurrent()};
        let currUser = this.securityService.getCurrentUser();
        currUser.accessTokenId = user.accessTokenId;
        currUser.accessTokenExpireOn = user.accessTokenExpireOn;
        this.securityService.updateUserDetails(currUser);
      }
    });
  }


  showExpanded(expand: boolean) {
    this.fullMenu = expand;
  }

}
