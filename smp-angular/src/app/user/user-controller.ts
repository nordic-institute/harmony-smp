import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material';
import {UserDetailsDialogComponent} from './user-details-dialog/user-details-dialog.component';
import {UserRo} from './user-ro.model';
import {SearchTableEntityStatus} from '../common/search-table/search-table-entity-status.model';
import {GlobalLookups} from "../common/global-lookups";
import {CertificateRo} from "./certificate-ro.model";
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";
import {of} from "rxjs/internal/observable/of";
import {SearchTableValidationResult} from "../common/search-table/search-table-validation-result.model";
import {ServiceMetadataValidationEditRo} from "../service-group-edit/service-group-metadata-dialog/service-metadata-validation-edit-ro.model";
import {SmpConstants} from "../smp.constants";
import {HttpClient} from "@angular/common/http";

export class UserController implements SearchTableController {

  constructor(protected http: HttpClient, protected lookups: GlobalLookups, public dialog: MatDialog) { }

  public showDetails(row: any) {
    let dialogRef: MatDialogRef<UserDetailsDialogComponent> = this.dialog.open(UserDetailsDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }

  public edit(row: any) { }

  public delete(row: any) {

  }

  public newDialog(config?: MatDialogConfig): MatDialogRef<UserDetailsDialogComponent> {
    return this.dialog.open(UserDetailsDialogComponent, config);
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

  validateDeleteOperation(rows: Array<SearchTableEntity>){
    var deleteRowIds = rows.map(rows => rows.id);
    return  this.http.post<SearchTableValidationResult>(SmpConstants.REST_USER_VALIDATE_DELETE, deleteRowIds);
  }

  public newValidationResult(lst: Array<number>): SearchTableValidationResult {
    return {
      validOperation: false,
      stringMessage: null,
      listId:lst,
    }
  }




}
