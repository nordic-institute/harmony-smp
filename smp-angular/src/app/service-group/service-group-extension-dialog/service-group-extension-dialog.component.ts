import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material';

@Component({
  selector: 'servicegroup-extension-dialog',
  templateUrl: './service-group-extension-dialog.component.html',
})
export class ServiceGroupExtensionDialogComponent {

  servicegroup;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MatDialogRef<ServiceGroupExtensionDialogComponent>) {
  }

}
