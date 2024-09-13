import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Input,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {ColumnPicker} from '../../column-picker/column-picker.model';
import {MatDialog} from '@angular/material/dialog';

import {AlertMessageService} from '../../alert-message/alert-message.service';
import {AlertController} from './alert-controller';
import {HttpClient} from '@angular/common/http';
import {GlobalLookups} from "../../global-lookups";
import {SearchTableComponent} from "../../search-table/search-table.component";
import {SecurityService} from "../../../security/security.service";
import {
  ObjectPropertiesDialogComponent
} from "../../dialogs/object-properties-dialog/object-properties-dialog.component";

/**
 * This is a generic alert panel component for previewing alert list
 */
@Component({
  selector: 'alert-panel',
  templateUrl: './alert-panel.component.html',
  styleUrls: ['./alert-panel.component.css']
})
export class AlertPanelComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('dateTimeColumn') dateTimeColumn: TemplateRef<any>;
  @ViewChild('truncateText') truncateText: TemplateRef<any>;
  @ViewChild('credentialType') credentialType: TemplateRef<any>;
  @ViewChild('forUser') forUser: TemplateRef<any>;

  @Input() baseUrl = null;
  columnPicker: ColumnPicker = new ColumnPicker();
  alertController: AlertController;
  filter: any = {};

  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.alertController = new AlertController(this.lookups, this.dialog);
  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  initColumns() {
    this.columnPicker.allColumns = [
      {
        name: 'Alert date',
        title: "Alert date",
        prop: 'reportingTime',
        showInitially: true,
        maxWidth: 250,
        cellTemplate: this.dateTimeColumn,
      },
      {
        name: 'Alert level',
        title: "Alert level.",
        prop: 'alertLevel',
        showInitially: true,
        maxWidth: 100,

      },
      {
        name: 'For User',
        title: "For User",
        prop: 'username',
        cellTemplate: this.forUser,
        maxWidth: 200,
        showInitially: true,
      },
      {
        name: 'Credential type',
        title: "Credential type.",
        prop: 'alertDetails',
        maxWidth: 200,
        cellTemplate: this.credentialType,
        showInitially: true,
      },
      {
        name: 'Alert type',
        title: "Alert type.",
        prop: 'alertType',
        cellTemplate: this.truncateText,
        showInitially: true,
      },
      {
        name: 'Alert status',
        title: "Alert status.",
        prop: 'alertStatus',
        showInitially: true,
        maxWidth: 100,
      },
      {
        name: 'Status desc.',
        title: "Status desc.",
        prop: 'alertStatusDesc',
        cellTemplate: this.truncateText,
        showInitially: true,

      },
    ];
    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => col.showInitially);
    this.searchTable.tableColumnInit();
  }

  ngAfterViewInit() {
    this.initColumns();
  }


  details(row: any) {
    this.dialog.open(ObjectPropertiesDialogComponent, {
      data: {
        title: "Alert details",
        object: row.alertDetails,

      }
    });
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  get dateTimeFormat(): string {
    return this.lookups.getDateTimeFormat();
  }
}
