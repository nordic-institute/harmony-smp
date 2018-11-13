import {Component, ViewChild} from '@angular/core';
import {SecurityService} from './security/security.service';
import {Router} from '@angular/router';
import {Authority} from "./security/authority.model";
import {AlertService} from "./alert/alert.service";
import {MatDialog, MatDialogRef} from "@angular/material";
import {GlobalLookups} from "./common/global-lookups";
import {UserController} from "./user/user-controller";
import {HttpClient} from "@angular/common/http";
import {SearchTableEntityStatus} from "./common/search-table/search-table-entity-status.model";
import {SmpConstants} from "./smp.constants";
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
    return this.securityService.isCurrentUserInRole([ Authority.SMP_ADMIN]);
  }

  isCurrentUserServiceGroupAdmin(): boolean {
    return this.securityService.isCurrentUserInRole([ Authority.SERVICE_GROUP_ADMIN]);
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

  get currentUser(): string {
    let user = this.securityService.getCurrentUser();
    return user ? user.username : "";
  }

  get currentUserRoleDescription(): string {
      if (this.securityService.isCurrentUserSystemAdmin()){
        return "System administrator";
      } else if (this.securityService.isCurrentUserSMPAdmin()){
        return "SMP administrator";
      } else if (this.securityService.isCurrentUserServiceGroupAdmin()){
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

}
