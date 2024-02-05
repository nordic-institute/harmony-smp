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
import {ResourceSearchController} from './resource-search-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableComponent} from "../common/search-table/search-table.component";
import {ResourceSearchRo} from "./resource-search-ro.model";
import {SubresourceSearchRo} from "./subresource-search-ro.model";

@Component({
  templateUrl: './resource-search.component.html',
  styleUrls: ['./resource-search.component.css']
})
export class ResourceSearchComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @ViewChild('rowSMPUrlLinkAction', {static: true}) rowSMPUrlLinkAction: TemplateRef<any>
  @ViewChild('rowActions', {static: true}) rowActions: TemplateRef<any>;
  @ViewChild('searchTable', {static: true}) searchTable: SearchTableComponent;

  columnPicker: ColumnPicker = new ColumnPicker();
  resourceSearchController: ResourceSearchController;
  filter: any = {};
  contextPath: string = location.pathname.substring(0, location.pathname.length - 3); // remove /ui s
  baseUrl: string;

  constructor(protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService:
                AlertMessageService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {

    this.baseUrl = SmpConstants.REST_PUBLIC_SEARCH_RESOURCE;
  }

  ngOnInit(): void {
    this.resourceSearchController = new ResourceSearchController(this.dialog);
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

  createResourceURL(row: ResourceSearchRo) {

    return (!row?.domainCode? "" : row.domainCode+ '/')
          + (!row?.resourceDefUrlSegment?"" : row.resourceDefUrlSegment + '/')
          + encodeURIComponent((!row.participantScheme ? '' : row.participantScheme) + '::' + row.participantIdentifier);
  }

  createServiceMetadataURL(row: ResourceSearchRo, rowSMD: SubresourceSearchRo) {

    return this.createResourceURL(row)
            + '/' + rowSMD.subresourceDefUrlSegment + '/'
            + encodeURIComponent((!rowSMD.documentIdentifierScheme ? '' : rowSMD.documentIdentifierScheme) + '::' + rowSMD.documentIdentifier);
  }



  details(row: any) {
    this.resourceSearchController.showDetails(row);

  }
}
