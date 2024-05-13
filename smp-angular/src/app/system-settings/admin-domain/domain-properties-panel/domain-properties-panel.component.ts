import {
  Component,
  EventEmitter,
  Input,
  OnDestroy, OnInit,
  Output,
} from '@angular/core';
import {DomainRo} from "../../../common/model/domain-ro.model";
import {AdminDomainService} from "../admin-domain.service";
import {MatDialogRef} from "@angular/material/dialog";
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {GlobalLookups} from "../../../common/global-lookups";
import {DomainPropertyRo} from "../../../common/model/domain-property-ro.model";
import {MatTableDataSource} from "@angular/material/table";
import {Subscription} from "rxjs";
import {PropertyController} from "../../admin-properties/property-controller";
import {EntityStatus} from "../../../common/enums/entity-status.enum";
import {PropertyTypeEnum} from "../../../common/enums/property-type.enum";
import {EditDomainService} from "../../../edit/edit-domain/edit-domain.service";

@Component({
  selector: 'domain-properties-panel',
  templateUrl: './domain-properties-panel.component.html',
  styleUrls: ['./domain-properties-panel.component.scss']
})
export class DomainPropertiesPanelComponent implements OnInit, OnDestroy, BeforeLeaveGuard {
  @Input() systemAdminService: boolean;
  @Output() onSavePropertiesDataEvent: EventEmitter<DomainRo> = new EventEmitter()
  displayedColumns: string[] = ['systemDefault', 'property', 'value'];
  _domain: DomainRo = null;
  selected?: DomainPropertyRo;
  dataChanged: boolean = false


  private domainPropertyUpdatedEventSub: Subscription = Subscription.EMPTY;

  propertyDataSource: MatTableDataSource<DomainPropertyRo> = new MatTableDataSource();

  constructor(private domainService: AdminDomainService,
              private editDomainService: EditDomainService,
              private propertyController: PropertyController,
              protected lookups: GlobalLookups) {

  }

  ngOnInit(): void {
    if (!this.systemAdminService) {
      this.domainPropertyUpdatedEventSub = this.editDomainService.onDomainPropertyUpdatedEvent()
        .subscribe((updateDomainList: DomainPropertyRo[]): void => {
            this.updateDomainPropertyList(updateDomainList);
          }
        );
    } else {
      this.domainPropertyUpdatedEventSub = this.domainService.onDomainPropertyUpdatedEvent()
        .subscribe((updateDomainList: DomainPropertyRo[]): void => {
            this.updateDomainPropertyList(updateDomainList);
          }
        );
    }
    this.refresh();
  }

  ngOnDestroy(): void {
    this.domainPropertyUpdatedEventSub.unsubscribe();
  }

  @Input() set domain(value: DomainRo) {
    this._domain = value
    this.refresh();
  }
  refresh(): void {
    if (!!this._domain) {
      if (!this.systemAdminService) {
        this.editDomainService.getDomainProperties(this._domain);

      } else {
        this.domainService.getDomainProperties(this._domain);
      }
    } else {
      // clear the table
      this.updateDomainPropertyList([]);
    }
    // update domain properties
  }

  get domain(): DomainRo {
    return {...this._domain};
  }

  get submitButtonEnabled(): boolean {
    return this.dataChanged;
  }

  get resetButtonEnabled(): boolean {
    return this.dataChanged;
  }

  public onSaveButtonClicked() {
     let changedProperties: DomainPropertyRo[] = this.propertyDataSource.data.filter(element => {
      return element.status == EntityStatus.UPDATED;
    })
    if (!this.systemAdminService) {
      this.editDomainService.updateDomainProperties(this._domain, changedProperties);
    } else {
      this.domainService.updateDomainProperties(this._domain, changedProperties);
    }
  }

  /*
    * reset/reload properties from the server
   */
  public onResetButtonClicked(): void {

    if (!this._domain) {
      this.updateDomainPropertyList([]);
      return;
    }
    if (!this.systemAdminService) {
      this.editDomainService.getDomainProperties(this._domain);
    } else {
      this.domainService.getDomainProperties(this._domain);
    }
  }

  /**
   * return the value to be displayed in the table row. If the row is updated and not system default,
   * then display the new value else display the old value
   * @param row
   */
  getTableRowValue(row: DomainPropertyRo){
    return  row.systemDefault ? row.systemDefaultValue  : row.value;
  }

  isDirty(): boolean {
    return this.dataChanged;
  }

  propertySelected(property: DomainPropertyRo) {
    this.selected = property;
  }

  editSelectedRow():void {
    const dialogRef: MatDialogRef<any> =  this.propertyController.edit({
      data: {
        edit: this.selected?.status != EntityStatus.NEW,
        propertyType: PropertyTypeEnum.DOMAIN,
        row: this.selected
      }
    })


    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        let changedProperty:DomainPropertyRo =  dialogRef.componentInstance.getCurrent();
        for(let i=0;i<this.propertyDataSource.data.length ;i++){
          let prop = this.propertyDataSource.data[i];
          if (changedProperty.property === prop.property) {
            this.propertyDataSource.data[i] = changedProperty;
            this.propertyDataSource.data = [...this.propertyDataSource.data];
            this.dataChanged = true;
            break;
          }
        }
      }
    });
  }

  updateDomainPropertyList(updateDomainList: DomainPropertyRo[]): void {
    this.propertyDataSource.data = [... updateDomainList]
    this.dataChanged = false;
  }

  getRowClass(row, oddRow: boolean) {
    return {
      'datatable-row-selected': row===this.selected,
      'table-row-new': (row.status === EntityStatus.NEW),
      'table-row-updated': (row.status === EntityStatus.UPDATED),
      'deleted': (row.status === EntityStatus.REMOVED),
      'datatable-row-odd': oddRow
    };
  }
}
