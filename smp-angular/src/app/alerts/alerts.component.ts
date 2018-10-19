import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {RowLimiter} from '../common/row-limiter/row-limiter.model';
import {DownloadService} from '../download/download.service';
import {AlertComponent} from '../alert/alert.component';
import {Observable} from 'rxjs';
import {AlertsResult} from './alerts-result.model';
import {AlertService} from '../alert/alert.service';
import {AlertsEntry} from './alerts-entry.model';
import {CancelDialogComponent} from '../common/cancel-dialog/cancel-dialog.component';
import {MatDatepickerInputEvent, MatDialog} from '@angular/material';
import {SaveDialogComponent} from '../common/save-dialog/save-dialog.component';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';

@Component({
  moduleId: module.id,
  templateUrl: './alerts.component.html'
})

export class AlertsComponent implements OnInit {

  @ViewChild('rowProcessed') rowProcessed: TemplateRef<any>;

  static readonly ALERTS_URL: string = 'rest/alerts';

  columnPicker: ColumnPicker = new ColumnPicker();
  rowLimiter: RowLimiter = new RowLimiter();

  advancedSearch: boolean;
  loading: boolean = false;

  // data table
  rows = [];
  count: number = 0;
  offset: number = 10;
  //default value
  orderBy: string = "creationTime";
  //default value
  asc: boolean = false;

  buttonsDisabled: boolean = true;

  // Mocked values
  aTypes = ['MSG_COMMUNICATION_FAILURE','MSG_TEST'];
  aLevels = ['HIGH', 'MEDIUM', 'LOW'];

  aProcessedValues = ['PROCESSED', 'UNPROCESSED'];

  filter: any = {};

  dynamicFilters = [];

  items=[];

  timestampCreationFromMaxDate: Date = new Date();
  timestampCreationToMinDate: Date = null;
  timestampCreationToMaxDate: Date = new Date();
  timestampReportingFromMaxDate: Date = new Date();
  timestampReportingToMinDate: Date = null;
  timestampReportingToMaxDate: Date = new Date();

  constructor(private http: HttpClient, private alertService: AlertService, public dialog: MatDialog, private downloadService: DownloadService) {
  }

  ngOnInit() {
    this.filter.alertType = null;

    this.columnPicker.allColumns = [
      { name: 'Processed', cellTemplate: this.rowProcessed, width: 50 },
      { name: 'Alert Id' },
      { name: 'Alert Type' },
      { name: 'Alert Level', width: 50 },
      { name: 'Alert Text' },
      { name: 'Creation Time' },
      { name: 'Reporting Time' },
      { name: 'Parameters', sortable: false }
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Processed", "ID", "Alert Type", "Alert Level", "Creation Time", "Reporting Time", "Parameters"].indexOf(col.name) != -1
    });

