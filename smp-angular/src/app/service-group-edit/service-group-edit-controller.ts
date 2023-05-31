import {SearchTableController} from '../common/search-table/search-table-controller';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {ServiceGroupDetailsDialogComponent} from './service-group-details-dialog/service-group-details-dialog.component';
import {ServiceGroupEditRo} from './service-group-edit-ro.model';
import {EntityStatus} from '../common/enums/entity-status.enum';
import {ServiceMetadataEditRo} from "./service-metadata-edit-ro.model";
import {ServiceGroupMetadataDialogComponent} from "./service-group-metadata-dialog/service-group-metadata-dialog.component";
import {of} from "rxjs/internal/observable/of";
import {SearchTableValidationResult} from "../common/search-table/search-table-validation-result.model";
import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export class ServiceGroupEditController implements SearchTableController {

  compareUpdateSGProperties = ["extension", "users", "serviceGroupDomains"];
  compareNewSGProperties = ["participantScheme", "participantIdentifier", "", "extension", "users", "serviceGroupDomains"];

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
    let sgRow = row as ServiceGroupEditRo;
    sgRow.serviceMetadata.forEach(function (part, index, metaDataList) {
      metaDataList[index].status = EntityStatus.REMOVED;
      metaDataList[index].deleted = true;
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
      serviceMetadata: [],
      users: [],
      serviceGroupDomains: [],
      extensionStatus: EntityStatus.NEW,
      status: EntityStatus.NEW
    };
  }

  public newServiceMetadataRow(): ServiceMetadataEditRo {
    return {
      id: null,
      documentIdentifier: '',
      documentIdentifierScheme: '',
      smlSubdomain: '',
      domainCode: '',
      domainId: null,
      status: EntityStatus.NEW,
      xmlContentStatus: EntityStatus.NEW,
    };
  }

  public dataSaved() {
  }

  validateDeleteOperation(rows: Array<SearchTableEntity>) {
    return of(this.newValidationResult(true, ''));
  }

  public newValidationResult(result: boolean, message: string): SearchTableValidationResult {
    return {
      validOperation: result,
      stringMessage: message,
    }
  }

  isRowExpanderDisabled(row: ServiceGroupEditRo): boolean {
    const serviceGroup = <ServiceGroupEditRo>row;
    return !(serviceGroup.serviceMetadata && serviceGroup.serviceMetadata.length);
  }

  isRecordChanged(oldModel, newModel): boolean {
    // different set of properties to compare in case if new entry is reedited or already saved entry is reedited.
    let propsToCompare = newModel.status === EntityStatus.NEW ?
      this.compareNewSGProperties : this.compareUpdateSGProperties;

    // check if other properties were changed
    let propSize = propsToCompare.length;
    for (let i = 0; i < propSize; i++) {
      let property = propsToCompare[i];
      let isEqual = false;

      if (property === 'users') {
        isEqual = this.isEqualListByAttribute(newModel[property], oldModel[property], "userId");
      } else if (property === 'serviceGroupDomains') {
        isEqual = this.isEqualListByAttribute(newModel[property], oldModel[property], "domainCode");
      } else {
        isEqual = this.isEqual(JSON.stringify(newModel[property]), JSON.stringify(oldModel[property]));
      }
      console.log("Property: " + property + " new: " + JSON.stringify(newModel[property]) + "old: " + JSON.stringify(oldModel[property]) + " val: " + isEqual);
      if (!isEqual) {
        return true; // Property has changed
      }
    }
    return false;
  }

  isEqualListByAttribute(array1, array2, compareByAttribute): boolean {
    let result1 = array1.filter(function (o1) {
      // filter out (!) items in result2
      return !array2.some(function (o2) {
        return o1[compareByAttribute] === o2[compareByAttribute]; //  unique id
      });
    });

    let result2 = array2.filter(function (o1) {
      // filter out (!) items in result2
      return !array1.some(function (o2) {
        return o1[compareByAttribute] === o2[compareByAttribute]; //  unique id
      });
    });

    return (!result1 || result1.length === 0) && (!result2 || result2.length === 0);
  }

  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }
}
