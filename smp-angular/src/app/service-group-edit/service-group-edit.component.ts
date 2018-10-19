import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AlertService} from '../alert/alert.service';
import {ServiceGroupEditController} from './service-group-edit-controller';
import {HttpClient} from '@angular/common/http';
import {ServiceGroupDetailsDialogComponent} from "./service-group-details-dialog/service-group-details-dialog.component";
import {SmpConstants} from "../smp.constants";
import {Observable} from "rxjs/internal/Observable";
import {UserRo} from "../user/user-ro.model";
import {SearchTableResult} from "../common/search-table/search-table-result.model";
import {SearchTableEntityStatus} from "../common/search-table/search-table-entity-status.model";
import {SearchTableComponent} from "../common/search-table/search-table.component";
import {ServiceMetadataEditRo} from "./service-metadata-edit-ro.model";
import {ServiceGroupEditRo} from "./service-group-edit-ro.model";
import {GlobalLookups} from "../common/global-lookups";
import {DomainRo} from "../domain/domain-ro.model";

@Component({
  moduleId: module.id,
  templateUrl: './service-group-edit.component.html',
  styleUrls: ['./service-group-edit.component.css']
})
export class ServiceGroupEditComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('rowSMPUrlLinkAction') rowSMPUrlLinkAction: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;

  columnPicker: ColumnPicker = new ColumnPicker();
  serviceGroupEditController: ServiceGroupEditController;
  filter: any = {};
  baseUrl: string = SmpConstants.REST_EDIT;

  userObserver: Observable<SearchTableResult>;
  domainObserver: Observable<SearchTableResult>;
  userlist: Array<UserRo> = [];
  domainlist: Array<any>;

  constructor(protected lookups: GlobalLookups, protected http: HttpClient, protected alertService: AlertService, public dialog: MatDialog) {

    this.userObserver = this.http.get<SearchTableResult>(SmpConstants.REST_USER);
    this.userObserver.subscribe((users: SearchTableResult) => {
      this.userlist = new Array(users.serviceEntities.length)
        .map((v, index) => users.serviceEntities[index] as UserRo);

      this.userlist = users.serviceEntities.map(serviceEntity => {
        return {...<UserRo>serviceEntity}
      });
    });

    this.lookups.getDomainLookupObservable().subscribe((domains: SearchTableResult) => {
      this.domainlist = new Array(domains.serviceEntities.length)
        .map((v, index) => domains.serviceEntities[index] as DomainRo);

      this.domainlist = domains.serviceEntities.map(serviceEntity => {
        return {...<DomainRo>serviceEntity}
      });
    });
  }

  ngOnInit() {


    this.serviceGroupEditController = new ServiceGroupEditController(this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Metadata size',
        prop: 'serviceMetadata.length',
        width: 120,
        maxWidth: 120,
        resizable: "false"
      },
      {
        name: 'Owners size',
        prop: 'users.length',
        width: 120,
        maxWidth: 120,
        resizable: "false"
      },
      {
        name: 'Participant scheme',
        prop: 'participantScheme',
        width: 300,
        maxWidth: 300,
        resizable: "false"
      },
      {
        name: 'Participant identifier',
        prop: 'participantIdentifier',
      },
      {
        cellTemplate: this.rowSMPUrlLinkAction,
        name: 'OASIS ServiceGroup URL',
        width: 250,
        maxWidth: 250,
        resizable: "false",
        sortable: false
      },

    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Metadata size", 'Owners size', "Participant scheme", "Participant identifier", "OASIS ServiceGroup URL"].indexOf(col.name) != -1
    });
  }

  details(row: any) {
    this.serviceGroupEditController.showDetails(row);

  }

  onAddMetadataRow(row: any) {
    let rowNumber = this.searchTable.rows.indexOf(row);

    const formRef: MatDialogRef<any> = this.serviceGroupEditController.newMetadataDialog({
      data: {edit: true, serviceGroup: row, metadata: null}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const status = row.status === SearchTableEntityStatus.PERSISTED
          ? SearchTableEntityStatus.UPDATED
          : row.status;

        let data = this.serviceGroupEditController.newServiceMetadataRow();
        data.documentIdentifier = "aaaaaaaaa";
        data.documentIdentifierScheme = "aaaaaaaaa";
        row.serviceMetadata.push(data);

        this.searchTable.updateTableRow(rowNumber, row, status);
        //this.searchTable.rows[rowNumber] = {...row, status};
        //this.searchTable.rows = [...this.searchTable.rows];
        /*
        const status = row.status === SearchTableEntityStatus.PERSISTED
          ? SearchTableEntityStatus.UPDATED
          : row.status;
        this.rows[rowNumber] = {...formRef.componentInstance.current, status};
        this.rows = [...this.rows];*/

      }
    });

  }

  getRowClass(row) {
    return {
      'table-row-new': (row.status === SearchTableEntityStatus.NEW),
      'table-row-updated': (row.status === SearchTableEntityStatus.UPDATED),
      'deleted': (row.status === SearchTableEntityStatus.REMOVED)
    };
  }

  onEditMetadataRow(metaDataRow: any) {

  }

  onDeleteMetadataRowActionClicked(serviceGroupRow: any, metaDataRow: any) {
    let rowNumber = this.searchTable.rows.indexOf(serviceGroupRow);

    if (metaDataRow.status === SearchTableEntityStatus.NEW) {
      serviceGroupRow.splice(serviceGroupRow.indexOf(metaDataRow), 1);
    } else {
      metaDataRow.status = SearchTableEntityStatus.REMOVED;
      metaDataRow.deleted = true;
      // set row as updated
      const status = serviceGroupRow.status === SearchTableEntityStatus.PERSISTED
        ? SearchTableEntityStatus.UPDATED
        : serviceGroupRow.status;
      serviceGroupRow.status = status;

      // do not do that it updates the whole table
      // this.searchTable.rows[rowNumber] = {...serviceGroupRow, status};
      // this.searchTable.rows = [...this.searchTable.rows];
    }
  }
}
