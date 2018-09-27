import {Component} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'app-messagefilter-dialog',
  templateUrl: './save-dialog.component.html'
})
export class SaveDialogComponent {

  constructor(public dialogRef: MdDialogRef<SaveDialogComponent>) {
  }

}

