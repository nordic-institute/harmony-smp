import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material';

@Component({
  selector: 'smp-cancel-dialog',
  templateUrl: './cancel-dialog.component.html',
})
export class CancelDialogComponent {

  constructor(public dialogRef: MatDialogRef<CancelDialogComponent>) {
  }

}
