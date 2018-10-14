import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material';

@Component({
  selector: 'app-messagelog-details',
  templateUrl: './service-group-details-dialog.component.html'
})
export class ServiceGroupDetailsDialogComponent {

  servicegroup;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MatDialogRef<ServiceGroupDetailsDialogComponent>) {
  }

}
