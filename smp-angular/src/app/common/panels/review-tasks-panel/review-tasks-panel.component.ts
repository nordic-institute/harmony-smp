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
import {
  ObjectPropertiesDialogComponent
} from "../../dialogs/object-properties-dialog/object-properties-dialog.component";
import {ReviewTasksController} from "./review-tasks-controller";
import {lastValueFrom} from "rxjs";
import {TranslateService} from "@ngx-translate/core";

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

  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              private translateService: TranslateService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {
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
        maxWidth: 250,
        cellTemplate: this.dateTimeColumn,
      },
      {
        name: await lastValueFrom(this.translateService.get("resource.search.label.column.resource.scheme")),
        prop: 'resourceScheme',
        width: 250,
        maxWidth: 250,
        resizable: 'true',
        showInitially: true,
      },
      {
        name: await lastValueFrom(this.translateService.get("resource.search.label.column.resource.id")),
        prop: 'resourceIdentifier',
        resizable: 'true',
        showInitially: true,
      },
      {
        name: 'Version',
        title: "Version",
        prop: 'version',
        maxWidth: 60,
        cellTemplate: this.credentialType,
        showInitially: true,
      }

    ];
    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => col.showInitially);
    this.searchTable.tableColumnInit();
  }

  ngAfterViewInit() {
    this.initColumns();
  }


  details(row: any) {
    this.dialog.open(ObjectPropertiesDialogComponent, {
      data: {
        title: "Alert details",
        object: row.alertDetails,

      }
    });
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  get dateTimeFormat(): string {
    return this.lookups.getDateTimeFormat();
  }
}