    this.page(this.offset, this.rowLimiter.pageSize, this.orderBy, this.asc);
  }

  getAlertsEntries(offset: number, pageSize: number, orderBy: string, asc: boolean): Observable<AlertsResult> {
    let params: HttpParams = new HttpParams();
    params.set('page', offset.toString());
    params.set('pageSize', pageSize.toString());
    params.set('orderBy', orderBy);

    // filters
    if(this.filter.processed) {
      params.set('processed', this.filter.processed==='PROCESSED'?'true':'false');
    }

    if(this.filter.alertType) {
      params.set('alertType', this.filter.alertType);
    }

    if(this.filter.alertId) {
      params.set('alertId', this.filter.alertId);
    }

    if(this.filter.alertLevel) {
      params.set('alertLevel', this.filter.alertLevel);
    }

    if(this.filter.creationFrom) {
      params.set('creationFrom', this.filter.creationFrom.getTime());
    }

    if(this.filter.creationTo) {
      params.set('creationTo', this.filter.creationTo.getTime());
    }

    if(this.filter.reportingFrom) {
      params.set('reportingFrom', this.filter.reportingFrom.getTime());
    }

    if(this.filter.reportingTo) {
      params.set('reportingTo', this.filter.reportingTo.getTime());
    }

    if(this.dynamicFilters.length > 0) {
      let d : string[];
      for(let i = 0; i < this.dynamicFilters.length; i++) {
        d[i] = '';
      }
      for(let filter in this.dynamicFilters) {
        d[filter] = this.dynamicFilters[filter];
      }
      params.set('parameters', d.toString());
    }

    if (asc != null) {
      params.set('asc', asc.toString());
    }

    return this.http.get<AlertsResult>(AlertsComponent.ALERTS_URL, { params });
  }

  page(offset, pageSize, orderBy, asc) {
    this.loading = true;

    let newEntries: AlertsEntry[] = [];

    // MOCK info
    /*let entry1: AlertsEntry = new AlertsEntry(true, 'alertId1', this.aTypes[0], this.aLevels[0], 'aText1', new Date(), new Date(), ['asasas','ddsdsd','ddd']);
    let entry2: AlertsEntry = new AlertsEntry(false, 'alertId2', this.aTypes[1], this.aLevels[1], 'aText2', new Date(), new Date(), ['tryrty','trurutru']);
    let entry3: AlertsEntry = new AlertsEntry(true, 'alertId3', this.aTypes[0], this.aLevels[0], 'aText3', new Date(), new Date(), ['aaaaa','bbbbb','cccccc']);
    newEntries[0] = entry1;
    newEntries[1] = entry2;
    newEntries[2] = entry3;

    // information of parameters and values
    let entry: any;
    for(entry in newEntries) {
      let buildParams = [], pos = 0, params = this.getDynamicParameters(newEntries[entry].alertType);
      for(let param in params) {
        buildParams[pos] = params[param] + '=' + newEntries[entry].parameters[pos++];
      }
      newEntries[entry].parameters = buildParams;
    }

    this.rows = newEntries;

    this.count = 3;
    this.offset = offset;
    this.rowLimiter.pageSize = pageSize;
    this.orderBy = orderBy;
    this.asc = asc;
    this.loading = false;*/

    this.getAlertsEntries(offset, pageSize, orderBy, asc).subscribe( (result: AlertsResult) => {
      console.log("alerts response: " + result);
      this.offset = offset;
      this.rowLimiter.pageSize = pageSize;
      this.orderBy = orderBy;
      this.asc = asc;
      this.count = result.count;

      const start = offset * pageSize;
      const end = start + pageSize;
      const newRows = [...result.alertsEntries];

      let index = 0;
      for(let i = start; i <end; i++) {
        newRows[i] = result.alertsEntries[index++];
      }

      // information of parameters and values
      let entry: any;
      for(entry in newRows) {
        let buildParams = [], pos = 0, params = this.getDynamicParameters(newRows[entry].alertType);
        for(let param in params) {
          buildParams[pos] = params[param] + '=' + newRows[entry].parameters[pos++];
        }
        newRows[entry].parameters = buildParams;
      }

      this.rows = newRows;

      this.filter = result.filter;
      this.aLevels = result.alertsLevels;
      this.aTypes = result.alertsType;

      this.loading = false;
/*
      if(this.count > AlertComponent.MAX_COUNT_CSV) {
        this.alertService.error("Maximum number of rows reached for downloading CSV");
      }
      */
    }, (error: any) => {
      console.log("error getting the alerts:" + error);
      this.loading = false;
      this.alertService.error("Error occured:" + error);
    });
  }

  search() {
    console.log("Searching using filter:" + this.filter);
    this.page(0, this.rowLimiter.pageSize, this.orderBy, this.asc);
  }

  toggleAdvancedSearch() {
    this.advancedSearch = !this.advancedSearch;
    return false;//to prevent default navigation
  }

  onAlertTypeChanged(alertType: string) {
    this.items = this.getDynamicParameters(alertType);
  }

  getDynamicParameters(alertType:string): string[] {
    if(alertType) {
      // just for testing begin: MOCK
      if(alertType == 'MSG_COMMUNICATION_FAILURE') {
        return ['MSG_COMM1', 'MSG_COMM2', 'MSG_COMM3']
      } else {
        return ['MSG_TEST1', 'MSG_TEST2'];
      }
      // just for testing end
    } else {
      return [];
    }
  }

  onTimestampCreationFromChange(event: MatDatepickerInputEvent<Date>) {
    this.timestampCreationToMinDate = event.value;
  }

  onTimestampCreationToChange(event: MatDatepickerInputEvent<Date>) {
    this.timestampCreationFromMaxDate = event.value;
  }

  onTimestampReportingFromChange(event: MatDatepickerInputEvent<Date>) {
    this.timestampReportingToMinDate = event.value;
  }

  onTimestampReportingToChange(event: MatDatepickerInputEvent<Date>) {
    this.timestampReportingFromMaxDate = event.value;
  }

  // datatable methods

  onActivate(event) {
    console.log('Activate Event', event);

    // Prepared if in the future we will show details of alerts
    /*if ("dblclick" === event.type) {
      this.details(event.row);
    }*/
  }

  onPage(event) {
    console.log('Page Event', event);
    this.page(event.offset, event.pageSize, this.orderBy, this.asc);
  }

  onSort(event) {
    console.log('Sort Event', event);
    let ascending = true;
    if (event.newValue === 'desc') {
      ascending = false;
    }
    this.page(this.offset, this.rowLimiter.pageSize, event.column.prop, ascending);
  }

  changePageSize(newPageLimit: number) {
    console.log('New page limit:', newPageLimit);
    this.rowLimiter.pageSize = newPageLimit;
    this.page(0, newPageLimit, this.orderBy, this.asc);
  }

  /**
   * Method that checks if CSV Button export can be enabled
   * @returns {boolean} true, if button can be enabled; and false, otherwise
   */
  isSaveAsCSVButtonEnabled() : boolean {
    return this.rows.length < AlertComponent.MAX_COUNT_CSV;
  }

  saveAsCSV() {
    if(!this.buttonsDisabled) {
      this.save(true);
    } else {
      this.downloadService.downloadNative(AlertsComponent.ALERTS_URL + "/csv" + this.getFilterPath());
    }
  }

  private getFilterPath() {
    let result = '?';
    //filters
    if(this.filter.processed != null) {
      result += 'processed=' + (this.filter.processed==='PROCESSED') + '&';
    }

    if(this.filter.alertType) {
      result += 'alertType=' + this.filter.alertType + '&';
    }

    if(this.filter.alertId) {
      result += 'alertId=' + this.filter.alertId + '&';
    }

    if(this.filter.alertLevel) {
      result += 'alertLevel=' + this.filter.alertLevel + '&';
    }

    if(this.filter.creationFrom) {
      result += 'creationFrom=' + this.filter.creationFrom.getTime() + '&';
    }

    if(this.filter.creationTo) {
      result += 'creationTo=' + this.filter.creationTo.getTime() + '&';
    }

    if(this.filter.reportingFrom) {
      result += 'reportingFrom=' + this.filter.reportingFrom.getTime() + '&';
    }

    if(this.filter.reportingTo) {
      result += 'reportingTo=' + this.filter.reportingTo.getTime() + '&';
    }

    return result;
  }

  public isAlertTypeDefined(): boolean {
    return this.filter.alertType;
  }

  cancel() {
    let dialogRef = this.dialog.open(CancelDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.buttonsDisabled = true;
        this.page(this.offset, this.rowLimiter.pageSize, this.orderBy, this.asc);
      }
    });
  }

  save(withDownloadCSV: boolean) {
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    let dialogRef = this.dialog.open(SaveDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.http.put(AlertsComponent.ALERTS_URL, JSON.stringify(this.rows), {headers}).subscribe(res => {
          this.alertService.success("The operation 'update alerts' completed successfully.", false);
          this.page(this.offset, this.rowLimiter.pageSize, this.orderBy, this.asc);
          if(withDownloadCSV) {
            this.downloadService.downloadNative(AlertsComponent.ALERTS_URL + "/csv");
          }
        }, err => {
          this.alertService.error("The operation 'update alerts' not completed successfully.", false);
          this.page(this.offset, this.rowLimiter.pageSize, this.orderBy, this.asc);
        });
      } else {
        if(withDownloadCSV) {
          this.downloadService.downloadNative(AlertsComponent.ALERTS_URL + "/csv");
        }
      }
    });
  }

  setProcessedValue(row) {
    this.buttonsDisabled = false;
    row.processed = !row.processed;
    this.rows[row.$$index] = row;
  }
}
