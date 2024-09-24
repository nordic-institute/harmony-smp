import {
  SearchTableController
} from '../../search-table/search-table-controller';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {GlobalLookups} from "../../global-lookups";
import {SearchTableEntity} from "../../search-table/search-table-entity.model";
import {
  ObjectPropertiesDialogComponent
} from "../../dialogs/object-properties-dialog/object-properties-dialog.component";

export class ReviewTasksController implements SearchTableController {

  constructor(protected lookups: GlobalLookups, public dialog: MatDialog) {
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
        i18n: "review.edit.dialog.title",
        object: [{
          i18n: "review.edit.panel.label.column.review.date",
          value: row.row?.lastUpdatedOn,
          type: "dateTime"
        }, {
          i18n: "review.edit.panel.label.column.target",
          value: row.row?.target,
        }, {
          i18n: "review.edit.panel.label.column.version",
          value: row.row?.version,
        }, {
          i18n: "review.edit.panel.label.column.resource.scheme",
          value: row.row?.resourceIdentifierScheme,
        }, {
          i18n: "review.edit.panel.label.column.resource.value",
          value: row.row?.resourceIdentifierValue,
        }, {
          i18n: "review.edit.panel.label.column.subresource.scheme",
          value: row.row?.subresourceIdentifierScheme,
        }, {
          i18n: "review.edit.panel.label.column.subresource.value",
          value: row.row?.subresourceIdentifierValue,
        }]
      }
    });
  }

  public edit(row: any): MatDialogRef<any> {
    return null;
  }

  public delete(row: any) {
  }

  newDialog(config): MatDialogRef<any> {
    return null;
  }
}
