import {Component, OnInit} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'app-messagelog-details',
  templateUrl: './servicegroup-details-dialog.component.html',
  styleUrls: ['./servicegroup-details-dialog.component.css']
})
export class ServicegroupDetailsDialogComponent implements OnInit {

  servicegroup;
  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';

  constructor(public dialogRef: MdDialogRef<ServicegroupDetailsDialogComponent>) {
  }

  ngOnInit() {
  }

}
