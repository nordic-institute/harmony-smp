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
import {ServiceGroupSearchRo} from "./service-group-search-ro.model";
import {ServiceMetadataSearchRo} from "./service-metadata-search-ro.model";

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
        name: 'Sr. Cnt.',
        prop: 'serviceMetadata.length',
        width: 70,
        maxWidth: 70,
        resizable: 'false',
        showInitially: true,
      },
      {
        name: 'Domain',
        prop: 'domainCode',
        width: 180,
        maxWidth: 180,
        resizable: 'false',
        showInitially: true,
      },
      {
        name: 'Resource scheme',
        prop: 'participantScheme',
        width: 250,
        maxWidth: 250,
        resizable: 'true',
        showInitially: true,
      },
      {
        name: 'Resource identifier',
        prop: 'participantIdentifier',
        width: 450,
        resizable: 'true',
        showInitially: true,
      },
      {
        cellTemplate: this.rowSMPUrlLinkAction,
        name: 'Resource URL',
        width: 120,
        maxWidth: 120,
        resizable: 'false',
        showInitially: true,
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

  createServiceGroupURL(row: ServiceGroupSearchRo) {

    return (!row?.domainCode? "" : row.domainCode+ '/')
          + (!row?.resourceDefUrlSegment?"" : row.resourceDefUrlSegment + '/')
          + encodeURIComponent((!row.participantScheme ? '' : row.participantScheme) + '::' + row.participantIdentifier);
  }

  createServiceMetadataURL(row: ServiceGroupSearchRo, rowSMD: ServiceMetadataSearchRo) {

    return this.createServiceGroupURL(row)
            + '/' + rowSMD.subresourceDefUrlSegment + '/'
            + encodeURIComponent((!rowSMD.documentIdentifierScheme ? '' : rowSMD.documentIdentifierScheme) + '::' + rowSMD.documentIdentifier);
  }



  details(row: any) {
    this.serviceGroupSearchController.showDetails(row);

  }
}
