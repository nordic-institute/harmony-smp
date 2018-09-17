import {Component} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'servicegroup-extension-dialog',
  templateUrl: './servicegroup-extension-dialog.component.html',
})
export class ServiceGroupExtensionDialogComponent {

  servicegroup;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<ServiceGroupExtensionDialogComponent>) {
  }

}
