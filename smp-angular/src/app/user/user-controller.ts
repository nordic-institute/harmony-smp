import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {UserDetailsDialogComponent} from './user-details-dialog/user-details-dialog.component';
import {UserRo} from './user-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';

export class UserController implements SearchTableController {

  constructor(public dialog: MatDialog) { }

  public showDetails(row: any) {
    let dialogRef: MatDialogRef<UserDetailsDialogComponent> = this.dialog.open(UserDetailsDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public edit(row: any) { }

  public delete(row: any) { }

  public newDialog(config?: MatDialogConfig): MatDialogRef<UserDetailsDialogComponent> {
    return this.dialog.open(UserDetailsDialogComponent, config);
  }

  public newRow(): UserRo {
    return {
      userName: '',
      email: '',
      role: '',
      status: SearchTableEntityStatus.NEW
    }
  }
}
