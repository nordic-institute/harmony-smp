import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableResult} from './search-table-result.model';
import {Observable} from 'rxjs';
import {AlertMessageService} from '../alert-message/alert-message.service';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ColumnPicker} from '../column-picker/column-picker.model';
import {RowLimiter} from '../row-limiter/row-limiter.model';
import {SearchTableController} from './search-table-controller';
import {finalize} from 'rxjs/operators';
import {SearchTableEntity} from './search-table-entity.model';
import {EntityStatus} from '../enums/entity-status.enum';
import {CancelDialogComponent} from '../dialogs/cancel-dialog/cancel-dialog.component';
import {SaveDialogComponent} from '../dialogs/save-dialog/save-dialog.component';
import {DownloadService} from '../../download/download.service';
import {HttpParams} from '@angular/common/http';
import {ConfirmationDialogComponent} from "../dialogs/confirmation-dialog/confirmation-dialog.component";
import {SearchTableValidationResult} from "./search-table-validation-result.model";
import {ExtendedHttpClient} from "../../http/extended-http-client";
import {Router} from "@angular/router";
import ObjectUtils from "../utils/object-utils";

@Component({
  selector: 'smp-search-table',
  templateUrl: './search-table.component.html',
  styleUrls: ['./search-table.component.css']
})
export class SearchTableComponent implements OnInit {
  @ViewChild('searchTable', {static: true}) searchTable: any;
  @ViewChild('rowActions', {static: true}) rowActions: TemplateRef<any>;
  @ViewChild('rowExpand', {static: true}) rowExpand: TemplateRef<any>;
  @ViewChild('rowIndex', {static: true}) rowIndex: TemplateRef<any>;
  @Input() additionalToolButtons: TemplateRef<any>;
  @Input() additionalRowActionButtons: TemplateRef<any>;
  @Input() searchPanel: TemplateRef<any>;
  @Input() tableRowDetailContainer: TemplateRef<any>;
  @Input() tableTitle: TemplateRef<any>;

  @Input() id: String = "";
  @Input() title: String = "";
  @Input() columnPicker: ColumnPicker;
  @Input() url: string = ''; // URL for query (and if manageUrl is null also for "managing")
  @Input() manageUrl: string = ''; // (for "managing" the entities (add, update, remove) )
  @Input() searchTableController: SearchTableController;
  @Input() filter: any = {};
  @Input() showActionButtons: boolean = true;
  @Input() showSearchPanel: boolean = true;
  @Input() showIndexColumn: boolean = false;
  @Input() allowNewItems: boolean = false;
  @Input() allowEditItems: boolean = true;
  @Input() allowDeleteItems: boolean = false;

  loading = false;

  columnActions: any;
  columnExpandDetails: any;
  columnIndex: any;

  rowLimiter: RowLimiter = new RowLimiter();

  rowNumber: number;

  rows: Array<SearchTableEntity> = [];
  selected: Array<SearchTableEntity> = [];

  count: number = 0;
  offset: number = 0;
  orderBy: string = null;
  asc = false;
  forceRefresh: boolean = false;
  showSpinner: boolean = false;
  currentResult: SearchTableResult = null;
 // override datatable messages to remove selectedMessage message
 datatableMessages: any =  {
  // Message to show when array is presented
  // but contains no values
  emptyMessage: 'No data to display',

  // Footer total message
  totalMessage: 'total',

  // Footer selected message
  selectedMessage: null
};

  constructor(protected http: ExtendedHttpClient,
              protected alertService: AlertMessageService,
              private downloadService: DownloadService,
              public dialog: MatDialog,
              private router: Router) {
  }

  ngOnInit(): void {
    this.columnIndex = {
      cellTemplate: this.rowIndex,
      name: 'Index',
      width: 30,
      maxWidth: 80,
      sortable: false,
      showInitially: false
    };

    this.columnActions = {
      cellTemplate: this.rowActions,
      name: 'Actions',
      width: 100,
      maxWidth: 150,
      sortable: false,
      showInitially: false
    };
    this.columnExpandDetails = {
      cellTemplate: this.rowExpand,
      name: 'Upd.',
      width: 40,
      maxWidth: 50,
      sortable: false,
      showInitially: false
    };
  }


