import {SearchTableController} from '../../search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {GlobalLookups} from "../../global-lookups";
import {SearchTableEntity} from "../../search-table/search-table-entity.model";
import {ObjectPropertiesDialogComponent} from "../../dialogs/object-properties-dialog/object-properties-dialog.component";

export class AlertController implements SearchTableController {

  constructor(protected lookups: GlobalLookups,
              public dialog: MatDialog) {
  }

  validateDeleteOperation(rows: SearchTableEntity[]) {
    return null;
  }

  newRow(): SearchTableEntity {
    return null;
  }

  dataSaved() {

  }

  isRecordChanged(oldModel: any, newModel: any): boolean {
    return false;
  }

  isRowExpanderDisabled(row: SearchTableEntity): boolean {
    return true;
  }

  public showDetails(row: any): MatDialogRef<any> {
    return this.dialog.open(ObjectPropertiesDialogComponent, {
      data: {
        i18n: "alert.panel.dialog.title.alert.details",
        object: [{
          i18n: "alert.panel.label.column.alert.date",
          value: row.row?.reportingTime,
          type: "dateTime"
        }, {
          i18n: "alert.panel.label.column.alert.level",
          value: row.row?.alertLevel
        }, {
          i18n: "alert.panel.label.column.for.user",
          value: row.row?.username
        }, {
          i18n: "alert.panel.label.column.credential.type",
          value: row.row?.alertDetails['CREDENTIAL_TYPE']
        }, {
          i18n: "alert.panel.label.column.alert.type",
          value: row.row?.alertType,
        }, {
          i18n: "alert.panel.label.column.alert.status",
          value: row.row?.alertStatus,
        }, {
          i18n: "alert.panel.label.column.status.description",
          value: row.row?.alertStatusDesc,
        }]
      }
    });
  }

  public edit(row: any): MatDialogRef<any> {
    // not actually editing the row
    return this.showDetails(row);
  }

  public delete(row: any) {
  }

  newDialog(config): MatDialogRef<any> {
    if (config && config.data && config.data.edit) {
      return this.edit(config.data);
    } else {
      return this.showDetails(config.data);
    }
  }
}
