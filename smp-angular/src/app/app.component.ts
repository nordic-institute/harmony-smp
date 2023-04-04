import {Component, ViewChild} from '@angular/core';
import {SecurityService} from './security/security.service';
import {Router} from '@angular/router';
import {Authority} from "./security/authority.model";
import {AlertMessageService} from "./common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {GlobalLookups} from "./common/global-lookups";
import {UserController} from "./system-settings/user/user-controller";
import {HttpClient} from "@angular/common/http";
import {SidenavComponent} from "./window/sidenav/sidenav.component";
import {ToolbarComponent} from "./window/toolbar/toolbar.component";
import {ThemeService} from "./common/theme-service/theme.service";
import {AlertRo} from "./alert/alert-ro.model";
import {AlertMessageComponent} from "./common/alert-message/alert-message.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  @ViewChild('alertMessage') alertMessage: AlertMessageComponent;

  @ViewChild('sidenav') sidenav: SidenavComponent;
  @ViewChild('windowToolbar') windowToolbar: ToolbarComponent;


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
    private themeService: ThemeService,
  ) {
    this.userController = new UserController(this.http, this.lookups, this.dialog);

    themeService.updateThemeFromLocalStorage();
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
    this.router.navigate(['/search']).then((result) => {
      if (result) {
        this.securityService.logout();
      }
    });
  }

  toggleMenu() {
    this.fullMenu = !this.fullMenu;
    this.sidenav.showExpanded(this.fullMenu);
    this.windowToolbar.showExpanded(this.fullMenu);

    window.dispatchEvent(new Event('resize'));
    /*
    setTimeout(() => {
      var evt = document.createEvent("HTMLEvents");
      evt.initEvent('resize', true, false);
      window.dispatchEvent(evt);
    }, 200)*/
    //ugly hack but otherwise the ng-datatable doesn't resize when collapsing the menu
    //alternatively this can be tried (https://github.com/swimlane/ngx-datatable/issues/193) but one has to implement it on every page
    //containing a ng-datatable and it only works after one clicks inside the table
  }

  clearWarning() {
    this.alertService.clearAlert();
  }

  onDrawerContentScroll(scrollEvent: any){
    let scrollTop = scrollEvent.srcElement.scrollTop;
    this.alertMessage.setSticky(scrollTop > 0)
  }

}
