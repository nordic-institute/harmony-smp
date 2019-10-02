import {ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AlertService} from '../alert/alert.service';
import {ServiceGroupEditController} from './service-group-edit-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {SearchTableEntityStatus} from "../common/search-table/search-table-entity-status.model";
import {SearchTableComponent} from "../common/search-table/search-table.component";
import {GlobalLookups} from "../common/global-lookups";
import {SecurityService} from "../security/security.service";

@Component({
  moduleId: 'edit',
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
  contextPath: string = location.pathname.substring(0, location.pathname.length - 3); // remove /ui s

  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {

    // if smp admin it needs to have update user list for detail dialog!
    if (this.securityService.isCurrentUserSMPAdmin() || this.securityService.isCurrentUserServiceGroupAdmin()) {
      this.lookups.refreshUserLookup();
      this.lookups.refreshApplicationConfiguration();
    }
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
      data: {edit: false, serviceGroup: row, metadata: this.serviceGroupEditController.newServiceMetadataRow()}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        const status = row.status === SearchTableEntityStatus.PERSISTED
          ? SearchTableEntityStatus.UPDATED
          : row.status;

        let data = formRef.componentInstance.getCurrent();
        row.serviceMetadata.push(data);
        this.searchTable.updateTableRow(rowNumber, row, status);
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

  onEditMetadataRow(serviceGroupRow: any,metaDataRow: any) {
    let metadataRowNumber = serviceGroupRow.serviceMetadata.indexOf(metaDataRow);

    const formRef: MatDialogRef<any> = this.serviceGroupEditController.newMetadataDialog({
      data: {edit: true, serviceGroup: serviceGroupRow, metadata: metaDataRow}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {

        // method isServiceMetaDataChanged must be called before getCurrent!
        let isChanged=formRef.componentInstance.isServiceMetaDataChanged();
        if (!isChanged ){
          // nothing to save
          return;
        }

        let statusMetadata =metaDataRow.status === SearchTableEntityStatus.PERSISTED
          ? SearchTableEntityStatus.UPDATED
          : metaDataRow;


        metaDataRow.status = statusMetadata;
        metaDataRow  = {...formRef.componentInstance.getCurrent()};

        serviceGroupRow.serviceMetadata [metadataRowNumber] = {...metaDataRow };
        // change reference to fire table update
        serviceGroupRow.serviceMetadata = [...serviceGroupRow.serviceMetadata]

        // set row as updated
        const status = serviceGroupRow.status === SearchTableEntityStatus.PERSISTED
          ? SearchTableEntityStatus.UPDATED
          : serviceGroupRow.status;
        serviceGroupRow.status = status;

        this.changeDetector.detectChanges();

      }
    });
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



  // for dirty guard...
  isDirty (): boolean {
    return this.searchTable.isDirty();
  }

  createServiceGroupURL(row: any){
    return encodeURIComponent((!row.participantScheme? '' : row.participantScheme)+'::'+row.participantIdentifier);
  }

  createServiceMetadataURL(row: any, rowSMD: any){
    return encodeURIComponent((!row.participantScheme? '': row.participantScheme)+'::'+row.participantIdentifier)+'/services/'+ encodeURIComponent((!rowSMD.documentIdentifierScheme?'':rowSMD.documentIdentifierScheme)+'::'+rowSMD.documentIdentifier);
  }
}
