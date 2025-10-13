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
import {MatDialog} from '@angular/material/dialog';

import {AlertMessageService} from '../../alert-message/alert-message.service';
import {AlertController} from './alert-controller';
import {HttpClient} from '@angular/common/http';
import {GlobalLookups} from "../../global-lookups";
import {SearchTableComponent} from "../../search-table/search-table.component";
import {SecurityService} from "../../../security/security.service";
import {TranslateService} from "@ngx-translate/core";
import {DateTimeService} from "../../services/date-time.service";
import {SmpTableColDef} from "../../components/smp-table/smp-table-coldef.model";
import {AlertRo} from "./alert-ro.model";

/**
 * This is a generic alert panel component for previewing alert list
 */
@Component({
    selector: 'alert-panel',
    templateUrl: './alert-panel.component.html',
    styleUrls: ['./alert-panel.component.css'],
    standalone: false
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
  alertController: AlertController;
  filter: any = {};
  columns: SmpTableColDef[];
  displayedColumnIds: string[];

  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
              protected dateTimeService: DateTimeService,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef,
              private translateService: TranslateService) {
  }

  ngOnInit() {
    this.alertController = new AlertController(this.lookups, this.dialog);
  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  async initColumns() {
    this.displayedColumnIds = [
      'reporting-time',
      'alert-level',
      'username',
      'alert-details',
      'alert-type',
      'alert-status',
      'alert-status-desc'
    ];

    this.columns = [
      {
        columnDef: 'reporting-time',
        header: 'alert.panel.label.column.alert.date',
        headerTooltip: 'alert.panel.label.column.title.alert.date',
        cellTemplate: this.dateTimeColumn,
        style: "max-width: 250px; width: 200px; display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'alert-level',
        header: 'alert.panel.label.column.alert.level',
        headerTooltip: 'alert.panel.label.column.title.alert.level',
        cell: (alert: AlertRo) => alert.alertLevel,
        style: "max-width:100px; width: 100px; display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'username',
        header: 'alert.panel.label.column.for.user',
        headerTooltip: 'alert.panel.label.column.title.for.user',
        cellTemplate: this.forUser,
        style: "display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'alert-details',
        header: 'alert.panel.label.column.credential.type',
        headerTooltip: 'alert.panel.label.column.title.credential.type',
        cellTemplate: this.credentialType,
        style: "display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'alert-type',
        header: 'alert.panel.label.column.alert.type',
        headerTooltip: 'alert.panel.label.column.title.alert.type',
        cellTemplate: this.credentialType,
        style: "max-width:100px; width: 100px; display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'alert-status',
        header: 'alert.panel.label.column.alert.status',
        headerTooltip: 'alert.panel.label.column.title.alert.status',
        cell: (alert: AlertRo) => alert.alertStatus,
        style: "max-width:100px; width: 100px; display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'alert-status-desc',
        header: 'alert.panel.label.column.status.description',
        headerTooltip: 'alert.panel.label.column.title.status.description',
        cellTemplate: this.truncateText,
        style: "display: flex; justify-content: left;"
      } as SmpTableColDef,
    ];

    this.searchTable.tableColumnInit(this.columns, this.displayedColumnIds);
  }

  ngAfterViewInit() {
    this.initColumns();
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  get dateTimeFormat(): string {
    return this.dateTimeService.userDateTimeFormat;
  }
}
