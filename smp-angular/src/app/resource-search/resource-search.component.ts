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
import {MatDialog} from '@angular/material/dialog';
import {AlertMessageService} from '../common/alert-message/alert-message.service';
import {ResourceSearchController} from './resource-search-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableComponent} from "../common/search-table/search-table.component";
import {ResourceSearchRo} from "./resource-search-ro.model";
import {SubresourceSearchRo} from "./subresource-search-ro.model";
import {ResourceFilterOptionsService} from "../common/services/resource-filter-options.service";
import {ResourceFilterOptionsRo} from "../common/model/resource-filter-options-ro.model";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import {SecurityEventService} from "../security/security-event.service";
import {SmpTableColDef} from "../common/components/smp-table/smp-table-coldef.model";

@Component({
  templateUrl: './resource-search.component.html',
  styleUrls: ['./resource-search.component.css'],
  standalone: false
})
export class ResourceSearchComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @ViewChild('rowSMPUrlLinkAction', {static: true}) rowSMPUrlLinkAction: TemplateRef<any>
  @ViewChild('searchTable', {static: true}) searchTable: SearchTableComponent;
  @ViewChild('domainCodeTemplate') domainCodeTemplate: TemplateRef<any>;

  columns: SmpTableColDef[];
  displayedColumnIds: string[];
  resourceSearchController: ResourceSearchController;
  filter: any = {};
  contextPath: string = location.pathname.substring(0, location.pathname.length - 3); // remove /ui s
  baseUrl: string;
  domainList: string[];
  documentTypeList: string[];

  constructor(protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef,
              private resourceFilterOptionsService: ResourceFilterOptionsService,
              private translateService: TranslateService,
              private securityEventService: SecurityEventService) {

    this.baseUrl = SmpConstants.REST_PUBLIC_SEARCH_RESOURCE;
    this.securityEventService.onLogoutSuccessEvent().subscribe(value => {
      // refresh the search table pn logout
      this.searchTable.search();
    });
  }

  ngOnInit() {
    this.resourceFilterOptionsService.getResourceFilterOptions$().subscribe({
      next: (value: ResourceFilterOptionsRo) => {
        this.domainList = [''].concat(value.availableDomains || []);
        this.documentTypeList = [''].concat(value.availableDocumentTypes || []);

        this.resourceSearchController = new ResourceSearchController(this.dialog);
      },
      error: async (err) => {
        this.alertService.exception(await lastValueFrom(this.translateService.get("resource.search.error.fetch.resource.metadata")), err);
      }
    });

  }

  async initColumns() {

    this.displayedColumnIds = ['resource-count',
      'visibility',
      'domain',
      'resource-scheme',
      'resource-value',
      'resource-type',
      'resource-url'];

    this.columns = [
      {
        columnDef: 'resource-count',
        header: 'resource.search.label.column.subresource.count',
        cell: (row: ResourceSearchRo) => row.serviceMetadata?.length || 0,
        style: "max-width: 60px; width: 60px; display: flex; justify-content: right;"
      } as SmpTableColDef,
      {
        columnDef: 'visibility',
        header: 'resource.search.label.column.visibility',
        cell: (row: ResourceSearchRo) => row.visibility,
        style: "width: 120px; "
      } as SmpTableColDef,
      {
        columnDef: 'domain',
        header: 'resource.search.label.column.domain',
        cellTemplate: this.domainCodeTemplate
      } as SmpTableColDef,
      {
        columnDef: 'resource-scheme',
        header: 'resource.search.label.column.resource.scheme',
        cell: (row: ResourceSearchRo) => row.participantScheme,
        style: "flex-grow: 2;flex-basis:250px;"
      } as SmpTableColDef,
      {
        columnDef: 'resource-value',
        header: 'resource.search.label.column.resource.id',
        cell: (row: ResourceSearchRo) => row.participantIdentifier,
        style: "flex-grow: 4;flex-basis:250px;"
      } as SmpTableColDef,
      {
        columnDef: 'resource-type',
        header: 'resource.search.label.column.document.type',
        cell: (row: ResourceSearchRo) => row.documentType,
        style: "flex-basis:120px;"
      } as SmpTableColDef,
      {
        columnDef: 'resource-url',
        header: 'resource.search.label.column.resource.url',
        cellTemplate: this.rowSMPUrlLinkAction,
      } as SmpTableColDef,
    ];

    this.searchTable.tableColumnInit(this.columns, this.displayedColumnIds);
  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  ngAfterViewInit() {
    this.initColumns();
  }

  createResourceURL(row: ResourceSearchRo) {
    return (!row?.domainCode ? "" : row.domainCode + '/')
      + (!row?.resourceDefUrlSegment ? "" : row.resourceDefUrlSegment + '/')
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

  clearFilters(): void {
    this.filter = {};
  }
}
