import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TrustStoreService} from './trust-store.service';
import {TrustStoreEntry} from './trust-store-entry.model';
import {TrustStoreDialogComponent} from './trust-store-dialog/trust-store-dialog.component';
import {MatDialog, MatDialogRef} from '@angular/material';
import {TrustStoreUploadComponent} from './trust-store-upload/trust-store-upload.component';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {RowLimiter} from '../common/row-limiter/row-limiter.model';
import {DownloadService} from '../download/download.service';
import {AlertComponent} from '../alert/alert.component';
import {AlertService} from '../alert/alert.service';

@Component({
  selector: 'app-truststore',
  templateUrl: './trust-store.component.html',
  styleUrls: ['./trust-store.component.css']
})
export class TrustStoreComponent implements OnInit {

  columnPicker: ColumnPicker = new ColumnPicker();

  rowLimiter: RowLimiter = new RowLimiter();

  @ViewChild('rowWithDateFormatTpl') rowWithDateFormatTpl: TemplateRef<any>;

  trustStoreEntries: Array<TrustStoreEntry> = [];
  selectedMessages: Array<any> = [];
  loading: boolean = false;

  rows: Array<any> = [];

  static readonly TRUSTSTORE_URL: string = "rest/truststore";
  static readonly TRUSTSTORE_CSV_URL: string = TrustStoreComponent.TRUSTSTORE_URL + "/csv";

  constructor(private trustStoreService: TrustStoreService, public dialog: MatDialog, public alertService: AlertService, private downloadService: DownloadService) {
  }

  ngOnInit(): void {
    this.columnPicker.allColumns = [
      {

        name: 'Name',
        prop: 'name'
      },
      {
        name: 'Subject',
        prop: 'subject',
      },
      {
        name: 'Issuer',
        prop: 'issuer',
      },
      {
        cellTemplate: this.rowWithDateFormatTpl,
        name: 'Valid from',
        prop: 'validFrom'

      },
      {
        cellTemplate: this.rowWithDateFormatTpl,
        name: 'Valid until',
        prop: 'validUntil',
      }

    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Name", "Subject", "Issuer", "Valid from", "Valid until"].indexOf(col.name) != -1
    });
    this.getTrustStoreEntries();

    if(this.trustStoreEntries.length > AlertComponent.MAX_COUNT_CSV) {
      this.alertService.error("Maximum number of rows reached for downloading CSV");
    }
  }

  getTrustStoreEntries(): void {
    this.trustStoreService.getEntries().subscribe(trustStoreEntries => this.trustStoreEntries = trustStoreEntries);
  }

  onSelect({selected}) {
    console.log('Select Event');
    this.selectedMessages.splice(0, this.selectedMessages.length);
    this.selectedMessages.push(...selected);
  }

  onActivate(event) {
    console.log('Activate Event', event);
    if ("dblclick" === event.type) {
      this.details(event.row);
    }
  }

  details(selectedRow: any) {
    let dialogRef: MatDialogRef<TrustStoreDialogComponent> = this.dialog.open(TrustStoreDialogComponent, {data: {trustStoreEntry: selectedRow}});
    dialogRef.afterClosed().subscribe(result => {

    });
  }

  changePageSize(newPageSize: number) {
    this.rowLimiter.pageSize = newPageSize;
    this.getTrustStoreEntries();
  }

  openEditTrustStore() {
    let dialogRef: MatDialogRef<TrustStoreUploadComponent> = this.dialog.open(TrustStoreUploadComponent);
    dialogRef.componentInstance.onTruststoreUploaded.subscribe(updated => {
        this.getTrustStoreEntries();
    });
  }

  /**
   * Method that checks if CSV Button export can be enabled
   * @returns {boolean} true, if button can be enabled; and false, otherwise
   */
  isSaveAsCSVButtonEnabled() : boolean {
    return this.rows.length < AlertComponent.MAX_COUNT_CSV;
  }

  /**
   * Saves the content of the datatable into a CSV file
   */
  saveAsCSV() {
    this.downloadService.downloadNative(TrustStoreComponent.TRUSTSTORE_CSV_URL);
  }
}
