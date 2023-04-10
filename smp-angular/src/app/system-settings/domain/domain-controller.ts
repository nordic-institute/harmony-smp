import {SearchTableController} from '../../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {DomainDetailsDialogComponent} from './domain-details-dialog/domain-details-dialog.component';
import {DomainRo} from '../admin-domain/domain-ro.model';
import {EntityStatus} from '../../common/model/entity-status.model';
import {GlobalLookups} from "../../common/global-lookups";
import {SearchTableValidationResult} from "../../common/search-table/search-table-validation-result.model";
import {SearchTableEntity} from "../../common/search-table/search-table-entity.model";
import {SmpConstants} from "../../smp.constants";
import {HttpClient} from "@angular/common/http";

export class DomainController implements SearchTableController {

  constructor(protected http: HttpClient, protected lookups: GlobalLookups, public dialog: MatDialog) {
  }

  public showDetails( row: any) {

    let dialogRef: MatDialogRef<DomainDetailsDialogComponent> = this.dialog.open(DomainDetailsDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      console.log("Domain dialog is closed!");
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
      status: EntityStatus.NEW,
      smlRegistered: false,
      smlClientCertAuth: false,
    }
  }
  public dataSaved() {
    this.lookups.refreshDomainLookupForLoggedUser();
  }

  validateDeleteOperation(rows: Array<SearchTableEntity>){
    var deleteRowIds = rows.map(rows => rows.id);
    return  this.http.put<SearchTableValidationResult>(SmpConstants.REST_INTERNAL_DOMAIN_VALIDATE_DELETE, deleteRowIds);
  }

  public newValidationResult(result: boolean, message: string): SearchTableValidationResult {
    return {
      validOperation: result,
      stringMessage: '',
    }
  }

  isRowExpanderDisabled(row: SearchTableEntity): boolean {
    return false;
  }

  isRecordChanged(oldModel, newModel): boolean {
    for (let property in oldModel) {
      let isEqual = this.isEqual(newModel[property],oldModel[property]);
      console.log("Property: "+property+" new: " +newModel[property] +  "old: " +oldModel[property] + " val: " + isEqual  );
      if (!isEqual) {
        return true; // Property has changed
      }
    }
    return false;
  }

  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }
}
