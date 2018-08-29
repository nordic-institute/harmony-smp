import {SearchTableController} from "../common/searchtable/searchtable-controller";
import {MdDialog, MdDialogRef} from "@angular/material";
import {ServicegroupDetailsDialogComponent} from "./servicegroup-details-dialog/servicegroup-details-dialog.component";
import {Http} from "@angular/http";
import {AlertService} from "../alert/alert.service";
import {ServiceGroupExtensionDialogComponent} from "./servicegroup-extension-dialog/servicegroup-extension-dialog.component";
import {ServicegroupMetadatalistDialogComponent} from "./servicegroup-metadatalist-dialog/servicegroup-metadatalist-dialog.component";

export class ServiceGroupController extends SearchTableController {

  constructor(public dialog: MdDialog) {
    super();
  }


  public showDetails(row: any) {
    let dialogRef: MdDialogRef<ServicegroupDetailsDialogComponent> = this.dialog.open(ServicegroupDetailsDialogComponent);
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
    let dialogRef: MdDialogRef<ServicegroupMetadatalistDialogComponent> = this.dialog.open(ServicegroupMetadatalistDialogComponent);
   // dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });

  }


  public edit(row: any) {

  }

  public  delete(row: any) {

  }
}
