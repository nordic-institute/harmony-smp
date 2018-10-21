import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {SecurityService} from '../security/security.service';
import {AuthorizedGuard} from './authorized.guard';
import {Role} from '../security/role.model';

@Injectable()
export class AuthorizedAdminGuard extends AuthorizedGuard {

  constructor(securityService: SecurityService) {
    super(securityService);
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return super.canActivate(route, state);
  }


  getAllowedRoles(route: ActivatedRouteSnapshot): Array<Role> {
    // TODO check if we need the SMP admin in here
    return [Role.SYSTEM_ADMIN/*, Role.SMP_ADMIN*/];
  }
}
