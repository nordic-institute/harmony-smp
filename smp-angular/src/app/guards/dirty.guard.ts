import {inject} from '@angular/core';
import {NavigationService} from "../window/sidenav/navigation-model.service";
import {
  CancelDialogComponent
} from "../common/dialogs/cancel-dialog/cancel-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../window/sidenav/navigation-on-leave-guard";
import {SecurityService} from "../security/security.service";

export const dirtyDeactivateGuard = (component: BeforeLeaveGuard) => {
  const navigationService: NavigationService = inject(NavigationService);
  const dialog: MatDialog = inject(MatDialog);
  const securityService: SecurityService = inject(SecurityService);

  if (!component) {
    return true;
  }

  if (component.isDirty && !component.isDirty()) {
    return true;
  }
  // if the user is not logged in, we can navigate away
  // no data can be saved anymore
  if (!securityService.hasUISessionData()) {
    return true;
  }

  return dialog.open(CancelDialogComponent).afterClosed().toPromise().then((cancelChanges: boolean) => {
    // rollback the navigation
    if (!cancelChanges) {
      navigationService.selectPreviousNode()
    }
    return cancelChanges;
  });
}
