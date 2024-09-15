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
import {ColumnPicker} from '../../column-picker/column-picker.model';
import {MatDialog} from '@angular/material/dialog';

import {AlertMessageService} from '../../alert-message/alert-message.service';
import {HttpClient} from '@angular/common/http';
import {GlobalLookups} from "../../global-lookups";
import {SearchTableComponent} from "../../search-table/search-table.component";
import {SecurityService} from "../../../security/security.service";
import {ReviewTasksController} from "./review-tasks-controller";
import {lastValueFrom} from "rxjs";
import {TranslateService} from "@ngx-translate/core";
import {
  NavigationNode,
  NavigationService
} from "../../../window/sidenav/navigation-model.service";
import {
  EditResourceService
} from "../../../edit/edit-resources/edit-resource.service";
import {
  ReviewDocumentVersionRo
} from "../../model/review-document-version-ro.model";

/**
 * This is a generic alert panel component for previewing alert list
 */
@Component({
  selector: 'review-tasks-panel',
  templateUrl: './review-tasks-panel.component.html',
  styleUrls: ['./review-tasks-panel.component.css']
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
  columnPicker: ColumnPicker = new ColumnPicker();
  reviewTaskController: ReviewTasksController;
  filter: any = {};
  selected: any;

  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
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
    this.columnPicker.allColumns = [
      {
        name: 'Review date',
        title: "Review date",
        prop: 'lastUpdatedOn',
        showInitially: true,
        maxWidth: 200,
        cellTemplate: this.dateTimeColumn,
      },
      {
        name: await lastValueFrom(this.translateService.get("review.edit.panel.label.column.target")),
        prop: 'target',
        maxWidth: 160,
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("review.edit.panel.label.column.version")),
        prop: 'version',
        maxWidth: 60,
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("review.edit.panel.label.column.resource.scheme")),
        prop: 'resourceIdentifierScheme',
        width: 250,
        maxWidth: 250,
        resizable: 'true',
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("review.edit.panel.label.column.resource.value")),
        prop: 'resourceIdentifierValue',
        resizable: 'true',
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("review.edit.panel.label.column.subresource.scheme")),
        prop: 'subresourceIdentifierScheme',
        width: 250,
        maxWidth: 250,
        resizable: 'true',
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("review.edit.panel.label.column.subresource.value")),
        prop: 'subresourceIdentifierValue',
        resizable: 'true',
        showInitially: true,
      },



    ];
    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => col.showInitially);
    this.searchTable.tableColumnInit();
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
    return this.lookups.getDateTimeFormat();
  }

  async onRowDoubleClicked(row: ReviewDocumentVersionRo) {
    // set selected resource
    this.editResourceService.selectedReviewDocument = row;
    let node: NavigationNode = await this.createNewReviewDocumentNavigationNode();
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

