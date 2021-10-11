import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-default-password-dialog',
  templateUrl: './default-password-dialog.component.html',
  styleUrls: ['./default-password-dialog.component.css']
})
export class DefaultPasswordDialogComponent {

  constructor(public dialogRef: MatDialogRef<DefaultPasswordDialogComponent>) { }

}