  tableColumnInit(){
    // Add actions to last column
    if (this.columnPicker) {
      // prepend columns
      if (!!this.tableRowDetailContainer) {
        console.log("show table row details!")
        this.columnExpandDetails.showInitially = true
        this.columnPicker.allColumns.unshift(this.columnExpandDetails);
      }
      if (this.showIndexColumn) {
        console.log("show table index!")
        this.columnIndex.showInitially = true
        this.columnPicker.allColumns.unshift(this.columnIndex);
      }

      if (this.showActionButtons) {
        console.log("show action buttons!")
        this.columnActions.showInitially = true
        this.columnPicker.allColumns.push(this.columnActions);
      }
      this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => col.showInitially);
    } else {
      console.log("Column picker is not registered for the table!")
    }

  }

  getRowClass(row) {
    return {
      'datatable-row-selected': (this.selected && this.selected.length >= 0 && this.rows.indexOf(row) === this.rowNumber),
      'table-row-new': (row.status === EntityStatus.NEW),
      'table-row-updated': (row.status === EntityStatus.UPDATED),
      'deleted': (row.status === EntityStatus.REMOVED)
    };
  }

  getTableDataEntries$(offset: number, pageSize: number, orderBy: string, asc: boolean): Observable<SearchTableResult> {
    let params: HttpParams = new HttpParams()
      .set('page', offset.toString())
      .set('pageSize', pageSize.toString());


    for (let filterProperty in this.filter) {
      if (this.filter.hasOwnProperty(filterProperty)) {
        // must encode else problem with + sign
        params = params.set(filterProperty, encodeURIComponent(this.filter[filterProperty]));
      }
    }

    this.loading = true;
    return this.http.get<SearchTableResult>(this.url, {params}).pipe(
      finalize(() => {
        this.loading = false;
      })
    );
  }

  page(offset: number, pageSize: number, orderBy: string, asc: boolean) {
    if (this.safeRefresh) {

      this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: "Not persisted data",
          description: "Action will refresh all data and not saved data will be lost. Do you wish to continue?"
        }
      }).afterClosed().subscribe(result => {
        if (result) {
          this.pageInternal(offset, pageSize, orderBy, asc);
        }
      })
    } else {
      this.pageInternal(offset, pageSize, orderBy, asc);
    }
  }

  private pageInternal(offset: number, pageSize: number, orderBy: string, asc: boolean) {
    this.getTableDataEntries$(offset, pageSize, orderBy, asc).subscribe((result: SearchTableResult) => {
      // empty page - probably refresh from delete...check if we can go one page back
      // try again
      if (result.count < 1 && offset > 0) {
        this.pageInternal(offset--, pageSize, orderBy, asc)
      } else {
        this.currentResult = result;
        this.offset = offset;
        this.rowLimiter.pageSize = pageSize;
        this.orderBy = orderBy;
        this.asc = asc;
        this.unselectRows();
        this.forceRefresh = false;
        this.count = result.count; // must be set else table can not calculate page numbers
        this.rows = result.serviceEntities.map(serviceEntity => {
          return {
            ...serviceEntity,
            status: EntityStatus.PERSISTED,
            deleted: false
          }
        });
      }
    }, (error: any) => {
      console.error("Error occurred while retrieving table data:" + JSON.stringify(error));
    });
  }

  onPage(event) {
    this.page(event.offset, event.pageSize, this.orderBy, this.asc);
  }

  onSort(event) {
    let ascending = event.newValue !== 'desc';
    this.page(this.offset, this.rowLimiter.pageSize, event.column.prop, ascending);
  }

  onSelect({selected}) {
    this.selected = [...selected];
    if (this.editButtonEnabled) {
      this.rowNumber = this.rows.indexOf(this.selected[0]);
    }
  }

  onActivate(event) {
    if ("dblclick" === event.type) {
      this.editSearchTableEntityRow(event.row);
    }
  }

  changePageSize(newPageLimit: number) {
    this.page(0, newPageLimit, this.orderBy, this.asc);
  }

  search() {
    this.page(0, this.rowLimiter.pageSize, this.orderBy, this.asc);
  }


  onNewButtonClicked() {
        this.fireCreateNewEntityEvent();
  }

  fireCreateNewEntityEvent() {
    const formRef: MatDialogRef<any> = this.searchTableController.newDialog({
      data: {edit: false}
    });
    if (!formRef) {
      return;
    }
    formRef.afterClosed().subscribe(result => {
      if (result) {
        this.rows = [...this.rows, {...formRef.componentInstance.getCurrent()}];
        this.count++;
      } else {
        this.unselectRows();
      }
    });
  }

  onDeleteButtonClicked() {
        this.fireDeleteEntityEvent();
  }

  fireDeleteEntityEvent() {
    this.deleteSearchTableEntities(this.selected);
  }

  onDeleteRowActionClicked(row: SearchTableEntity) {
        this.deleteSearchTableEntities([row]);
  }

  onEditButtonClicked() {
        this.fireEditEntityEvent();
  }

  fireEditEntityEvent() {
    if (this.rowNumber >= 0 && this.rows[this.rowNumber] && this.rows[this.rowNumber].deleted) {
      this.alertService.error('You cannot edit a deleted entry.', false);
      return;
    }
    this.editSearchTableEntity(this.rowNumber);
  }


  onSaveButtonClicked(withDownloadCSV: boolean) {
    try {
      this.dialog.open(SaveDialogComponent).afterClosed().subscribe(result => {
        if (result) {
          const modifiedRowEntities = this.rows.filter(el => el.status !== EntityStatus.PERSISTED);
          this.showSpinner = true;
          this.http.put(this.managementUrl, modifiedRowEntities).toPromise().then(res => {
            this.showSpinner = false;
            this.alertService.success('The operation \'update\' completed successfully.', false);
            this.forceRefresh = true;
            this.onRefresh();
            this.searchTableController.dataSaved();
            if (withDownloadCSV) {
              this.downloadService.downloadNative(/*UserComponent.USER_CSV_URL TODO: use CSV url*/ '');
            }
          }, err => {
            this.showSpinner = false;
            try {
              console.log("eror: " + err)
              let parser = new DOMParser();
              let xmlDoc = parser.parseFromString(err.error, "text/xml");
              let errDesc = xmlDoc.getElementsByTagName("ErrorDescription")[0].childNodes[0].nodeValue;
              this.alertService.exception('The operation \'update\' not completed successfully.', errDesc, false);
            } catch (err2) {
              // if parse failed
              this.alertService.exception('The operation \'update\' not completed successfully.', err, false);
            }
          });
        } else {
          this.showSpinner = false;
          if (withDownloadCSV) {
            this.downloadService.downloadNative(/*UserComponent.USER_CSV_URL TODO: use CSV url*/ '');
          }
        }
      });
    } catch (err) {
      // this.isBusy = false;
      this.showSpinner = false;
      this.alertService.exception('The operation \'update\' completed with errors.', err);
    }
  }

  onRefresh() {
    this.page(this.offset, this.rowLimiter.pageSize, this.orderBy, this.asc);
  }

  onCancelButtonClicked() {
    this.dialog.open(CancelDialogComponent).afterClosed().subscribe(result => {
      if (result) {
        this.onRefresh();
      }
    });
  }

  getRowsAsString(): number {
    return this.rows.length;
  }
  getCurrentResult(){
    return this.currentResult;
  }

  get editButtonEnabled(): boolean {
    return this.selected && this.selected.length == 1 && !this.selected[0].deleted;
  }

  get managementUrl(): string {
    return (this.manageUrl == null || this.manageUrl.length === 0)? this.url:this.manageUrl;
  }

  get deleteButtonEnabled(): boolean {
    return this.selected && this.selected.length > 0 && !this.selected.every(el => el.deleted);
  }

  get submitButtonsEnabled(): boolean {
    const rowsDeleted = !!this.rows.find(row => row.deleted);
    const dirty = rowsDeleted || !!this.rows.find(el => el.status !== EntityStatus.PERSISTED);
    return dirty;
  }

  get safeRefresh(): boolean {
    return !(!this.submitButtonsEnabled || this.forceRefresh);
  }

  isRowExpanderDisabled(row: any, rowDisabled: boolean): boolean {
    return rowDisabled || this.searchTableController.isRowExpanderDisabled(row);
  }

  private editSearchTableEntity(rowNumber: number) {
    const row = this.rows[rowNumber];
    const formRef: MatDialogRef<any> = this.searchTableController.newDialog({
      data: {edit: row?.status!=EntityStatus.NEW, row}
    });
    if (!formRef) {
      return;
    }
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const changed = this.searchTableController.isRecordChanged(row, formRef.componentInstance.getCurrent());
        if (changed) {
          const status = ObjectUtils.isEqual(row.status, EntityStatus.PERSISTED)
            ? EntityStatus.UPDATED
            : row.status;
          this.rows[rowNumber] = {...formRef.componentInstance.getCurrent(), status};
          this.rows = [...this.rows];
        }
      }
    });
  }

  public updateTableRow(rowNumber: number, row: any, status: EntityStatus) {
    this.rows[rowNumber] = {...row, status};
    this.rows = [...this.rows];
  }

  public getRowNumber(row: any) {
    return this.rows.indexOf(row);
  }

  private editSearchTableEntityRow(row: SearchTableEntity) {
    let rowNumber = this.rows.indexOf(row);
    this.editSearchTableEntity(rowNumber);
  }

  private deleteSearchTableEntities(rows: Array<SearchTableEntity>) {

    this.searchTableController.validateDeleteOperation(rows).subscribe((res: SearchTableValidationResult) => {
      if (!res.validOperation) {
        this.alertService.exception("Delete validation error", res.stringMessage, false);
      } else {
        for (const row of rows) {
          if (row.status === EntityStatus.NEW) {
            this.rows.splice(this.rows.indexOf(row), 1);
          } else {
            this.searchTableController.delete(row);
            row.status = EntityStatus.REMOVED;
            row.deleted = true;
          }
        }
        this.unselectRows();
      }
    });

  }

  private unselectRows() {
    this.selected = [];
  }

  toggleExpandRow(selectedRow: any) {
    //this.searchTableController.toggleExpandRow(selectedRow);
    this.searchTable.rowDetail.toggleExpandRow(selectedRow);
  }

  onDetailToggle(event) {

  }

  isDirty(): boolean {
    return this.submitButtonsEnabled;
  }
}
