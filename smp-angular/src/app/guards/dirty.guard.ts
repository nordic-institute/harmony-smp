import {inject} from '@angular/core';
import {NavigationService} from "../window/sidenav/navigation-model.service";
import {CancelDialogComponent} from "../common/dialogs/cancel-dialog/cancel-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../window/sidenav/navigation-on-leave-guard";

export const dirtyDeactivateGuard = (component: BeforeLeaveGuard) => {
  const navigationService = inject(NavigationService);
  const dialog = inject(MatDialog);

  if (component.isDirty && !component.isDirty()) return true;
  return dialog.open(CancelDialogComponent).afterClosed().toPromise().then((cancelChanges: boolean) => {
    // rollback the navigation
    if (!cancelChanges) {
      navigationService.selectPreviousNode()
    }
    return cancelChanges;
  });
}
