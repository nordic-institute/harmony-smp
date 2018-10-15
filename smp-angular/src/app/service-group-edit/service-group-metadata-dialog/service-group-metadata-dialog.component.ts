import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material';

@Component({
  selector: 'app-messagelog-dialog',
  templateUrl: './service-group-metadata-dialog.component.html',
  styleUrls: ['./service-group-metadata-dialog.component.css']
})
export class ServiceGroupMetadataDialogComponent {

  constructor(public dialogRef: MatDialogRef<ServiceGroupMetadataDialogComponent>) {
  }

}
