import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableResult} from './search-table-result.model';
import {lastValueFrom, Observable} from 'rxjs';
import {AlertMessageService} from '../alert-message/alert-message.service';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
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
import ObjectUtils from "../utils/object-utils";
import {TranslateService} from "@ngx-translate/core";
import {MatTableDataSource} from "@angular/material/table";
import {SmpTableColDef} from "../components/smp-table/smp-table-coldef.model";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'smp-search-table',
  templateUrl: './search-table.component.html',
  styleUrls: ['./search-table.component.css'],
  standalone: false
})
export class SearchTableComponent implements OnInit, AfterViewInit {
  @Output() onRowDoubleClickedEventEmitter: EventEmitter<SearchTableEntity> = new EventEmitter<SearchTableEntity>();

  @ViewChild('rowActions', {static: true}) rowActions: TemplateRef<any>;
  @ViewChild('rowExpand', {static: true}) rowExpand: TemplateRef<any>;
  @ViewChild('rowIndex', {static: true}) rowIndex: TemplateRef<any>;
  @Input() additionalToolButtons: TemplateRef<any>;
  @Input() additionalSearchAreaButtons: TemplateRef<any>;
  @Input() additionalRowActionButtons: TemplateRef<any>;
  @Input() searchPanel: TemplateRef<any>;
  @Input() tableRowDetailContainer: TemplateRef<any>;
  @Input() tableTitle: TemplateRef<any>;

  @Input() id: String = "";
  @Input() title: String = "";
  @Input() url: string = ''; // URL for query (and if manageUrl is null also for "managing")
  @Input() manageUrl: string = ''; // (for "managing" the entities (add, update, remove) )
  @Input() searchTableController: SearchTableController<any>;
  @Input() filter: any = {};
  @Input() showActionButtons: boolean = true;
  @Input() showSearchParametersPanel: boolean = true;
  @Input() showSimpleFilter: boolean = false;
  @Input() simpleFilterKey: string = "filter";
  @Input() filterLabel: string;
  @Input() filterPlaceholder: string;
  @Input() filterValue: string;
  @Input() noResultLabel: string;
  @Input() noResultForFilterLabel: string;
  @Input() disabledFilter: boolean;
  @Input() showIndexColumn: boolean = false;
  @Input() allowNewItems: boolean = false;
  @Input() allowEditItems: boolean = true;
  @Input() allowDeleteItems: boolean = false;

  @Input() columns: SmpTableColDef[];
  @Input() displayedColumnIds: string[];

  loading = false;

  columnActions: SmpTableColDef;
  columnExpandAction: SmpTableColDef;
  columnIndex: SmpTableColDef;
  dataSource: MatTableDataSource<SearchTableEntity> = new MatTableDataSource();
  selectedRows: Array<SearchTableEntity> = [];

  totalRowCount: number = 0;
  pageSize: number = 50;
  pageIndex: number = 50;
  orderBy: string = null;
  asc = false;
  forceRefresh: boolean = false;
  showSpinner: boolean = false;
  currentResult: SearchTableResult = null;

  constructor(protected http: ExtendedHttpClient,
              protected alertService: AlertMessageService,
              private downloadService: DownloadService,
              public dialog: MatDialog,
              private translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.columnIndex = {
      columnDef: 'row-index',
      header: 'search.table.label.column.index',
      cellTemplate: this.rowIndex,
      style: "max-width: 50px; width: 50px; display: flex; justify-content: right;"
    } as SmpTableColDef;

    this.columnActions = {
      columnDef: 'row-actions',
      header: 'search.table.label.column.actions',
      cellTemplate: this.rowActions,
      style: "max-width: 150px; width: 100px; display: flex; justify-content: center;"
    } as SmpTableColDef;

    this.columnExpandAction = {
      columnDef: 'row-expand',
      header: 'search.table.label.column.expand',
      cellTemplate: this.rowExpand,
      style: "max-width: 50px; width: 50px; display: flex; justify-content: center;"
    } as SmpTableColDef;
  }

  ngAfterViewInit() {
    this.search();
  }


  tableColumnInit(columns: SmpTableColDef[], displayedColumnIds: string[]) {
    // Add actions to last column
    if (columns) {
      // prepend columns
      if (!!this.tableRowDetailContainer) {
        columns.unshift(this.columnExpandAction);
        displayedColumnIds.unshift(this.columnExpandAction.columnDef);
      }
      if (this.showIndexColumn) {
        columns.unshift(this.columnIndex);
        displayedColumnIds.unshift(this.columnIndex.columnDef);
      }
      if (this.showActionButtons) {
        columns.push(this.columnActions);
        displayedColumnIds.push(this.columnActions.columnDef);
      }
    } else {
      console.log("No Columns registered for the table!")
    }

  }

