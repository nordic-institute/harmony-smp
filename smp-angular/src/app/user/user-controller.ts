import {SearchTableController} from "../common/searchtable/searchtable-controller";
import {MdDialog, MdDialogRef} from "@angular/material";
import {UserDetailsDialogComponent} from "./user-details-dialog/user-details-dialog.component";

export class UserController implements SearchTableController {

  constructor(public dialog: MdDialog) { }

  public showDetails(row: any) {
    let dialogRef: MdDialogRef<UserDetailsDialogComponent> = this.dialog.open(UserDetailsDialogComponent);
    dialogRef.componentInstance.user = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public edit(row: any) { }

  public  delete(row: any) { }
}
