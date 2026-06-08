import {Component, HostListener, ViewChild} from '@angular/core';
import {SecurityService} from './security/security.service';
import {Router} from '@angular/router';
import {Authority} from "./security/authority.model";
import {
  AlertMessageService
} from "./common/alert-message/alert-message.service";
import {SidenavComponent} from "./window/sidenav/sidenav.component";
import {ThemeService} from "./common/theme-service/theme.service";
import {TranslateService} from "@ngx-translate/core";
import {WindowSpinnerService} from "./common/services/window-spinner.service";


@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    standalone: false
})
export class AppComponent {

  @ViewChild('sidenav') sidenav: SidenavComponent;

  fullMenu: boolean = true;
  get menuClass(): string {
    return this.fullMenu ? 'menu-expanded' : 'menu-collapsed';
  }

  constructor(
    private alertService: AlertMessageService,
    private securityService: SecurityService,
    private windowSpinnerService: WindowSpinnerService,
    private router: Router,
    private themeService: ThemeService,
    private translateService: TranslateService,
  ) {
    this.themeService.updateThemeFromLocalStorage();

    this.translateService.setDefaultLang("en");
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

    window.dispatchEvent(new Event('resize'));
  }

  clearWarning() {
    this.alertService.clearAlert();
  }

  onDrawerContentScroll(scrollEvent: any) {
    let scrollTop = scrollEvent.srcElement.scrollTop;
    this.alertService.setKeepAfterNavigationChange(scrollTop > 0)
  }

  get showSpinner(): boolean {
    return this.windowSpinnerService.showSpinner
  }

// Listeners for activity monitoring
// Every time one of these events are triggered, all the "watches" are reseted
  @HostListener('window:mousemove')
  mouseMove() {
    this.refreshUserState();
  }

  @HostListener('keydown')
  keyboardClick() {
    this.refreshUserState();
  }

  @HostListener('touchstart')
  screenTouched() {
    this.refreshUserState();
  }

  @HostListener('touchmove')
  screenDragged() {
    this.refreshUserState();
  }

  refreshUserState() {
    // if user is not logged in, do nothing
    if (!this.securityService.isAuthenticated(false)) {
      return;
    }
    this.securityService.uiUserActivityDetected()
  }
}
