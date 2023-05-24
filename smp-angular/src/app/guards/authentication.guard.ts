import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {SecurityService} from "../security/security.service";
import {AlertMessageService} from "../common/alert-message/alert-message.service";
import {NavigationService} from "../window/sidenav/navigation-model.service";

export const authenticationGuard = () => {
  const navigationService = inject(NavigationService);
  const securityService = inject(SecurityService);
  const alertService = inject(AlertMessageService);

  // test if logged in
  securityService.isAuthenticated(true).subscribe((isAuthenticated: boolean) => {
    if (isAuthenticated) {
      return true;
    } else {
      alertService.error('You have been logged out because of inactivity or missing access permissions.', true);
      // Redirect to the login page
      navigationService.navigateToLogin();
    }
  });
};
