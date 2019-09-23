import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {UserDetailsDialogComponent, UserDetailsDialogMode} from './user-details-dialog/user-details-dialog.component';
import {UserRo} from './user-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {SearchTableValidationResult} from "../common/search-table/search-table-validation-result.model";
import {SmpConstants} from "../smp.constants";
import {HttpClient} from "@angular/common/http";
import {CertificateRo} from "./certificate-ro.model";

export class UserController implements SearchTableController {

  nullCert:CertificateRo;


  compareUserProperties = ["username","password","emailAddress","active","role","certificate"];
  compareCertProperties = ["certificateId","subject","issuer","serialNumber","crlUrl","validFrom","validTo"];


  constructor(protected http: HttpClient, protected lookups: GlobalLookups, public dialog: MatDialog) {
    this.nullCert = this.newCertificateRo();
  }

  public showDetails(row: any) {
    let dialogRef: MatDialogRef<UserDetailsDialogComponent> = this.dialog.open(UserDetailsDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public edit(row: any) {
  }

  public delete(row: any) {
  }

  public newDialog(config?: MatDialogConfig): MatDialogRef<UserDetailsDialogComponent> {
    return this.dialog.open(UserDetailsDialogComponent, this.convertWithMode(config));
  }

  private convertWithMode(config) {
    return (config && config.data)
      ? {
        ...config,
        data: {
          ...config.data,
          mode: config.data.mode || (config.data.edit ? UserDetailsDialogMode.EDIT_MODE : UserDetailsDialogMode.NEW_MODE)
        }
      }
      : config;
  }

  public newRow(): UserRo {
    return {
      id: null,
      index: null,
      username: '',
      emailAddress: '',
      role: '',
      active: true,
      status: SearchTableEntityStatus.NEW,
      statusPassword: SearchTableEntityStatus.NEW
    }
  }

  public dataSaved() {
    this.lookups.refreshUserLookup();
  }

  validateDeleteOperation(rows: Array<SearchTableEntity>) {
    var deleteRowIds = rows.map(rows => rows.id);
    return this.http.post<SearchTableValidationResult>(SmpConstants.REST_USER_VALIDATE_DELETE, deleteRowIds);
  }

  public newValidationResult(lst: Array<number>): SearchTableValidationResult {
    return {
      validOperation: false,
      stringMessage: null,
      listId: lst,
    }
  }

  isRowExpanderDisabled(row: SearchTableEntity): boolean {
    return false;
  }

  isCertificateChanged(oldCert, newCert): boolean {
    if (this.isNull(oldCert) && this.isNull(newCert)) {
      console.log("both null return false! ");
      return false;
    }

    if (this.isNull(oldCert)) {
      oldCert = this.nullCert;
    }

    if (this.isNull(newCert)) {
      newCert = this.nullCert;
    }

    return this.propertyChanged(oldCert, newCert, this.compareCertProperties);
  }

  isRecordChanged(oldModel, newModel): boolean {
    return this.propertyChanged(oldModel, newModel, this.compareUserProperties);
  }

  propertyChanged(oldModel, newModel, arrayProperties): boolean {


    let propSize = arrayProperties.length;
    for (let i = 0; i < propSize; i++) {

      let property = arrayProperties[i];
     if (property === 'certificate') {
        if (this.isCertificateChanged(oldModel[property], newModel[property])) {
          return true; // Property has changed
        }
      } else {
        const isEqual = this.isEqual(newModel[property], oldModel[property]);
        if (!isEqual) {
          console.log("property "+property+" is changed! ");
          return true; // Property has changed
        }
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

  isNull(obj): boolean {
    return !obj
  }


  private newCertificateRo(): CertificateRo {
    return {
      subject: '',
      validFrom: null,
      validTo: null,
      issuer: '',
      serialNumber: '',
      certificateId: '',
      fingerprints: '',
    };
  }


}
