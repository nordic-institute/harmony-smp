import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {TranslateService} from "@ngx-translate/core";
import {DatePipe} from "@angular/common";
import {GlobalLookups} from "../../global-lookups";

@Component({
  selector: 'object-properties-dialog',
  templateUrl: './object-properties-dialog.component.html',
  styleUrls: ['./object-properties-dialog.component.css']
})
export class ObjectPropertiesDialogComponent {

  title: string = "Object properties";
  displayedColumns: string[] = ['key', 'value'];
  dataSource: object[];

  constructor(public dialogRef: MatDialogRef<ObjectPropertiesDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private translateService: TranslateService,
              private datePipe: DatePipe,
              private lookups: GlobalLookups) {
    this.translateService.get(data.i18n).subscribe(title => this.title = title);
    this.dataSource = data.object.map(row => [row.i18n, this.parseValue(row)]);
  }

  private parseValue(row) {
    if (row.type === "dateTime") {
      let dateTimeFormat = this.lookups.getDateTimeFormat();
      return this.datePipe.transform(row.value, dateTimeFormat);
    }
    return row.value;
  }
}
