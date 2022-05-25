import {Component} from '@angular/core';
import {SecurityService} from './security/security.service';
import {Router} from '@angular/router';
import {Authority} from "./security/authority.model";
import {AlertMessageService} from "./common/alert-message/alert-message.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {GlobalLookups} from "./common/global-lookups";
import {UserController} from "./user/user-controller";
import {HttpClient} from "@angular/common/http";
import {SearchTableEntityStatus} from "./common/search-table/search-table-entity-status.model";
import {UserService} from "./user/user.service";
import {UserDetailsDialogMode} from "./user/user-details-dialog/user-details-dialog.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  fullMenu: boolean = true;
  menuClass: string = this.fullMenu ? "menu-expanded" : "menu-collapsed";
  userController: UserController;

  constructor(
    private alertService: AlertMessageService,
    private securityService: SecurityService,
    private router: Router,
    private http: HttpClient,
    private dialog: MatDialog,
    private lookups: GlobalLookups,
    private userService: UserService,
  ) {
    this.userController = new UserController(this.http, this.lookups, this.dialog);
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

  editCurrentUser() {
    const formRef: MatDialogRef<any> = this.userController.newDialog({
      data: {mode: UserDetailsDialogMode.PREFERENCES_MODE, row: this.securityService.getCurrentUser()}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const user = {...formRef.componentInstance.getCurrent(), status: SearchTableEntityStatus.UPDATED};
        this.userService.updateUser(user);
      }
    });
  }


  changeCurrentUserPassword() {
    const formRef: MatDialogRef<any> = this.userController.changePasswordDialog({
      data: {user: this.securityService.getCurrentUser(), adminUser:false}
    });
  }

  regenerateCurrentUserAccessToken() {
    const formRef: MatDialogRef<any> = this.userController.generateAccessTokenDialog({
      data: {user: this.securityService.getCurrentUser(), adminUser:false}
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

  get currentUser(): string {
    let user = this.securityService.getCurrentUser();
    return user ? user.username : "";
  }

  get currentUserRoleDescription(): string {
    if (this.securityService.isCurrentUserSystemAdmin()) {
      return "System administrator";
    } else if (this.securityService.isCurrentUserSMPAdmin()) {
      return "SMP administrator";
    } else if (this.securityService.isCurrentUserServiceGroupAdmin()) {
      return "Service group administrator";
    }
    return "";
  }

  logout(event: Event): void {
    event.preventDefault();

    this.router.navigate(['/search']).then((ok) => {
      if (ok) {

        this.securityService.logout();
      }
    });
  }

  toggleMenu() {
    this.fullMenu = !this.fullMenu;
    this.menuClass = this.fullMenu ? "menu-expanded" : "menu-collapsed";
    setTimeout(() => {
      var evt = document.createEvent("HTMLEvents");
      evt.initEvent('resize', true, false);
      window.dispatchEvent(evt);
    }, 500)
    //ugly hack but otherwise the ng-datatable doesn't resize when collapsing the menu
    //alternatively this can be tried (https://github.com/swimlane/ngx-datatable/issues/193) but one has to implement it on every page
    //containing a ng-datatable and it only works after one clicks inside the table
  }

  clearWarning() {
    this.alertService.clearAlert();
  }

}
