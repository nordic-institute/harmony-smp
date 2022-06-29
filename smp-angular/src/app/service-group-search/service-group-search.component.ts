///<reference path="../smp.constants.ts"/>
import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog} from '@angular/material/dialog';
import {AlertMessageService} from '../common/alert-message/alert-message.service';
import {ServiceGroupSearchController} from './service-group-search-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableComponent} from "../common/search-table/search-table.component";

@Component({
  moduleId: module.id,
  templateUrl: './service-group-search.component.html',
  styleUrls: ['./service-group-search.component.css']
})
export class ServiceGroupSearchComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @ViewChild('rowSMPUrlLinkAction', {static: true}) rowSMPUrlLinkAction: TemplateRef<any>
  @ViewChild('rowActions', {static: true}) rowActions: TemplateRef<any>;
  @ViewChild('searchTable', {static: true}) searchTable: SearchTableComponent;

  columnPicker: ColumnPicker = new ColumnPicker();
  serviceGroupSearchController: ServiceGroupSearchController;
  filter: any = {};
  contextPath: string = location.pathname.substring(0, location.pathname.length - 3); // remove /ui s
  baseUrl: string;

  constructor(protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService:
                AlertMessageService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {

    this.baseUrl = SmpConstants.REST_PUBLIC_SEARCH_SERVICE_GROUP;
  }

  ngOnDestroy() {

  }

  ngOnInit(): void {
    this.serviceGroupSearchController = new ServiceGroupSearchController(this.dialog);
  }

  initColumns(): void {
    this.columnPicker.allColumns = [
      {
        name: 'Metadata size',
        prop: 'serviceMetadata.length',
        showInitially: true,
        width: 120,
        maxWidth: 120,
        resizable: "false",
      },
      {
        name: 'Participant scheme',
        prop: 'participantScheme',
        showInitially: true,
        width: 300,
        maxWidth: 300,
        resizable: "false"
      },
      {
        name: 'Participant identifier',
        prop: 'participantIdentifier',
        showInitially: true,
      },
      {
        cellTemplate: this.rowSMPUrlLinkAction,
        name: 'OASIS ServiceGroup URL',
        showInitially: true,
        width: 250,
        maxWidth: 250,
        resizable: "false",
        sortable: false
      },
    ];
    this.searchTable.tableColumnInit();
  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  ngAfterViewInit() {
    this.initColumns();
  }

  createServiceGroupURL(row: any) {
    return encodeURIComponent((!row.participantScheme ? '' : row.participantScheme) + '::' + row.participantIdentifier);
  }

  createServiceMetadataURL(row: any, rowSMD: any) {
    return encodeURIComponent((!row.participantScheme ? '' : row.participantScheme) + '::' + row.participantIdentifier) + '/services/' + encodeURIComponent((!rowSMD.documentIdentifierScheme ? '' : rowSMD.documentIdentifierScheme) + '::' + rowSMD.documentIdentifier);
  }

  details(row: any) {
    this.serviceGroupSearchController.showDetails(row);

  }
}