  getRowClass(row: SearchTableEntity) {
    return {
      'datatable-row-selected': (this.isRowSelected(row)),
      'table-row-new': (row.status === EntityStatus.NEW),
      'table-row-updated': (row.status === EntityStatus.UPDATED),
      'deleted': (row.status === EntityStatus.REMOVED)
    };
  }

  /**
   * Check if a row is selected and return true if is already in the selectedRows array or false if not
   * @param row the row to check
   * @return true if the row is selected, false otherwise
   */
  isRowSelected(row: SearchTableEntity): boolean {
    return this.selectedRows && this.selectedRows.indexOf(row) !== -1;
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

  async page(offset: number, pageSize: number, orderBy: string, asc: boolean) {
    if (this.safeRefresh) {

      this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: await lastValueFrom(this.translateService.get("search.table.dirty.confirmation.dialog.title")),
          description: await lastValueFrom(this.translateService.get("search.table.dirty.confirmation.dialog.description"))
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

  private pageInternal(pageIndex: number, pageSize: number, orderBy: string, asc: boolean) {

    this.getTableDataEntries$(pageIndex, pageSize, orderBy, asc).subscribe((result: SearchTableResult) => {
      // empty page - probably refresh from delete...check if we can go one page back
      // try again
      if (result.count < 1 && pageIndex > 0) {
        // empty page - probably refresh from delete...check if we can go one page back
        // try again
        this.pageInternal(pageIndex--, pageSize, orderBy, asc)
      } else {
        this.currentResult = result;
        this.dataSource.data = result.serviceEntities.map(serviceEntity => {
          return {
            ...serviceEntity,
            status: EntityStatus.PERSISTED,
            deleted: false
          }
        });

        this.totalRowCount = result.count;
        this.pageSize = result.pageSize;
        this.pageIndex = result.page;
        this.unselectRows();
      }
    }, (error: any) => {
      this.currentResult = null
      console.error("Error occurred while retrieving table data:" + JSON.stringify(error));
    });
  }

  onRowDoubleClicked(row: SearchTableEntity) {
    this.onRowDoubleClickedEventEmitter.emit(row);
    this.editSearchTableEntityRow(row);
  }

  /**
   *  Get the current selected row (if multiple selected, the first one)
   */
  get currentRow(): SearchTableEntity {
    return this.selectedRows && this.selectedRows.length > 0 ? this.selectedRows[0] : null;
  }

  search() {
    this.page(0, this.pageSize, this.orderBy, this.asc);
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
        this.dataSource.data = [...this.dataSource.data, {...formRef.componentInstance.getCurrent()}];
        this.totalRowCount++;
      } else {
        this.unselectRows();
      }
    });
  }

  onDeleteButtonClicked() {
    this.fireDeleteEntityEvent();
  }

  fireDeleteEntityEvent() {
    this.deleteSearchTableEntities(this.selectedRows);
  }

  onDeleteRowActionClicked(row: SearchTableEntity) {
    this.deleteSearchTableEntities([row]);
  }

  onEditButtonClicked() {
    this.fireEditEntityEvent();
  }

  fireEditEntityEvent() {

    if (this.currentRow?.deleted) {
      this.alertService.error('You cannot edit a deleted entry.', false);
      return;
    }
    this.editSearchTableEntity(this.currentRow);
  }

