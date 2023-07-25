import {SearchTableController} from '../../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {GlobalLookups} from "../../common/global-lookups";
import {SearchTableEntity} from "../../common/search-table/search-table-entity.model";
import {HttpClient} from "@angular/common/http";
import {DomainDetailsDialogComponent} from "../domain/domain-details-dialog/domain-details-dialog.component";
import {PropertyDetailsDialogComponent} from "./property-details-dialog/property-details-dialog.component";
import {PropertyRo} from "./property-ro.model";

export class PropertyController implements SearchTableController {

  constructor(protected http: HttpClient, protected lookups: GlobalLookups, public dialog: MatDialog) {
  }

  validateDeleteOperation(rows: SearchTableEntity[]) {
    return null;
  }

  newRow(): SearchTableEntity {
    return null;
  }

  dataSaved() {
    this.lookups.refreshApplicationConfiguration();
  }

  isRecordChanged(oldEntity: PropertyRo, newEntity: PropertyRo): boolean {
      let isEqual = this.isEqual(oldEntity.value,newEntity.value);
      if (!isEqual) {
        return true; // Property has changed
      }
  }

  isRowExpanderDisabled(row: SearchTableEntity): boolean {
    return true;
  }

  public showDetails(row: any) {
    let dialogRef: MatDialogRef<PropertyDetailsDialogComponent> = this.dialog.open(PropertyDetailsDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      console.log("Property dialog is closed!");
    });
  }

  public edit(row: any) {
  }

  public delete(row: any) {
  }

  newDialog(config?: MatDialogConfig): MatDialogRef<any> {
    return this.dialog.open(PropertyDetailsDialogComponent, config);
  }

  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }
}
