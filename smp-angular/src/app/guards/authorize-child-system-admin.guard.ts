import {inject} from '@angular/core';
import {SecurityService} from '../security/security.service';
import {AlertMessageService} from "../common/alert-message/alert-message.service";
import {Authority} from "../security/authority.model";
import {ActivatedRouteSnapshot, CanActivateChildFn, RouterStateSnapshot} from "@angular/router";


export const authorizeChildSystemAdminGuard: CanActivateChildFn =
  (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    console.log("Is user is authorized");
    const alertService: AlertMessageService = inject(AlertMessageService);
    const securityService: SecurityService = inject(SecurityService);
    let isAuthorized: boolean = securityService.isCurrentUserInRole([Authority.SYSTEM_ADMIN]);
    if (!isAuthorized) {
      alertService.error('Navigation denied! Missing access permissions.', true);
    }
    return isAuthorized;
  };
