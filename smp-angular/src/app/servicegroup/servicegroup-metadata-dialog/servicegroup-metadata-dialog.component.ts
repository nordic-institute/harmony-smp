import {Component, OnInit} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'app-messagelog-dialog',
  templateUrl: './servicegroup-metadata-dialog.component.html',
  styleUrls: ['./servicegroup-metadata-dialog.component.css']
})
export class ServicegroupMetadataDialogComponent implements OnInit {

  constructor(public dialogRef: MdDialogRef<ServicegroupMetadataDialogComponent>) {
  }

  ngOnInit() {
  }

}