  async onSaveButtonClicked(withDownloadCSV: boolean) {
    try {
      this.dialog.open(SaveDialogComponent).afterClosed().subscribe(result => {
        if (result) {
          const modifiedRowEntities = this.dataSource.data.filter(el => el.status !== EntityStatus.PERSISTED);
          this.showSpinner = true;
          this.http.put(this.managementUrl, modifiedRowEntities).toPromise().then(async res => {
            this.showSpinner = false;
            this.alertService.success(await lastValueFrom(this.translateService.get("search.table.success.update")), false);
            this.forceRefresh = true;
            this.onRefresh();
            this.searchTableController.dataSaved();
            if (withDownloadCSV) {
              this.downloadService.downloadNative(/*UserComponent.USER_CSV_URL TODO: use CSV url*/ '');
            }
          }, async err => {
            this.showSpinner = false;
            try {
              console.log("eror: " + err)
              let parser = new DOMParser();
              let xmlDoc = parser.parseFromString(err.error, "text/xml");
              let errDesc = xmlDoc.getElementsByTagName("ErrorDescription")[0].childNodes[0].nodeValue;
              this.alertService.exception(await lastValueFrom(this.translateService.get("search.table.error.update")), errDesc, false);
            } catch (err2) {
              // if parse failed
              this.alertService.exception(await lastValueFrom(this.translateService.get("search.table.error.update")), err, false);
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
      this.alertService.exception(await lastValueFrom(this.translateService.get("search.table.error.update")), err);
    }
  }

  onRefresh() {
    this.page(this.pageIndex, this.pageSize, this.orderBy, this.asc);
  }

  onCancelButtonClicked() {
    this.dialog.open(CancelDialogComponent).afterClosed().subscribe(result => {
      if (result) {
        this.onRefresh();
      }
    });
  }

  getCurrentResult() {
    return this.currentResult;
  }

  get editButtonEnabled(): boolean {
    return this.selectedRows && this.selectedRows.length == 1 && !this.selectedRows[0].deleted;
  }

  get managementUrl(): string {
    return (this.manageUrl == null || this.manageUrl.length === 0) ? this.url : this.manageUrl;
  }

  get deleteButtonEnabled(): boolean {
    return this.selectedRows && this.selectedRows.length > 0 && !this.selectedRows.every(el => el.deleted);
  }

  get submitButtonsEnabled(): boolean {
    let rowsDeleted = !!this.dataSource.data.find(row => row.deleted);
    return rowsDeleted || !!this.dataSource.data.find(el => el.status !== EntityStatus.PERSISTED);
  }

  get safeRefresh(): boolean {
    return !(!this.submitButtonsEnabled || this.forceRefresh);
  }

  isRowExpanderDisabled(row: any, rowDisabled: boolean): boolean {
    return rowDisabled || this.searchTableController.isRowExpanderDisabled(row);
  }

  private editSearchTableEntity(editRow: SearchTableEntity) {

    let rowNumber = this.getRowNumber(editRow);
    const formRef: MatDialogRef<any> = this.searchTableController.newDialog({
      data: {edit: editRow?.status != EntityStatus.NEW, row: editRow}
    });
    if (!formRef) {
      return;
    }
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const changed = this.searchTableController.isRecordChanged(editRow, formRef.componentInstance.getCurrent());
        if (changed) {
          const status = ObjectUtils.isEqual(editRow.status, EntityStatus.PERSISTED)
            ? EntityStatus.UPDATED
            : editRow.status;
          this.dataSource.data[rowNumber] = {
            ...formRef.componentInstance.getCurrent(),
            status
          };
          this.dataSource.data = [...this.dataSource.data];
        }
      }
    });
  }

  public getRowNumber(row: any) {
    return this.dataSource.data.indexOf(row);
  }

  private editSearchTableEntityRow(row: SearchTableEntity) {
    this.editSearchTableEntity(row);
  }

  private deleteSearchTableEntities(rows: Array<SearchTableEntity>) {

    this.searchTableController.validateDeleteOperation(rows).subscribe(async (res: SearchTableValidationResult) => {
      if (!res.validOperation) {
        this.alertService.exception(await lastValueFrom(this.translateService.get("search.table.error.delete")), res.stringMessage, false);
      } else {
        for (const row of rows) {
          if (row.status === EntityStatus.NEW) {
            this.dataSource.data.splice(this.dataSource.data.indexOf(row), 1);
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
    this.selectedRows = [];
  }

  onToggleExpandRow(selectedRow: SearchTableEntity) {
    selectedRow.expanded = !selectedRow.expanded;
  }

  isDirty(): boolean {
    return this.submitButtonsEnabled;
  }

  applyTableFilter(filterValue: string) {
    this.filter[this.simpleFilterKey] = filterValue?.trim().toLowerCase();
    this.search();
  }

  public onRowSelected(tableEntity: SearchTableEntity) {
    if (this.isRowSelected(tableEntity)) {
      console.log("Row already selected");
      return;
    }
    // current implementation allows only single selection
    this.selectedRows = [tableEntity];
  }

  onPageChanged(page: PageEvent) {
    this.page(page.pageIndex, page.pageSize, this.orderBy, this.asc);
  }
}
