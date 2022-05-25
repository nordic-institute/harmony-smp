import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-information-dialog',
  templateUrl: './information-dialog.component.html',
  styleUrls: ['./information-dialog.component.css']
})
export class InformationDialogComponent {

  title: string;
  description: string

  constructor(public dialogRef: MatDialogRef<InformationDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    this.title=data.title;
    this.description=data.description;
  }
}
