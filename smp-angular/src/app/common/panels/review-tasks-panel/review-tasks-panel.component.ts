import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Input,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';

import {AlertMessageService} from '../../alert-message/alert-message.service';
import {HttpClient} from '@angular/common/http';
import {GlobalLookups} from "../../global-lookups";
import {SearchTableComponent} from "../../search-table/search-table.component";
import {SecurityService} from "../../../security/security.service";
import {ReviewTasksController} from "./review-tasks-controller";
import {lastValueFrom} from "rxjs";
import {TranslateService} from "@ngx-translate/core";
import {NavigationService} from "../../../window/sidenav/navigation-model.service";
import {EditResourceService} from "../../../edit/edit-resources/edit-resource.service";
import {ReviewDocumentVersionRo} from "../../model/review-document-version-ro.model";
import {DateTimeService} from "../../services/date-time.service";
import {SmpTableColDef} from "../../components/smp-table/smp-table-coldef.model";

/**
 * This is a generic alert panel component for previewing alert list
 */
@Component({
  selector: 'review-tasks-panel',
  templateUrl: './review-tasks-panel.component.html',
  styleUrls: ['./review-tasks-panel.component.css'],
  standalone: false
})
export class ReviewTasksPanelComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('dateTimeColumn') dateTimeColumn: TemplateRef<any>;
  @ViewChild('truncateText') truncateText: TemplateRef<any>;
  @ViewChild('credentialType') credentialType: TemplateRef<any>;
  @ViewChild('forUser') forUser: TemplateRef<any>;


  @Input() baseUrl = null;
  columns: SmpTableColDef[];
  displayedColumnIds: string[];
  reviewTaskController: ReviewTasksController;
  filter: any = {};
  selected: any;

  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
              protected dateTimeService: DateTimeService,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              private translateService: TranslateService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef,
              private navigationService: NavigationService,
              private editResourceService: EditResourceService,) {
  }

  ngOnInit() {
    this.reviewTaskController = new ReviewTasksController(this.lookups, this.dialog);
  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  async initColumns() {
    this.displayedColumnIds = [
      'last-updated-on',
      'document-level',
      'version',
      'resource-identifier-scheme',
      'resource-identifier-value',
      'subresource-identifier-scheme',
      'subresource-identifier-value',
    ];

    this.columns = [
      {
        columnDef: 'last-updated-on',
        header: 'review.edit.panel.label.column.review.date',
        headerTooltip: 'review.edit.panel.label.column.title.review.date',
        cellTemplate: this.dateTimeColumn,
        style: "max-width: 200px; width: 200px; display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'document-level',
        header: 'review.edit.panel.label.column.target',
        cell: (row: ReviewDocumentVersionRo) => row.documentLevel,
        style: "max-width: 160px; width: 160px; display: flex; justify-content: left;"
      } as SmpTableColDef,
      {
        columnDef: 'version',
        header: 'review.edit.panel.label.column.version',
        cell: (row: ReviewDocumentVersionRo) => row.version,
        style: "max-width: 60px; width: 60px; display: flex; justify-content: right;"
      } as SmpTableColDef,
      {
        columnDef: 'resource-identifier-scheme',
        header: 'review.edit.panel.label.column.resource.scheme',
        cell: (row: ReviewDocumentVersionRo) => row.resourceIdentifierScheme,
      } as SmpTableColDef,
      {
        columnDef: 'resource-identifier-value',
        header: 'review.edit.panel.label.column.resource.value',
        cell: (row: ReviewDocumentVersionRo) => row.resourceIdentifierValue,
      } as SmpTableColDef,
      {
        columnDef: 'subresource-identifier-scheme',
        header: 'review.edit.panel.label.column.subresource.scheme',
        cell: (row: ReviewDocumentVersionRo) => row.subresourceIdentifierScheme,
      } as SmpTableColDef,
      {
        columnDef: 'subresource-identifier-value',
        header: 'review.edit.panel.label.column.subresource.value',
        cell: (row: ReviewDocumentVersionRo) => row.subresourceIdentifierValue,
      } as SmpTableColDef,
    ]

    this.searchTable.tableColumnInit(this.columns, this.displayedColumnIds);
  }

  ngAfterViewInit() {
    this.initColumns();
  }

  details(row: any) {
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  get dateTimeFormat(): string {
    return this.dateTimeService.userDateTimeFormat;
  }

  async onRowDoubleClicked(row: ReviewDocumentVersionRo) {
    // set selected resource
    this.editResourceService.selectedReviewDocument = row;
    let node = await this.createNewReviewDocumentNavigationNode();
    this.navigationService.selected.children = [node]
    this.navigationService.select(node);
  }

  public async createNewReviewDocumentNavigationNode() {
    return {
      code: "review-document",
      icon: "note",
      name: await lastValueFrom(this.translateService.get("review.edit.panel.label.review")),
      routerLink: "review-document",
      selected: true,
      tooltip: "",
      transient: true,
      i18n: "navigation.label.edit.document.review"
    }
  }
}
