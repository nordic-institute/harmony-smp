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
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";

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
    this.columnPicker.allColumns = [
      {
        name: await lastValueFrom(this.translateService.get("alert.panel.label.column.alert.date")),
        title: await lastValueFrom(this.translateService.get("alert.panel.label.column.title.alert.date")),
        prop: 'reportingTime',
        showInitially: true,
        maxWidth: 250,
        cellTemplate: this.dateTimeColumn,
      },
      {
        name: await lastValueFrom(this.translateService.get("alert.panel.label.column.alert.level")),
        title: await lastValueFrom(this.translateService.get("alert.panel.label.column.title.alert.level")),
        prop: 'alertLevel',
        showInitially: true,
        maxWidth: 100,

      },
      {
        name: await lastValueFrom(this.translateService.get("alert.panel.label.column.for.user")),
        title: await lastValueFrom(this.translateService.get("alert.panel.label.column.title.for.user")),
        prop: 'username',
        cellTemplate: this.forUser,
        maxWidth: 200,
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("alert.panel.label.column.credential.type")),
        title: await lastValueFrom(this.translateService.get("alert.panel.label.column.title.credential.type")),
        prop: 'alertDetails',
        maxWidth: 200,
        cellTemplate: this.credentialType,
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("alert.panel.label.column.alert.type")),
        title: await lastValueFrom(this.translateService.get("alert.panel.label.column.title.alert.type")),
        prop: 'alertType',
        cellTemplate: this.truncateText,
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("alert.panel.label.column.alert.status")),
        title: await lastValueFrom(this.translateService.get("alert.panel.label.column.title.alert.status")),
        prop: 'alertStatus',
        showInitially: true,
        maxWidth: 100,
      },
      {
        name: await lastValueFrom(this.translateService.get("alert.panel.label.column.status.description")),
        title: await lastValueFrom(this.translateService.get("alert.panel.label.column.title.status.description")),
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

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  get dateTimeFormat(): string {
    return this.lookups.getDateTimeFormat();
  }
}
