import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {ServiceGroupDetailsDialogComponent} from './service-group-details-dialog/service-group-details-dialog.component';
import {ServiceGroupExtensionDialogComponent} from './service-group-extension-dialog/service-group-extension-dialog.component';
import {ServiceGroupEditRo} from './service-group-edit-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';
import {ServiceMetadataEditRo} from "./service-metadata-edit-ro.model";
import {DomainDetailsDialogComponent} from "../domain/domain-details-dialog/domain-details-dialog.component";

export class ServiceGroupEditController implements SearchTableController {

  constructor(public dialog: MatDialog) { }

  public showDetails( row: any, config?: MatDialogConfig,) {
    let dialogRef: MatDialogRef<ServiceGroupDetailsDialogComponent>
      = this.dialog.open(ServiceGroupDetailsDialogComponent);
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

  }


  public edit(row: any) { }

  public delete(row: any) { }

  public newDialog(config?: MatDialogConfig): MatDialogRef<ServiceGroupDetailsDialogComponent> {
    return this.dialog.open(ServiceGroupDetailsDialogComponent, config);
  }

  public newRow(): ServiceGroupEditRo {
    return {
      id: null,
      index: null,
      participantIdentifier:'',
      participantScheme: '',
      domainCode: '',
      smlSubdomain: '',
      serviceMetadata:[],
      users: [],
      status: SearchTableEntityStatus.NEW
    };
  }

}
