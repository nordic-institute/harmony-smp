import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {ServiceGroupSearchRo} from './service-group-search-ro.model';
import {of} from "rxjs/internal/observable/of";
import {SearchTableValidationResult} from "../common/search-table/search-table-validation-result.model";
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {ServiceGroupEditRo} from "../service-group-edit/service-group-edit-ro.model";

export class ServiceGroupSearchController implements SearchTableController {

  constructor(public dialog: MatDialog) { }

  public showDetails(row: any) {
  }

  public showExtension(row: any) {
  }

  public edit(row: any) { }

  public delete(row: any) { }

  public newDialog(config?: MatDialogConfig) {
    return null;
  }

  public newRow(): ServiceGroupSearchRo {
    return null;
  }

  public dataSaved() {}

  validateDeleteOperation(rows: Array<SearchTableEntity>){
    return of( this.newValidationResult(true) );
  }

  public newValidationResult(result: boolean, message?: string): SearchTableValidationResult {
    return {
      validOperation: null,
      stringMessage: message,
    }
  }

  isRowExpanderDisabled(row: SearchTableEntity): boolean {
    const serviceGroup = <ServiceGroupSearchRo>row;
    return !(serviceGroup.serviceMetadata && serviceGroup.serviceMetadata.length);
  }
}
