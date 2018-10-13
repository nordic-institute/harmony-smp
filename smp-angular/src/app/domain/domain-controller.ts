import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {DomainDetailsDialogComponent} from './domain-details-dialog/domain-details-dialog.component';
import {DomainRo} from './domain-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';

export class DomainController implements SearchTableController {

  constructor(public dialog: MatDialog) { }

  public showDetails(row: any) {
    let dialogRef: MatDialogRef<DomainDetailsDialogComponent> = this.dialog.open(DomainDetailsDialogComponent);
    dialogRef.componentInstance.domain = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public edit(row: any) { }

  public  delete(row: any) { }

  public newDialog(config?: MatDialogConfig): MatDialogRef<DomainDetailsDialogComponent> {
    return this.dialog.open(DomainDetailsDialogComponent, config);
  }

  public newRow(): DomainRo {
    return {
      domainId: '',
      bdmslClientCertHeader: '',
      bdmslClientCertAlias: '',
      bdmslSmpId: '',
      signatureCertAlias: '',
      status: SearchTableEntityStatus.NEW
    }
  }
}