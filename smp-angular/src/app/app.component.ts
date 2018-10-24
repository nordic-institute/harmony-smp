import {Component, OnInit, ViewChild} from '@angular/core';
import {SecurityService} from './security/security.service';
import {Router, RouterOutlet} from '@angular/router';
import {SecurityEventService} from './security/security-event.service';
import {Title} from '@angular/platform-browser';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Authority} from "./security/authority.model";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  _currentUser: string;
  fullMenu: boolean = true;
  menuClass: string = this.fullMenu ? "menu-expanded" : "menu-collapsed";

  @ViewChild(RouterOutlet)
  outlet: RouterOutlet;

  constructor(private securityService: SecurityService,
              private router: Router,
              private securityEventService: SecurityEventService,
              private http: HttpClient,
              private titleService: Title) {
/*
    let applicationNameResponse: Observable<string> = this.http.get<string>('rest/application/name');

    applicationNameResponse.subscribe((name: string) => {
      this.titleService.setTitle(name);
    });
    */
  }

  ngOnInit() {
    /*
    this.securityEventService.onLoginSuccessEvent().subscribe(
      data => {
        this.isAdmin = this.securityService.isCurrentUserAdmin();
      });

    this.securityEventService.onLoginErrorEvent().subscribe(
      error => {
        this.isAdmin = this.securityService.isCurrentUserAdmin();
      });

    this.securityEventService.onLogoutSuccessEvent().subscribe(
      data => {
        this.isAdmin = this.securityService.isCurrentUserAdmin();
        this.router.navigate(['/login']);
      });*/
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

  get currentUser(): string {
    let user = this.securityService.getCurrentUser();
    return user ? user.username : "";
  }


  get getCurrentUserRoleDescription(): string {
      if (this.securityService.isCurrentUserSystemAdmin()){
        return "System administrator";
      } else if (this.securityService.isCurrentUserSMPAdmin()){
        return "SMP administrator";
      } else if (this.securityService.isCurrentUserServiceGroupAdmin()){
        return "Service group administrator"
      }
      return "";
  }


  logout(event: Event): void {
    event.preventDefault();
    this.router.navigate(['/search']).then((ok) => {
      if (ok) {
        this.securityService.logout();
      }
    })
  }

  toggleMenu() {
    this.fullMenu = !this.fullMenu
    this.menuClass = this.fullMenu ? "menu-expanded" : "menu-collapsed"
    setTimeout(() => {
      var evt = document.createEvent("HTMLEvents")
      evt.initEvent('resize', true, false)
      window.dispatchEvent(evt)
    }, 500)
    //ugly hack but otherwise the ng-datatable doesn't resize when collapsing the menu
    //alternatively this can be tried (https://github.com/swimlane/ngx-datatable/issues/193) but one has to implement it on every page
    //containing a ng-datatable and it only works after one clicks inside the table
  }

}
