import {SearchTableController} from "../common/searchtable/searchtable-controller";
import {MdDialog, MdDialogRef} from "@angular/material";
import {DomainDetailsDialogComponent} from "./domain-details-dialog/domain-details-dialog.component";

export class DomainController implements SearchTableController {

  constructor(public dialog: MdDialog) { }

  public showDetails(row: any) {
    let dialogRef: MdDialogRef<DomainDetailsDialogComponent> = this.dialog.open(DomainDetailsDialogComponent);
    dialogRef.componentInstance.domain = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public edit(row: any) { }

  public  delete(row: any) { }
}
