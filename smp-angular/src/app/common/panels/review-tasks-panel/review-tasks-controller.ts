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
        title: "Review tasks details",
        object: row,
      }
    });
  }

  public edit(row: any): MatDialogRef<any> {
    return this.dialog.open(ObjectPropertiesDialogComponent, {
      data: {
        title: "Update Review tasks",
        object: row,
      }
    });
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
