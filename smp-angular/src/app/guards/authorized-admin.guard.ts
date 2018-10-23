import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {SecurityService} from '../security/security.service';
import {AuthorizedGuard} from './authorized.guard';
import {Authority} from '../security/authority.model';

@Injectable()
export class AuthorizedAdminGuard extends AuthorizedGuard {

  constructor(securityService: SecurityService) {
    super(securityService);
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return super.canActivate(route, state);
  }


  getAllowedRoles(route: ActivatedRouteSnapshot): Array<Authority> {
    // TODO check if we need the SMP admin in here
    return [Authority.SYSTEM_ADMIN , Authority.SMP_ADMIN];
  }
}
