import {Component} from "@angular/core";
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'app-default-password-dialog',
  templateUrl: './default-password-dialog.component.html',
  styleUrls: ['./default-password-dialog.component.css']
})
export class DefaultPasswordDialogComponent {

  constructor(public dialogRef: MdDialogRef<DefaultPasswordDialogComponent>) { }

}
