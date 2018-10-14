import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';

import {UserDetailsDialogComponent} from '../user/user-details-dialog/user-details-dialog.component';
import {SearchTableEntity} from '../common/search-table/search-table-entity.model';
import {ServiceGroupRo} from './service-group-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';

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

  public newRow(): ServiceGroupRo {
    return null;
  }

}
