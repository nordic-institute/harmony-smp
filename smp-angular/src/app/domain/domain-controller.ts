import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {DomainDetailsDialogComponent} from './domain-details-dialog/domain-details-dialog.component';
import {DomainRo} from './domain-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';
import {UserDetailsDialogComponent} from "../user/user-details-dialog/user-details-dialog.component";

export class DomainController implements SearchTableController {

  constructor(public dialog: MatDialog) {
  }

  public showDetails(row: any) {
    let dialogRef: MatDialogRef<DomainDetailsDialogComponent> = this.dialog.open(DomainDetailsDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public edit(row: any) {
  }

  public delete(row: any) {
  }

  public newDialog(config?: MatDialogConfig): MatDialogRef<DomainDetailsDialogComponent> {
    return this.dialog.open(DomainDetailsDialogComponent, config);
  }

  public newRow(): DomainRo {
    return {
      domainCode: '',
      smlSubdomain: '',
      smlSmpId: '',
      smlParticipantIdentifierRegExp: '',
      smlClientCertHeader: '',
      smlClientKeyAlias: '',
      signatureKeyAlias: '',
      status: SearchTableEntityStatus.NEW
    }
  }
}
