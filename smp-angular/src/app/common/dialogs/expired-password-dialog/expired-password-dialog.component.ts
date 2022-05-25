import { Component} from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'smp-expired-password-dialog',
  templateUrl: './expired-password-dialog.component.html',
})
export class ExpiredPasswordDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ExpiredPasswordDialogComponent>,
  ) { }

}
