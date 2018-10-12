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
  @ViewChild('rowActions') rowActions: TemplateRef<any>;

  @Input() @ViewChild('additionalToolButtons') additionalToolButtons: TemplateRef<any>;
  @Input() @ViewChild('searchPanel') searchPanel: TemplateRef<any>;

  @Input() id: String = "";
  @Input() title: String = "";
  @Input() columnPicker: ColumnPicker;
  @Input() url: string = '';
  @Input() searchTableController: SearchTableController;
  @Input() filter: any = {};

  loading = false;

  columnActions: any;

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
    this.columnActions = {
      cellTemplate: this.rowActions,
      name: 'Actions',
      width: 80,
      sortable: false
    };

    // Add actions to last column
    if (this.columnPicker) {
      this.columnPicker.allColumns.push(this.columnActions);
      this.columnPicker.selectedColumns.push(this.columnActions);
    }
    this.page(this.offset, this.rowLimiter.pageSize, this.orderBy, this.asc);
  }

  getRowClass(row): string {
    return row.deleted ? 'deleted' : '';
  }

  getTableDataEntries$(offset: number, pageSize: number, orderBy: string, asc: boolean): Observable<SearchTableResult> {
    let params: HttpParams = new HttpParams();
    params.set('page', offset.toString());
    params.set('pageSize', pageSize.toString());
    params.set('orderBy', orderBy);

    //filters
    if (this.filter.userName) {
      params.set('userName', this.filter.userName);
    }

    if (this.filter.participantId) {
      params.set('participantId', this.filter.participantId);
    }

    if (this.filter.participantSchema) {
      params.set('participantSchema', this.filter.participantSchema);
    }

    if(this.filter.domain) {
      params.set('domain', this.filter.domain )
    }

    if (asc != null) {
      params.set('asc', asc.toString());
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
      const count = result.count;
      const start = offset * pageSize;
      const end = Math.min(start + pageSize, count);
      const newRows = [...result.serviceEntities];

      let index = 0;
      for (let i = start; i < end; i++) {
        newRows[i] = {...result.serviceEntities[index++],
          status: SearchTableEntityStatus.PERSISTED,
          deleted: false
        };
      }
      this.rows = newRows;

      if(count > AlertComponent.MAX_COUNT_CSV) {
        this.alertService.error("Maximum number of rows reached for downloading CSV");
      }
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
      this.details(event.row);
    }
  }

  changePageSize(newPageLimit: number) {
    this.page(0, newPageLimit, this.orderBy, this.asc);
  }

  search() {
    this.page(0, this.rowLimiter.pageSize, this.orderBy, this.asc);
  }

  details(selectedRow: any) {
    this.searchTableController.showDetails(selectedRow);
  }

  onEditRowActionClicked(rowNumber: number) {
    this.editSearchTableEntity(rowNumber);
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
        this.rows = [...this.rows, {...formRef.componentInstance.current}];
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
          const modifiedUsers = this.rows.filter(el => el.status !== SearchTableEntityStatus.PERSISTED);
          // this.isBusy = true;
          this.http.put(/*UserComponent.USER_USERS_URL TODO: use PUT url*/'', modifiedUsers).subscribe(res => {
            // this.isBusy = false;
            // this.getUsers();
            this.alertService.success('The operation \'update\' completed successfully.', false);
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

  onCancelButtonClicked() {
    this.dialog.open(CancelDialogComponent).afterClosed().subscribe(result => {
      if (result) {
        this.page(this.offset, this.rowLimiter.pageSize, this.orderBy, this.asc);
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
        this.rows[rowNumber] = {...formRef.componentInstance.current, status};
        this.rows = [...this.rows];
      }
    });
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
        row.status = SearchTableEntityStatus.REMOVED;
        row.deleted = true;
      }
    }

    this.unselectRows()
  }

  private unselectRows() {
    this.selected = [];
  }
}
