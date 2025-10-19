import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {DomainRo} from "../../../common/model/domain-ro.model";
import {AdminDomainService} from "../admin-domain.service";
import {MatDialogRef} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {GlobalLookups} from "../../../common/global-lookups";
import {DomainPropertyRo} from "../../../common/model/domain-property-ro.model";
import {MatTableDataSource} from "@angular/material/table";
import {Subscription} from "rxjs";
import {PropertyController} from "../../admin-properties/property-controller";
import {EntityStatus} from "../../../common/enums/entity-status.enum";
import {PropertySourceEnum} from "../../../common/enums/property-source.enum";
import {EditDomainService} from "../../../edit/edit-domain/edit-domain.service";
import {SmpTableColDef} from "../../../common/components/smp-table/smp-table-coldef.model";

@Component({
  selector: 'domain-properties-panel',
  templateUrl: './domain-properties-panel.component.html',
  styleUrls: ['./domain-properties-panel.component.scss'],
  standalone: false
})
export class DomainPropertiesPanelComponent implements OnInit, AfterViewInit, OnDestroy, BeforeLeaveGuard {
  @ViewChild('systemDefaultColumn') systemDefaultColumn: TemplateRef<any>;
  @Input() systemAdminService: boolean;
  @Output() onSavePropertiesDataEvent: EventEmitter<DomainRo> = new EventEmitter()
  displayedColumns: string[] = [];
  columns: SmpTableColDef[];
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
  }

  ngAfterViewInit(): void {
    this.displayedColumns = ['systemDefault', 'property', 'value'];
    this.columns = [
      {
        columnDef: 'systemDefault',
        header: 'domain.properties.label.system.default',
        cellTemplate: this.systemDefaultColumn,
        cell: (row: DomainPropertyRo) => row.systemDefault,
        style: "max-width: 50px; width: 50px; display: flex; justify-content: center;"
      } as SmpTableColDef,
      {
        columnDef: 'property',
        header: 'domain.properties.label.domain.property',
        tooltip: (row: DomainPropertyRo) => row?.desc,
        cell: (row: DomainPropertyRo) => row.property,
        style: "max-width: 350px; width: 200px; display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'value',
        header: 'domain.properties.label.domain.value',
        cell: (row: DomainPropertyRo) => this.getTableRowValue(row),
        style: " display: flex; flex-grow: 1; justify-content: left;"
      }
    ];
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

  get saveButtonEnabled(): boolean {
    return this.dataChanged;
  }

  get cancelButtonEnabled(): boolean {
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
  getTableRowValue(row: DomainPropertyRo) {
    return row.systemDefault ? row.systemDefaultValue : row.value;
  }

  isDirty(): boolean {
    return this.dataChanged;
  }

  propertySelected(property: DomainPropertyRo) {
    this.selected = property;
  }

  editSelectedRow(): void {
    const dialogRef: MatDialogRef<any> = this.propertyController.edit({
      data: {
        edit: this.selected?.status != EntityStatus.NEW,
        propertyType: PropertySourceEnum.DOMAIN,
        row: this.selected
      }
    })

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        let changedProperty: DomainPropertyRo = dialogRef.componentInstance.getCurrent();
        for (let i = 0; i < this.propertyDataSource.data.length; i++) {
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

  applyPropertyFilter(filterValue: string) {
    this.propertyDataSource.filter = filterValue.trim().toLowerCase();
    if (this.propertyDataSource.paginator) {
      this.propertyDataSource.paginator.firstPage();
    }
  }

  updateDomainPropertyList(updateDomainList: DomainPropertyRo[]): void {
    this.propertyDataSource.data = [...updateDomainList]
    this.dataChanged = false;
  }
}
