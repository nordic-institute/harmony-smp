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

@Component({
  moduleId: module.id,
  templateUrl:'./service-group-edit.component.html',
  styleUrls: ['./service-group-edit.component.css']
})
export class ServiceGroupEditComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('rowSMPUrlLinkAction') rowSMPUrlLinkAction: TemplateRef<any>

  columnPicker: ColumnPicker = new ColumnPicker();
  serviceGroupEditController: ServiceGroupEditController;
  filter: any = {};
  baseUrl: string = SmpConstants.REST_EDIT;

  userObserver:  Observable< SearchTableResult> ;
  domainObserver:  Observable< SearchTableResult> ;
  userlist: Array<UserRo> = [];

  constructor(protected http: HttpClient, protected alertService: AlertService, public dialog: MatDialog) {

    this.userObserver = this.http.get<SearchTableResult>(SmpConstants.REST_USER);
    this.userObserver.subscribe((users: SearchTableResult) => {
      this.userlist = new Array(users.serviceEntities.length)
        .map((v, index) => users.serviceEntities[index] as UserRo);

      this.userlist = users.serviceEntities.map(serviceEntity => {
        return {...<UserRo>serviceEntity}
      });
    });
  }

  ngOnInit() {
    this.serviceGroupEditController = new ServiceGroupEditController(this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Metadata size',
        prop: 'serviceMetadata.length',
        width: 80,
        maxWidth: 120
      },
      {
        name: 'Owners size',
        prop: 'users.length',
        width: 80,
        maxWidth: 120
      },
      {
        name: 'Participant scheme',
        prop: 'participantScheme',
        width: 250,
        maxWidth: 300
      },
      {
        name: 'Participant identifier',
        prop: 'participantIdentifier',
      },
      {
        cellTemplate: this.rowSMPUrlLinkAction,
        name: 'OASIS ServiceGroup URL',
        width: 150,
        maxWidth: 250,
        sortable: false
      },

    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Metadata size", 'Owners size', "Participant scheme", "Participant identifier", "OASIS ServiceGroup URL"].indexOf(col.name) != -1
    });
  }

  metadataRowButtonAction(row: any){
    this.serviceGroupEditController.showMetadataList(row);
  }

  details(row: any) {
    this.serviceGroupEditController.showDetails(row);

  }

  onEditMetadataRow(row:any){
    alert("edit" + row);
  }
  onDeleteMetadataRowActionClicked(row:any){
    alert("delete" + row);
  }
}
