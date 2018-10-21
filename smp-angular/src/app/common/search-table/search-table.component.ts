import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {SearchTableResult} from './search-table-result.model';
import {Observable} from 'rxjs';
import {AlertService} from '../../alert/alert.service';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ColumnPicker} from '../column-picker/column-picker.model';
import {RowLimiter} from '../row-limiter/row-limiter.model';
import {AlertComponent} from '../../alert/alert.component';
import {SearchTableController} from './search-table-controller';
import {finalize, map} from 'rxjs/operators';
import {SearchTableEntity} from './search-table-entity.model';
import {SearchTableEntityStatus} from './search-table-entity-status.model';
import {CancelDialogComponent} from '../cancel-dialog/cancel-dialog.component';
import {SaveDialogComponent} from '../save-dialog/save-dialog.component';
import {DownloadService} from '../../download/download.service';
import {HttpClient, HttpParams} from '@angular/common/http';


@Component({
  selector: 'smp-search-table',
  templateUrl: './search-table.component.html',
  styleUrls: ['./search-table.component.css']
})


export class SearchTableComponent implements OnInit {
  @ViewChild('searchTable') searchTable: any;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('rowExpand') rowExpand: TemplateRef<any>;
  @ViewChild('rowIndex') rowIndex: TemplateRef<any>;

  @Input() @ViewChild('additionalToolButtons') additionalToolButtons: TemplateRef<any>;
  @Input() @ViewChild('additionalRowActionButtons') additionalRowActionButtons: TemplateRef<any>;
  @Input() @ViewChild('searchPanel') searchPanel: TemplateRef<any>;
  @Input() @ViewChild('tableRowDetailContainer') tableRowDetailContainer: TemplateRef<any>;

  @Input() id: String = "";
  @Input() title: String = "";
  @Input() columnPicker: ColumnPicker;
  @Input() url: string = '';
  @Input() searchTableController: SearchTableController;
  @Input() filter: any = {};
  @Input() showActionButtons: boolean = true;
  @Input() showSearchPanel: boolean = true;
  @Input() showIndexColumn: boolean = false;

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

  constructor(protected http: HttpClient,
              protected alertService: AlertService,
              private downloadService: DownloadService,
              public dialog: MatDialog) {

  }

  ngOnInit() {
    this.columnIndex = {
      cellTemplate: this.rowIndex,
      name: 'Index',
      width: 50,
      maxWidth:80,
      sortable: false
    };

    this.columnActions = {
      cellTemplate: this.rowActions,
      name: 'Actions',
      width: 250,
      maxWidth:250,
      sortable: false
    };
    this.columnExpandDetails= {
      cellTemplate: this.rowExpand,
      name: ' ',
      width: 40,
      maxWidth:50,
      sortable: false
    };

    // Add actions to last column
    if (this.columnPicker) {
      // prepend columns
      if (!!this.tableRowDetailContainer){
        this.columnPicker.allColumns.unshift(this.columnExpandDetails);
        this.columnPicker.selectedColumns.unshift(this.columnExpandDetails);
      }
      if (this.showIndexColumn){
        this.columnPicker.allColumns.unshift(this.columnIndex);
        this.columnPicker.selectedColumns.unshift(this.columnIndex);
      }

      if (this.showActionButtons) {
        this.columnPicker.allColumns.push(this.columnActions);
        this.columnPicker.selectedColumns.push(this.columnActions);
      }
    }
  }

  getRowClass(row) {
    return {
      'table-row-new': (row.status === SearchTableEntityStatus.NEW),
      'table-row-updated': (row.status === SearchTableEntityStatus.UPDATED),
      'deleted': (row.status === SearchTableEntityStatus.REMOVED)
    };
  }


  getTableDataEntries$(offset: number, pageSize: number, orderBy: string, asc: boolean): Observable<SearchTableResult> {

    let params: HttpParams = new HttpParams()
       .set('page', offset.toString())
       .set('pageSize', pageSize.toString());


    for (let filterProperty in this.filter) {
      if (this.filter.hasOwnProperty(filterProperty)) {
        params = params.set(filterProperty, this.filter[filterProperty]);
      }
    }

    // TODO move to the HTTP service
    this.loading = true;
    return this.http.get<SearchTableResult>(this.url, { params }).pipe(
      finalize(() => {
        this.loading = false;
      })
    );
  }

