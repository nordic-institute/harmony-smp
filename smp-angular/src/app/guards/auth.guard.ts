import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {SecurityService} from "../security/security.service";
import {AlertMessageService} from "../common/alert-message/alert-message.service";
import {NavigationService} from "../window/sidenav/navigation-model.service";

export const authGuard = () => {
  const navigationService = inject(NavigationService);
  const securityService = inject(SecurityService);
  const alertService = inject(AlertMessageService);
  const router = inject(Router);

  console.log("guard - check for authentication: " + router.url)

  // test if logged in
  securityService.isAuthenticated(true).subscribe((isAuthenticated: boolean) => {
    console.log("Refresh application configuration is authenticated " + isAuthenticated )
    if (isAuthenticated) {
      console.log("guard - it is authenticated")
      return true;
    } else {
      console.log("guard - it is not authenticated")
      alertService.error('You have been logged out because of inactivity or missing access permissions.', true);
      // Redirect to the login page
      navigationService.reset();
      router.navigate(['/login'], {queryParams: {returnUrl: router.url}});
      router.parseUrl('/login');
    }
  });
};
