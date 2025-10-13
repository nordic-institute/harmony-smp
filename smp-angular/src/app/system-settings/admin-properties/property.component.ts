import {AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, TemplateRef, ViewChild} from '@angular/core';
import {AlertMessageService} from '../../common/alert-message/alert-message.service';
import {PropertyController} from './property-controller';
import {SmpConstants} from "../../smp.constants";
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {SecurityService} from "../../security/security.service";
import {EntityStatus} from "../../common/enums/entity-status.enum";
import {SmpTableColDef} from "../../common/components/smp-table/smp-table-coldef.model";
import {PropertyRo} from "./property-ro.model";


@Component({
  templateUrl: './property.component.html',
  styleUrls: ['./property.component.css'],
  standalone: false
})
export class PropertyComponent implements AfterViewInit, AfterViewChecked {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('propertyColumnTemplate') propertyColumnTemplate: TemplateRef<any>;
  @ViewChild('propertyValueTemplate') propertyValueTemplate: TemplateRef<any>;
  @ViewChild('propertyTypeColumnTemplate') propertyTypeColumnTemplate: TemplateRef<any>;

  baseUrl: string = SmpConstants.REST_INTERNAL_PROPERTY_MANAGE;
  filter: any = {property: ""};

  columns: SmpTableColDef[];
  displayedColumnIds: string[];

  constructor(public securityService: SecurityService,
              protected propertyController: PropertyController,
              protected alertService: AlertMessageService,
              private changeDetector: ChangeDetectorRef) {
  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  async initColumns() {

    this.displayedColumnIds = [
      'property-key',
      'property-value',
      'property-type',
      'restart-needed'
    ];

    this.columns = [
      {
        columnDef: 'property-key',
        header: 'property.label.column.property',
        headerTooltip: 'property.label.column.title.property',
        cellTemplate: this.propertyColumnTemplate,
        style: "max-width: 580px; width: 200px; display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'property-value',
        header: 'property.label.column.value',
        headerTooltip: 'property.label.column.title.value',
        cellTemplate: this.propertyValueTemplate,
        style: "width: 120px; "
      } as SmpTableColDef,
      {
        columnDef: 'property-type',
        header: 'property.label.column.type',
        headerTooltip: 'property.label.column.title.type',
        cellTemplate: this.propertyTypeColumnTemplate,
        style: "max-width: 120px; width: 120px;"
      } as SmpTableColDef,
      {
        columnDef: 'restart-needed',
        header: 'property.label.column.restart.needed',
        headerTooltip: 'property.label.column.title.restart.needed',
        cell: (row: PropertyRo) => row.restartNeeded,
        style: "max-width: 120px; width: 120px;"
      } as SmpTableColDef,
    ];

    this.searchTable.tableColumnInit(this.columns, this.displayedColumnIds);
  }

  ngAfterViewInit() {
    this.initColumns();
  }

  searchPropertyChanged() {
    this.searchTable.search();
  }

  details(row: any) {
    this.propertyController.showDetails(row);
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  aliasCssClass(row: PropertyRo) {
    if (row.status === EntityStatus.NEW) {
      return 'table-row-new';
    } else if (row.status === EntityStatus.UPDATED) {
      return 'table-row-updated';
    } else if (row.status === EntityStatus.REMOVED) {
      return 'deleted';
    } else if (row.updateDate) {
      return 'table-row-pending';
    }
  }

  isServerRestartNeeded(): boolean {
    return this.searchTable != null
      && this.searchTable.getCurrentResult() != null
      && this.searchTable.getCurrentResult()['serverRestartNeeded'];
  }
}
