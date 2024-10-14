import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnInit,
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
  @Output() onRowSelected: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild(MatTable) table: MatTable<any>;
  @Input() filterLabel: string;
  @Input() filterPlaceholder: string;
  @Input() noResultLabel: string;
  @Input() noResultForFilterLabel: string;

  @Input() displayedColumns: string[];
  @Input() columnDefList: SmpTableColDef[];
  selected: any;
  isLoadingResults = false;

  @Input() dataSource: MatTableDataSource<any>;
  @ViewChild("smpTablePaginator") paginator: MatPaginator;

  constructor() {
  }

  ngAfterViewInit(): void {
    // bind data to resource controller
    this.dataSource.paginator = this.paginator;
  }


  onFilterChangedEvent(event: Event) {
    let filterValue: string = (event.target as HTMLInputElement).value;
    this.onFilterChanged.emit(filterValue);
  }

  onSelected(event: any) {
    //this.editResourceController.onSelectionChange(event
  }

  get selectedResource(): any {
    return this.selected;
  }


  onPageChangedEvent(page: PageEvent): void {
    this.onPageChanged.emit(page);
  }

  get disabledResourceFilter(): boolean {
    return false;
  }
}
