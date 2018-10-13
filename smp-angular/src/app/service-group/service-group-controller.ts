import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {ServiceGroupDetailsDialogComponent} from './service-group-details-dialog/service-group-details-dialog.component';
import {AlertService} from '../alert/alert.service';
import {ServiceGroupExtensionDialogComponent} from './service-group-extension-dialog/service-group-extension-dialog.component';
import {ServiceGroupMetadataListDialogComponent} from './service-group-metadata-list-dialog/service-group-metadata-list-dialog.component';
import {UserDetailsDialogComponent} from '../user/user-details-dialog/user-details-dialog.component';
import {SearchTableEntity} from '../common/search-table/search-table-entity.model';
import {ServiceGroupRo} from './service-group-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';

export class ServiceGroupController implements SearchTableController {

  constructor(public dialog: MatDialog) { }

  public showDetails(row: any) {
    let dialogRef: MatDialogRef<ServiceGroupDetailsDialogComponent> = this.newDialog();
    dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public showExtension(row: any) {
    let dialogRef: MatDialogRef<ServiceGroupExtensionDialogComponent> = this.dialog.open(ServiceGroupExtensionDialogComponent);
    dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public showMetadataList(row: any) {
    let dialogRef: MatDialogRef<ServiceGroupMetadataListDialogComponent> = this.dialog.open(ServiceGroupMetadataListDialogComponent);
   // dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }


  public edit(row: any) { }

  public delete(row: any) { }

  public newDialog(config?: MatDialogConfig): MatDialogRef<ServiceGroupDetailsDialogComponent> {
    return this.dialog.open(ServiceGroupDetailsDialogComponent, config);
  }

  public newRow(): ServiceGroupRo {
    return {
      domain: '',
      serviceGroupROId: {
        participantId: '',
        participantSchema: ''
      },
      status: SearchTableEntityStatus.NEW
    };
  }

}