import {Component} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'app-messagelog-details',
  templateUrl: './servicegroup-details-dialog.component.html'
})
export class ServicegroupDetailsDialogComponent {

  servicegroup;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<ServicegroupDetailsDialogComponent>) {
  }

}
