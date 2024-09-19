import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {SecurityService} from "../../../security/security.service";

@Component({
  templateUrl: './session-expiration-dialog.component.html',
})
export class SessionExpirationDialogComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              public dialogRef: MatDialogRef<SessionExpirationDialogComponent>,
              public securityService: SecurityService) {
    // Disable the ability to close the dialog by clicking outside of it
    dialogRef.disableClose = true;
  }

  public onExtendSessionClicked() {
    // just make another simple call to the backend which cancels out the current inactivity
    this.securityService.isAuthenticated(true);
    this.dialogRef.close();
  }

  onLogoutClicked() {
    this.securityService.logout();
    this.dialogRef.close();
  }
}

