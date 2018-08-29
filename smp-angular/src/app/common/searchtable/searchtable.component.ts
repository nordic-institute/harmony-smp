import {Component, EventEmitter, Input, TemplateRef, ViewChild} from "@angular/core";
import {Http, URLSearchParams, Response} from "@angular/http";
import {SearchTableResult} from "./searchtableresult";
import {Observable} from "rxjs";
import {AlertService} from "../../alert/alert.service";
import {MdDialog, MdDialogRef} from "@angular/material";
import {ColumnPickerBase} from "../column-picker/column-picker-base";
import {RowLimiterBase} from "../row-limiter/row-limiter-base";
import {AlertComponent} from "../../alert/alert.component";
import {SearchTableController} from "./searchtable-controller";

@Component({
  selector: 'smp-search-table',
  templateUrl: './searchtable.component.html',
  providers: [],
  styleUrls: ['./searchtable.component.css']
})

export class SearchTableComponent {
  @ViewChild('rowActions') rowActions: TemplateRef<any>;

  @Input() @ViewChild('additionalToolButtons') additionalToolButtons: TemplateRef<any>;
  @Input() @ViewChild('searchPanel') searchPanel: TemplateRef<any>;




  @Input() id: String = "";
  @Input() title: String = "";
  @Input() columnPicker: ColumnPickerBase;
  @Input() url: string = '';
  @Input() searchTableController: SearchTableController;
  @Input() filter: any = {};



  columnActions:any;

  rowLimiter: RowLimiterBase = new RowLimiterBase();

  selected = [];



  loading: boolean = false;
  rows = [];
  count: number = 0;
  offset: number = 0;
  //default value
  orderBy: string = null;
  //default value
  asc: boolean = false;

  msgStatus: Array<String>;


  messageResent = new EventEmitter(false);

  constructor(protected http: Http, protected alertService: AlertService, public dialog: MdDialog) {
  }

  ngOnInit() {

    this.columnActions = {
      cellTemplate: this.rowActions,
      name: 'Actions',
      width: 80,
      sortable: false
    };
    /**
     * Add actions to last column
     */
    if (this.columnPicker) {
      this.columnPicker.allColumns.push(this.columnActions);

      this.columnPicker.selectedColumns.push(this.columnActions);
    }
    this.page(this.offset, this.rowLimiter.pageSize, this.orderBy, this.asc);
  }

  getTableDataEntries(offset: number, pageSize: number, orderBy: string, asc: boolean): Observable< SearchTableResult > {
    let searchParams: URLSearchParams = new URLSearchParams();
    searchParams.set('page', offset.toString());
    searchParams.set('pageSize', pageSize.toString());
    searchParams.set('orderBy', orderBy);

    //filters
    if (this.filter.participantId) {
      searchParams.set('participantId', this.filter.participantId);
    }

    if (this.filter.participantSchema) {
      searchParams.set('participantSchema', this.filter.participantSchema);
    }


    if(this.filter.domain) {
      searchParams.set('domain', this.filter.domain )
    }

    if (asc != null) {
      searchParams.set('asc', asc.toString());
    }

    return this.http.get(this.url, {
      search: searchParams
    }).map((response: Response) =>
      response.json()
    );
  }

  page(offset, pageSize, orderBy, asc) {
    this.loading = true;

    this.getTableDataEntries(offset, pageSize, orderBy, asc).subscribe((result: SearchTableResult ) => {
      console.log("service group response:" + result);
      this.offset = offset;
      this.rowLimiter.pageSize = pageSize;
      this.orderBy = orderBy;
      this.asc = asc;
      this.count = result.count;
      this.selected = [];

      const start = offset * pageSize;
      const end = start + pageSize;
      const newRows = [...result.serviceEntities];

      let index = 0;
      for (let i = start; i < end; i++) {
        newRows[i] = result.serviceEntities[index++];
      }

      this.rows = newRows;

      this.loading = false;

      if(this.count > AlertComponent.MAX_COUNT_CSV) {
        this.alertService.error("Maximum number of rows reached for downloading CSV");
      }
    }, (error: any) => {
      console.log("error getting the message log:" + error);
      this.loading = false;
      this.alertService.error("Error occured:" + error);
    });
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

  onSelect({selected}) {
    // console.log('Select Event', selected, this.selected);
  }

  onActivate(event) {
    // console.log('Activate Event', event);

    if ("dblclick" === event.type) {
      this.details(event.row);
    }
  }

  changePageSize(newPageLimit: number) {
    console.log('New page limit:', newPageLimit);
    this.page(0, newPageLimit, this.orderBy, this.asc);
  }

  search() {
    console.log("Searching using filter:" + this.filter);
    this.page(0, this.rowLimiter.pageSize, this.orderBy, this.asc);
  }

  isRowSelected() {
    if (this.selected)
      return true;

    return false;
  }


  details(selectedRow: any) {
    this.searchTableController.showDetails(selectedRow);
  }

  newButtonAction(){

  }

  editButtonAction(){
    this.editRowButtonAction( this.selected[0]);
  }

  deleteButtonAction(){
      // delete all seleted rows
  }

  editRowButtonAction(row: any){
    this.details(row);
  }

  deleteRowButtonAction(row: any){

  }

}
