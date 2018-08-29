import {Component, OnInit} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'domain-details-dialog',
  templateUrl: './domain-details-dialog.component.html',
  styleUrls: ['./domain-details-dialog.component.css']
})
export class DomainDetailsDialogComponent implements OnInit {

  domain;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<DomainDetailsDialogComponent>) {
  }

  ngOnInit() {
  }

}
