import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {ColumnPicker} from '../../common/column-picker/column-picker.model';
import {AlertMessageService} from '../../common/alert-message/alert-message.service';
import {PropertyController} from './property-controller';
import {SmpConstants} from "../../smp.constants";
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {SecurityService} from "../../security/security.service";
import {EntityStatus} from "../../common/enums/entity-status.enum";


@Component({
  templateUrl: './property.component.html',
  styleUrls: ['./property.component.css']
})
export class PropertyComponent implements AfterViewInit, AfterViewChecked {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('propertyColumnTemplate') propertyColumnTemplate: TemplateRef<any>;
  @ViewChild('propertyValueTemplate') propertyValueTemplate: TemplateRef<any>;

  baseUrl: string = SmpConstants.REST_INTERNAL_PROPERTY_MANAGE;
  columnPicker: ColumnPicker = new ColumnPicker();
  filter: any = {property: ""};

  constructor(public securityService: SecurityService,
              protected propertyController: PropertyController,
              protected alertService: AlertMessageService,
              private changeDetector: ChangeDetectorRef) {

  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  initColumns() {
    this.columnPicker.allColumns = [
      {
        name: 'Property',
        title: "Property key.",
        prop: 'property',
        maxWidth: 580,
        cellTemplate: this.propertyColumnTemplate,
        showInitially: true,
      },
      {
        name: 'Value',
        title: "Property value.",
        prop: 'value',
        cellTemplate: this.propertyValueTemplate,
        showInitially: true,

      },
    ];
    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => col.showInitially);
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

  aliasCssClass(alias: string, row) {
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
