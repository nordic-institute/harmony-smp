import {Component, OnInit, ViewChild} from '@angular/core';

import {SecurityService} from '../../security/security.service';
import {User} from '../../security/user.model';
import {Authority} from "../../security/authority.model";
import {NavTree} from "./nav-tree/nav-tree.component";
import {SmpConstants} from "../../smp.constants";

/**
 * Expanded side navigation panel of the DomiSMP. The component shows all tools/pages according to user role and permissions
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component({
  moduleId: module.id,
  selector: 'smp-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})

export class SidenavComponent implements OnInit {

  @ViewChild('navtree') navtree: NavTree;
  currentUser: User;
  fullMenu: boolean = true;

  ngOnInit(): void {
    console.log("HomeComponent onInit")
    this.currentUser = this.securityService.getCurrentUser();
  }

  constructor(private securityService: SecurityService) {
  }

  showExpanded(expand: boolean) {
    this.fullMenu = expand;
    this.navtree.showExpandedMenu(expand);
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

  get expandedSideNavSize(){
    return SmpConstants.EXPANDED_MENU_WIDTH;
  }

  get collapsedSideNavSize(){
    return SmpConstants.COLLAPSED_MENU_WIDTH;
  }
}
