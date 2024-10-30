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
  styleUrls: ['./smp-table.component.css']
})
export class SmpTableComponent implements AfterViewInit {
  @Output() onFilterChanged: EventEmitter<string> = new EventEmitter<string>();
  @Output() onPageChanged: EventEmitter<PageEvent> = new EventEmitter<PageEvent>();
  @Output() onRowClicked: EventEmitter<any> = new EventEmitter<any>();
  @Output() onRowDoubleClicked: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild(MatTable) table: MatTable<any>;
  @Input() filterLabel: string;
  @Input() filterPlaceholder: string;
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
  @ViewChild("smpTablePaginator") paginator: MatPaginator;

  selected: any;
  isLoadingResults = false;

  constructor() {
  }

  ngAfterViewInit(): void {
    // do not bind paginator here, it will be done in parent component
    // because internal paginator has its own paginator which is limmited only to page.
    if (!this.isLoadableTable) {
      this.dataSource.paginator = this.paginator;
    }
  }

  onFilterChangedEvent(event: Event) {
    let filterValue: string = (event.target as HTMLInputElement).value;
    this.onFilterChanged.emit(filterValue);
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

  @Input() set dataLength(value: number) {
    console.log("Setting paginator size: " + value);
    if (this.paginator) {
      this.paginator.length = value;
    }
  }

  get dataLength(): number {
    return this.paginator.length;
  }

  @Input() set pageSize(value: number) {
    if (this.paginator) {
      this.paginator.pageSize = value;
    }
  }

  get pageSize(): number {
    return this.paginator.pageSize;
  }

  @Input() set pageIndex(value: number) {
    if (this.paginator) {
      this.paginator.pageIndex = value;
    }
  }

  getHeaderStyle(col: SmpTableColDef): string {
    if (!col) {
      return '';
    }
    return ( col?.style?col.style:'') + ' '  +( col?.headerStyle?col.headerStyle:'') ;
  }

}
