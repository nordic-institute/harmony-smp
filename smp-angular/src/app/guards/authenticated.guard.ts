import {Injectable} from '@angular/core';
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {SecurityService} from '../security/security.service';
import {ReplaySubject} from 'rxjs';
import {AlertService} from "../alert/alert.service";

@Injectable()
export class AuthenticatedGuard implements CanActivate {

  constructor(private router: Router, private securityService: SecurityService, private alertService: AlertService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const subject = new ReplaySubject<boolean>();
    this.securityService.isAuthenticated(true).subscribe((isAuthenticated: boolean) => {
      if(isAuthenticated) {
        subject.next(true);
      } else {
        console.log("User session is not active")
        // not logged in so redirect to login page with the return url
        this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
        subject.next(false);
        this.alertService.error('You have been logged out because of inactivity or missing access permissions.', true);
      }
    });
    return subject.asObservable();

  }
}
