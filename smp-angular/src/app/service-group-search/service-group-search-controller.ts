import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {ServiceGroupSearchRo} from './service-group-search-ro.model';

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
}
