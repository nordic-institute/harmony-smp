import {Component} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'app-messagelog-details',
  templateUrl: './service-group-details-dialog.component.html'
})
export class ServiceGroupDetailsDialogComponent {

  servicegroup;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<ServiceGroupDetailsDialogComponent>) {
  }

}
