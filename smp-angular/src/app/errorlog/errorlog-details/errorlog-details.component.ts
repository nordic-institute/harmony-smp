import { Component } from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'app-errorlog-details',
  templateUrl: './errorlog-details.component.html'
})
export class ErrorlogDetailsComponent {

  message;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<ErrorlogDetailsComponent>) { }

}
