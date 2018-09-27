import {Component} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'domain-details-dialog',
  templateUrl: './domain-details-dialog.component.html'
})
export class DomainDetailsDialogComponent {

  domain;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<DomainDetailsDialogComponent>) {
  }
}
