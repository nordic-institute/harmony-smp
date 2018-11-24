import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material';

@Component({
  selector: 'smp-save-dialog',
  templateUrl: './save-dialog.component.html',
})
export class SaveDialogComponent {

  constructor(public dialogRef: MatDialogRef<SaveDialogComponent>) {
  }

}

