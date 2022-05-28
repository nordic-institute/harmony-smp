import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'object-properties-dialog',
  templateUrl: './object-properties-dialog.component.html',
  styleUrls: ['./object-properties-dialog.component.css']
})
export class ObjectPropertiesDialogComponent {

  title: string="Object properties";
  object:Object
  displayedColumns: string[] = ['key', 'value'];
  dataSource : object[];

  constructor(public dialogRef: MatDialogRef<ObjectPropertiesDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    //this.title=data.title;
    this.object=data.row.alertDetails;
    this.dataSource = Object.keys(this.object).map((key) => [key, this.object[key]]);



  }
}
