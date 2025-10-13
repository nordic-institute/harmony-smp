import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output, TemplateRef,
  ViewChild
} from '@angular/core';
import {MatTable, MatTableDataSource} from "@angular/material/table";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {SmpTableColDef} from "./smp-table-coldef.model";
import {EntityStatus} from "../../enums/entity-status.enum";


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
  @Input() showFilter: boolean = true;
  @Input() displayedColumns: string[];
  @Input() columnDefList: SmpTableColDef[];

  // set this to true if pages are loaded from server
  // else complete data must be set to table datasource and pagination
  // is done on client side
  @Input() isLoadableTable: boolean = false;
  @Input() dataSource: MatTableDataSource<any>;
  @Input() multiTemplateDataRows: boolean = false;
  @ViewChild("smpTablePaginator") _paginator: MatPaginator;
  @Input() tableRowDetailContainer: TemplateRef<any>;

  _pageSizeOptions: number[] = [5, 10, 20, 50, 100];
  _pageSize: number = 10;

  @Input() set pageSizeOptions(value: number[]) {
    if (value && value.length > 0) {
      this._pageSizeOptions = value;
    }
  }
  get pageSizeOptions(): number[] {
    return this._pageSizeOptions;
  }

  @Input() set pageSize(value: number) {
    this._pageSize = value;
    if (this.paginator) {
      this.paginator.pageSize = value;
    }
  }
  get pageSize(): number {
    return this._pageSize;
  }

  get columnsWithDetailIds(): string[] {
    if (this.displayedColumns) {
      return [...this.displayedColumns, 'expandedDetail'];
    }
    return ['expandedDetail'];
  }

  // internal selected row

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
    if (this.paginator) {
      this.paginator.length = value;
    }
  }

  get dataLength(): number {
    return this.paginator?.length;
  }

  @Input() set pageIndex(value: number) {
    if (this.paginator) {
      this.paginator.pageIndex = value;
    }
  }

  firstPage(): void {
      this.paginator.firstPage();
  }

  lastPage(): void {
    this.paginator.lastPage();
  }

  getHeaderStyle(col: SmpTableColDef): string {
    if (!col) {
      return '';
    }
    return ( col?.style?col.style:'') + ' '  +( col?.headerStyle?col.headerStyle:'') ;
  }

  protected readonly EntityStatus = EntityStatus;
}
