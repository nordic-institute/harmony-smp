import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog} from '@angular/material/dialog';
import {AlertMessageService} from '../common/alert-message/alert-message.service';
import {PropertyController} from './property-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableComponent} from "../common/search-table/search-table.component";
import {SecurityService} from "../security/security.service";
import {EntityStatus} from "../common/model/entity-status.model";


@Component({
  moduleId: module.id,
  templateUrl: './property.component.html',
  styleUrls: ['./property.component.css']
})
export class PropertyComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('propertyColumnTemplate') propertyColumnTemplate: TemplateRef<any>;
  @ViewChild('propertyValueTemplate') propertyValueTemplate: TemplateRef<any>;

  baseUrl = SmpConstants.REST_INTERNAL_PROPERTY_MANAGE;
  columnPicker: ColumnPicker = new ColumnPicker();
  propertyController: PropertyController;
  filter: any = {property: ""};

  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {

  }

  jsonStringify(val: any): string {
    return JSON.stringify(val);
  }

  ngOnInit() {
    this.propertyController = new PropertyController(this.http, this.lookups, this.dialog);
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
