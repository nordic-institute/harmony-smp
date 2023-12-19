import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'object-properties-dialog',
  templateUrl: './object-properties-dialog.component.html',
  styleUrls: ['./object-properties-dialog.component.css']
})
export class ObjectPropertiesDialogComponent {

  title: string = "Object properties";
  object:Object
  displayedColumns: string[] = ['key', 'value'];
  dataSource : object[];

  constructor(public dialogRef: MatDialogRef<ObjectPropertiesDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    this.title = data.title;
    this.object = {...data.object.row.alertDetails,
          statusDescription: data.object.row.alertStatusDesc};
    this.dataSource = Object.keys(this.object)
      .map((key) => [ this.toTitleCase(key), this.object[key] ])
      .sort();
  }

  private toTitleCase(input: string): string {
    return input
      .replace(/([A-Z]+)/g, " $1")     // camelCase -> Title Case (part #1)
      .replace(/([A-Z][a-z])/g, " $1")  // camelCase -> Title Case (part #2)
      .replaceAll('_', '');
  }
}
