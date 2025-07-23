import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild
} from '@angular/core';
import {MatTable, MatTableDataSource} from "@angular/material/table";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {SmpTableColDef} from "./smp-table-coldef.model";


@Component({
    selector: 'smp-table',
    templateUrl: './smp-table.component.html',
    styleUrls: ['./smp-table.component.css'],
    standalone: false
})
export class SmpTableComponent implements AfterViewInit {
  @Output() onFilterChanged: EventEmitter<string> = new EventEmitter<string>();
  @Output() onPageChanged: EventEmitter<PageEvent> = new EventEmitter<PageEvent>();
  @Output() onRowClicked: EventEmitter<any> = new EventEmitter<any>();
  @Output() onRowDoubleClicked: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild(MatTable) table: MatTable<any>;
  @Input() filterLabel: string;
  @Input() filterPlaceholder: string;
  @Input() filterValue: string;
  @Input() noResultLabel: string;
  @Input() noResultForFilterLabel: string;
  @Input() disabledFilter: boolean;
  @Input() displayedColumns: string[];
  @Input() columnDefList: SmpTableColDef[];

  // set this to true if pages are loaded from server
  // else complete data must be set to table datasource and pagination
  // is done on client side
  @Input() isLoadableTable: boolean = false;
  @Input() dataSource: MatTableDataSource<any>;
  @ViewChild("smpTablePaginator") _paginator: MatPaginator;

  selected: any;
  isLoadingResults = false;

  constructor() {
  }

  ngAfterViewInit(): void {
    // do not bind paginator here, it will be done in parent component
    // because internal paginator has its own paginator which is limited only to page.
    if (!this.isLoadableTable) {
       this.dataSource.paginator = this.paginator;
    }
    if (this.filterValue === undefined) {
      this.filterValue = '';
    }
  }

  onFilterChangedEvent(event: Event) {
    let value: string = (event.target as HTMLInputElement).value;
    this.onFilterChanged.emit(value);
  }

  onRowClickedEvent(row: any) {
    this.onRowClicked.emit(row);
  }

  onRowDoubleClickedEvent(row: any) {
    this.onRowDoubleClicked.emit(row);
  }

  @Input() set selectedRow(value: any) {
    this.selected = value;
  }

  get selectedRow(): any {
    return this.selected;
  }

  onPageChangedEvent(page: PageEvent): void {
    this.onPageChanged.emit(page);
  }

  @Input() set isLoading(value: boolean) {
    this.isLoadingResults = value;
  }

  get isLoading(): boolean {
    return this.isLoadingResults;
  }

  get paginator() : MatPaginator {
    return this._paginator;
  }

  @Input() set dataLength(value: number) {
    console.log("Setting paginator size: " + value);
    if (this._paginator) {
      this._paginator.length = value;
    }
  }

  get dataLength(): number {
    return this._paginator.length;
  }

  @Input() set pageSize(value: number) {
    if (this._paginator) {
      this._paginator.pageSize = value;
    }
  }

  get pageSize(): number {
    return this._paginator.pageSize;
  }

  @Input() set pageIndex(value: number) {
    if (this._paginator) {
      this._paginator.pageIndex = value;
    }
  }

  firstPage(): void {
      this._paginator.firstPage();
  }

  lastPage(): void {
    this._paginator.lastPage();
  }

  getHeaderStyle(col: SmpTableColDef): string {
    if (!col) {
      return '';
    }
    return ( col?.style?col.style:'') + ' '  +( col?.headerStyle?col.headerStyle:'') ;
  }

}
