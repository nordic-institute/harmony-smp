import {SearchTableController} from '../../search-table/search-table-controller';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {GlobalLookups} from "../../global-lookups";
import {
  ObjectPropertiesDialogComponent
} from "../../dialogs/object-properties-dialog/object-properties-dialog.component";
import {AlertRo} from "./alert-ro.model";

export class AlertController implements SearchTableController<AlertRo> {

  constructor(protected lookups: GlobalLookups,
              public dialog: MatDialog) {
  }

  validateDeleteOperation(rows: AlertRo[]) {
    return null;
  }

  newRow(): AlertRo {
    return null;
  }

  dataSaved() {

  }

  isRecordChanged(oldModel: any, newModel: any): boolean {
    return false;
  }

  isRowExpanderDisabled(row: AlertRo): boolean {
    return true;
  }

  public showDetails(row: AlertRo): MatDialogRef<any> {
    return this.dialog.open(ObjectPropertiesDialogComponent, {
      data: {
        i18n: "alert.panel.dialog.title.alert.details",
        object: [{
          i18n: "alert.panel.label.column.alert.date",
          value: row?.reportingTime,
          type: "dateTime"
        }, {
          i18n: "alert.panel.label.column.alert.level",
          value: row?.alertLevel
        }, {
          i18n: "alert.panel.label.column.for.user",
          value: row?.username
        }, {
          i18n: "alert.panel.label.column.credential.type",
          value: row?.alertDetails['CREDENTIAL_TYPE']
        }, {
          i18n: "alert.panel.label.column.alert.type",
          value: row?.alertType,
        }, {
          i18n: "alert.panel.label.column.alert.status",
          value: row?.alertStatus,
        }, {
          i18n: "alert.panel.label.column.status.description",
          value: row?.alertStatusDesc,
        }]
      }
    });
  }

  public edit(row: AlertRo): MatDialogRef<any> {
    // not actually editing the row
    return this.showDetails(row);
  }

  public delete(row: AlertRo) {
  }

  newDialog(config): MatDialogRef<any> {
    if (config && config.data && config.data.edit) {
      return this.edit(config.data);
    } else {
      return this.showDetails(config.data);
    }
  }
}
