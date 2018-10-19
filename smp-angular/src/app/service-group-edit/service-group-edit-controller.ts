import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {ServiceGroupDetailsDialogComponent} from './service-group-details-dialog/service-group-details-dialog.component';
import {ServiceGroupEditRo} from './service-group-edit-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';
import {ServiceMetadataEditRo} from "./service-metadata-edit-ro.model";
import {DomainDetailsDialogComponent} from "../domain/domain-details-dialog/domain-details-dialog.component";
import {ServiceGroupMetadataDialogComponent} from "./service-group-metadata-dialog/service-group-metadata-dialog.component";

export class ServiceGroupEditController implements SearchTableController {

  constructor(public dialog: MatDialog) {
  }

  public showDetails(row: any, config?: MatDialogConfig,) {
    let dialogRef: MatDialogRef<ServiceGroupDetailsDialogComponent>
      = this.dialog.open(ServiceGroupDetailsDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }


  public edit(row: any) {
  }

  public delete(row: any) {

    // set all rows as deleted
    let sgRow =  row as ServiceGroupEditRo;
    sgRow.serviceMetadata.forEach(function(part, index, metaDataList) {
      metaDataList[index].status = SearchTableEntityStatus.REMOVED;
      metaDataList[index].deleted=true;
    });

  }

  public newDialog(config?: MatDialogConfig): MatDialogRef<ServiceGroupDetailsDialogComponent> {
    return this.dialog.open(ServiceGroupDetailsDialogComponent, config);
  }

  public newMetadataDialog(config?: MatDialogConfig): MatDialogRef<ServiceGroupMetadataDialogComponent> {
    return this.dialog.open(ServiceGroupMetadataDialogComponent, config);
  }

  public newRow(): ServiceGroupEditRo {
    return {
      id: null,
      index: null,
      participantIdentifier: '',
      participantScheme: '',
      domainCode: '',
      smlSubdomain: '',
      serviceMetadata: [],
      users: [],
      domains: [],
      status: SearchTableEntityStatus.NEW
    };
  }

  public newServiceMetadataRow(): ServiceMetadataEditRo {
    return {
      id:null,
      documentIdentifier: '',
      documentIdentifierScheme: '',
      smlSubdomain: '',
      domainCode: '',
      processSchema: '',
      processIdentifier: '',
      endpointUrl: '',
      endpointCertificate: '',
      status: SearchTableEntityStatus.NEW
    };
  }

}
