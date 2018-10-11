import {SearchTableController} from "../common/search-table/search-table-controller";
import {MdDialog, MdDialogConfig, MdDialogRef} from "@angular/material";
import {UserDetailsDialogComponent} from "./user-details-dialog/user-details-dialog.component";
import {UserRo} from "./user-ro.model";
import {SearchTableEntityStatus} from "../common/search-table/search-table-entity-status.model";

export class UserController implements SearchTableController {

  constructor(public dialog: MdDialog) { }

  public showDetails(row: any) {
    let dialogRef: MdDialogRef<UserDetailsDialogComponent> = this.dialog.open(UserDetailsDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public edit(row: any) { }

  public delete(row: any) { }

  public newDialog(config?: MdDialogConfig): MdDialogRef<UserDetailsDialogComponent> {
    return this.dialog.open(UserDetailsDialogComponent, config);
  }

  public newRow(): UserRo {
    return {
      userName: '',
      role: '',
      status: SearchTableEntityStatus.NEW
    }
  }
}
