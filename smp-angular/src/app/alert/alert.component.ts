import {AfterViewInit, Component, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog} from '@angular/material/dialog';

import {AlertMessageService} from '../common/alert-message/alert-message.service';
import {AlertController} from './alert-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableComponent} from "../common/search-table/search-table.component";
import {SecurityService} from "../security/security.service";


@Component({
  moduleId: module.id,
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements AfterViewInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;


  baseUrl = SmpConstants.REST_INTERNAL_ALERT_MANAGE;
  columnPicker: ColumnPicker = new ColumnPicker();
  alertController: AlertController;
  filter: any = {};
  isSMPIntegrationOn: boolean = false;


  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              public dialog: MatDialog) {
  }

  ngAfterViewInit() {
    this.alertController = new AlertController(this.http, this.lookups, this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Alert type',
        title: "Alert type.",
        prop: 'alertType',

      },
      {
        name: 'Alert status',
        title: "Alert status.",
        prop: 'alertStatus',

      },
      {
        name: 'Alert level',
        title: "Alert level.",
        prop: 'alertLevel',

      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Alert type', 'Alert status', 'Alert level'].indexOf(col.name) != -1
    });
  }


  details(row: any) {
    //this.alertController.showDetails(row);
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }
}
