import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {HttpClient} from "@angular/common/http";

export class AlertController implements SearchTableController {

  constructor(protected http: HttpClient, protected lookups: GlobalLookups, public dialog: MatDialog) {
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

  public showDetails(row: any) {
  }

  public edit(row: any) {
  }

  public delete(row: any) {
  }

  newDialog(config?: MatDialogConfig): MatDialogRef<any> {
    return undefined;
  }
}
