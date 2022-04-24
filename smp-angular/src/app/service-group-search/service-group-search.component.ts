///<reference path="../smp.constants.ts"/>
import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog} from '@angular/material/dialog';
import {AlertMessageService} from '../common/alert-message/alert-message.service';
import {ServiceGroupSearchController} from './service-group-search-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {GlobalLookups} from "../common/global-lookups";

@Component({
  moduleId: module.id,
  templateUrl: './service-group-search.component.html',
  styleUrls: ['./service-group-search.component.css']
})
export class ServiceGroupSearchComponent implements OnInit {

  @ViewChild('rowExtensionAction' , { static: true }) rowExtensionAction: TemplateRef<any>
  @ViewChild('rowSMPUrlLinkAction', { static: true }) rowSMPUrlLinkAction: TemplateRef<any>
  @ViewChild('rowActions', { static: true }) rowActions: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  serviceGroupSearchController: ServiceGroupSearchController;
  filter: any = {};
  contextPath: string = location.pathname.substring(0, location.pathname.length - 3); // remove /ui s
  baseUrl: string = SmpConstants.REST_PUBLIC_SEARCH_SERVICE_GROUP;

  constructor(protected lookups: GlobalLookups, protected http: HttpClient, protected alertService: AlertMessageService, public dialog: MatDialog) {

  }

  ngOnDestroy() {

  }

  ngOnInit() {

    this.serviceGroupSearchController = new ServiceGroupSearchController(this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Metadata size',
        prop: 'serviceMetadata.length',
        width: 80,
        maxWidth: 120,
        showInitially: true,
      },
      {
        name: 'Participant scheme',
        prop: 'participantScheme',
        maxWidth: 300,
        showInitially: true,
      },
      {
        name: 'Participant identifier',
        prop: 'participantIdentifier',
        showInitially: true,
      },
      {
        cellTemplate: this.rowSMPUrlLinkAction,
        name: 'OASIS ServiceGroup URL',
        width: 150,
        maxWidth: 250,
        sortable: false,
        showInitially: true,
      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => col.showInitially);
    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Metadata size", "Participant scheme", "Participant identifier", "OASIS ServiceGroup URL"].indexOf(col.name) != -1
    });
  }

  createServiceGroupURL(row: any){
    return encodeURIComponent((!row.participantScheme? '' : row.participantScheme)+'::'+row.participantIdentifier);
  }

  createServiceMetadataURL(row: any, rowSMD: any){
    return encodeURIComponent((!row.participantScheme? '': row.participantScheme)+'::'+row.participantIdentifier)+'/services/'+ encodeURIComponent((!rowSMD.documentIdentifierScheme?'':rowSMD.documentIdentifierScheme)+'::'+rowSMD.documentIdentifier);
  }

  details(row: any) {
    this.serviceGroupSearchController.showDetails(row);

  }
}
