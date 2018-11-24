import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'smp-expired-password-dialog',
  templateUrl: './expired-password-dialog.component.html',
})
export class ExpiredPasswordDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ExpiredPasswordDialogComponent>,
  ) { }

}
