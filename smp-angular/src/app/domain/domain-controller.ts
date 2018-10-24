import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {DomainDetailsDialogComponent} from './domain-details-dialog/domain-details-dialog.component';
import {DomainRo} from './domain-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';
import {GlobalLookups} from "../common/global-lookups";
import {of} from "rxjs/internal/observable/of";
import {SearchTableValidationResult} from "../common/search-table/search-table-validation-result.model";
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {SmpConstants} from "../smp.constants";
import {HttpClient} from "@angular/common/http";

export class DomainController implements SearchTableController {

  constructor(protected http: HttpClient, protected lookups: GlobalLookups, public dialog: MatDialog) {
  }

  public showDetails( row: any) {

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
      index: null,
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
  public dataSaved() {
    this.lookups.refreshDomainLookup();
  }

  validateDeleteOperation(rows: Array<SearchTableEntity>){
    var deleteRowIds = rows.map(rows => rows.id);
    return  this.http.post<SearchTableValidationResult>(SmpConstants.REST_DOMAIN_VALIDATE_DELETE, deleteRowIds);
  }

  public newValidationResult(result: boolean, message: string): SearchTableValidationResult {
    return {
      validOperation: result,
      stringMessage: '',
    }
  }
}
