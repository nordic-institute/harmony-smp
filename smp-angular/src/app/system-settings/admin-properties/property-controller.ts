import {SearchTableController} from '../../common/search-table/search-table-controller';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {GlobalLookups} from "../../common/global-lookups";
import {HttpClient} from "@angular/common/http";
import {PropertyRo} from "./property-ro.model";
import {Injectable} from "@angular/core";
import {
  PropertyDetailsDialogComponent
} from "../../common/dialogs/property-details-dialog/property-details-dialog.component";

@Injectable()
export class PropertyController implements SearchTableController<PropertyRo> {

  constructor(protected http: HttpClient, protected lookups: GlobalLookups, public dialog: MatDialog) {
  }

  validateDeleteOperation(rows: PropertyRo[]) {
    return null;
  }

  newRow(): PropertyRo {
    return null;
  }

  dataSaved() {
    this.lookups.refreshApplicationConfiguration();
  }

  isRecordChanged(oldEntity: PropertyRo, newEntity: PropertyRo): boolean {
    let isEqual = this.isEqual(oldEntity.value, newEntity.value);
    if (!isEqual) {
      return true; // Property has changed
    }
  }

  isRowExpanderDisabled(row: PropertyRo): boolean {
    return true;
  }

  public showDetails(row: PropertyRo): MatDialogRef<any> {
    return this.dialog.open(PropertyDetailsDialogComponent);
  }

  public edit(data: any): MatDialogRef<any> {
    return this.dialog.open(PropertyDetailsDialogComponent, data);
  }

  public delete(row: PropertyRo) {
  }

  newDialog(config): MatDialogRef<any> {
    if (config && config.data && config.data.edit) {
      return this.edit(config);
    } else {
      return this.showDetails(config);
    }
  }

  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }
}
