import {SearchTableController} from "../common/searchtable/search-table-controller";
import {MdDialog, MdDialogRef} from "@angular/material";
import {ServiceGroupDetailsDialogComponent} from "./servicegroup-details-dialog/service-group-details-dialog.component";
import {Http} from "@angular/http";
import {AlertService} from "../alert/alert.service";
import {ServiceGroupExtensionDialogComponent} from "./servicegroup-extension-dialog/service-group-extension-dialog.component";
import {ServiceGroupMetadatalistDialogComponent} from "./servicegroup-metadatalist-dialog/service-group-metadatalist-dialog.component";

export class ServiceGroupController implements SearchTableController {

  constructor(public dialog: MdDialog) { }

  public showDetails(row: any) {
    let dialogRef: MdDialogRef<ServiceGroupDetailsDialogComponent> = this.dialog.open(ServiceGroupDetailsDialogComponent);
    dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public showExtension(row: any) {
    let dialogRef: MdDialogRef<ServiceGroupExtensionDialogComponent> = this.dialog.open(ServiceGroupExtensionDialogComponent);
    dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public showMetadataList(row: any) {
    let dialogRef: MdDialogRef<ServiceGroupMetadatalistDialogComponent> = this.dialog.open(ServiceGroupMetadatalistDialogComponent);
   // dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }


  public edit(row: any) { }

  public  delete(row: any) { }
}
