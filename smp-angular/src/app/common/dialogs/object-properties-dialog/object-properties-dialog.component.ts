import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {TranslateService} from "@ngx-translate/core";
import {DateTimeService} from "../../services/date-time.service";

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
              private dateTimeService: DateTimeService) {
    this.translateService.get(data.i18n).subscribe(title => this.title = title);
    this.dataSource = data.object.map(row => [row.i18n, this.parseValue(row)]);
  }

  private parseValue(row) {
    if (row.type === "dateTime") {
      return this.dateTimeService.formatDateTimeForUserLocal(row.value);
    }
    return row.value;
  }
}
