import {Component} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'user-details-dialog',
  templateUrl: './user-details-dialog.component.html'
})
export class UserDetailsDialogComponent {

  user;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<UserDetailsDialogComponent>) {
  }

}
