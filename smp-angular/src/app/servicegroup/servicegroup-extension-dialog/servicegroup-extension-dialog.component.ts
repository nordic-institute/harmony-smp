import {Component, OnInit} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'servicegroup-extension-dialog',
  templateUrl: './servicegroup-extension-dialog.component.html',
  styleUrls: ['./servicegroup-extension-dialog.component.css']
})
export class ServiceGroupExtensionDialogComponent implements OnInit {

  servicegroup;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<ServiceGroupExtensionDialogComponent>) {
  }

  ngOnInit() {
  }

}