  page(offset: number, pageSize: number, orderBy: string, asc: boolean) {
    this.getTableDataEntries$(offset, pageSize, orderBy, asc).subscribe((result: SearchTableResult ) => {
      this.offset = offset;
      this.rowLimiter.pageSize = pageSize;
      this.orderBy = orderBy;
      this.asc = asc;

      this.unselectRows();
      this.count = result.count; // must be set else table can not calculate page numbers
      this.rows = result.serviceEntities.map(serviceEntity => {
        return {...serviceEntity,
          status: SearchTableEntityStatus.PERSISTED,
          deleted: false}
      });
      /*
      if(this.count > AlertComponent.MAX_COUNT_CSV) {
        this.alertService.error("Maximum number of rows reached for downloading CSV");
      }*/
    }, (error: any) => {
      this.alertService.error("Error occurred:" + error);
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
    if(this.editButtonEnabled) {
      this.rowNumber = this.selected[0]["$$index"];
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


  onDeleteRowActionClicked(row: SearchTableEntity) {
    this.deleteSearchTableEntities([row]);
  }

  onNewButtonClicked() {
    const formRef: MatDialogRef<any> = this.searchTableController.newDialog({
      data: { edit: false }
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        this.rows = [...this.rows, {...formRef.componentInstance.getCurrent()}];
        //this.rows = this.rows.concat(formRef.componentInstance.current);
        this.count++;
       // this.searchtable.refresh();
      } else {
        this.unselectRows();
      }
    });
  }

  onDeleteButtonClicked() {
    this.deleteSearchTableEntities(this.selected);
  }

  onEditButtonClicked() {
    if (this.rowNumber >= 0 && this.rows[this.rowNumber] && this.rows[this.rowNumber].deleted) {
      this.alertService.error('You cannot edit a deleted entry.', false);
      return;
    }
    this.editSearchTableEntity(this.rowNumber);
  }

  onSaveButtonClicked(withDownloadCSV: boolean) {
    try {
      // TODO: add validation support to existing controllers
      // const isValid = this.userValidatorService.validateUsers(this.users);
      // if (!isValid) return;

      this.dialog.open(SaveDialogComponent).afterClosed().subscribe(result => {
        if (result) {
          // this.unselectRows();
          const modifiedRowEntities = this.rows.filter(el => el.status !== SearchTableEntityStatus.PERSISTED);
          // this.isBusy = true;
          this.http.put(/*UserComponent.USER_USERS_URL TODO: use PUT url*/this.url, modifiedRowEntities).subscribe(res => {
            // this.isBusy = false;
            // this.getUsers();
            this.alertService.success('The operation \'update\' completed successfully.', false);
            this.onRefresh();
            if (withDownloadCSV) {
              this.downloadService.downloadNative(/*UserComponent.USER_CSV_URL TODO: use CSV url*/ '');
            }
          }, err => {
            // this.isBusy = false;
            // this.getUsers();
            this.alertService.exception('The operation \'update\' not completed successfully.', err, false);
          });
        } else {
          if (withDownloadCSV) {
            this.downloadService.downloadNative(/*UserComponent.USER_CSV_URL TODO: use CSV url*/ '');
          }
        }
      });
    } catch (err) {
      // this.isBusy = false;
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

  get editButtonEnabled(): boolean {
    return this.selected && this.selected.length == 1 && !this.selected[0].deleted;
  }

  get deleteButtonEnabled(): boolean {
    return this.selected && this.selected.length > 0 && !this.selected.every(el => el.deleted);
  }

  get submitButtonsEnabled(): boolean {
    const rowsDeleted = !!this.rows.find(row => row.deleted);
    const dirty = rowsDeleted || !!this.rows.find(el => el.status !== SearchTableEntityStatus.PERSISTED);
    return dirty;
  }

  private editSearchTableEntity(rowNumber: number) {
    const row = this.rows[rowNumber];
    const formRef: MatDialogRef<any> = this.searchTableController.newDialog({
      data: {edit: true, row}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const status = row.status === SearchTableEntityStatus.PERSISTED
          ? SearchTableEntityStatus.UPDATED
          : row.status;
         this.rows[rowNumber] = {...formRef.componentInstance.getCurrent(), status};
         this.rows = [...this.rows];
      }
    });
  }
  public updateTableRow(rowNumber:number, row:any, status:SearchTableEntityStatus ) {
    this.rows[rowNumber] = {...row, status};
    this.rows = [...this.rows];
  }
  public getRowNumber(row:any){
    return  this.rows.indexOf(row);
  }


  private editSearchTableEntityRow(row: SearchTableEntity) {
      let rowNumber = this.rows.indexOf(row);
      this.editSearchTableEntity(rowNumber);
  }

  private deleteSearchTableEntities(rows: Array<SearchTableEntity>) {
    // TODO: add validation support to existing controllers
    // if (this.searchTableController.validateDeleteOperation(rows)) {
    //   this.alertService.error('You cannot delete the logged in user: ' + this.securityService.getCurrentUser().username);
    //   return;
    // }

    for (const row of rows) {
      if (row.status === SearchTableEntityStatus.NEW) {
        this.rows.splice(this.rows.indexOf(row), 1);
      } else {
        this.searchTableController.delete(row);
        row.status = SearchTableEntityStatus.REMOVED;
        row.deleted = true;
      }
    }

    this.unselectRows()
  }

  private unselectRows() {
    this.selected = [];
  }

  toggleExpandRow(selectedRow: any){
    //this.searchTableController.toggleExpandRow(selectedRow);
    this.searchTable.rowDetail.toggleExpandRow(selectedRow);
  }

  onDetailToggle (event){

  }
}
