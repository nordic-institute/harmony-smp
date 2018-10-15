import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AlertService} from '../alert/alert.service';
import {ServiceGroupController} from './service-group-controller';
import {HttpClient} from '@angular/common/http';

@Component({
  moduleId: module.id,
  templateUrl:'./service-group.component.html',
  styleUrls: ['./service-group.component.css']
})
export class ServiceGroupComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>
  @ViewChild('rowActions') rowActions: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  serviceGroupController: ServiceGroupController;
  filter: any = {};

  constructor(protected http: HttpClient, protected alertService: AlertService, public dialog: MatDialog) {
  }

  ngOnInit() {
    this.serviceGroupController = new ServiceGroupController(this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Participant Id',
        prop: 'serviceGroupROId.participantId',
        width: 275
      },
      {
        name: 'Participant schema',
        prop: 'serviceGroupROId.participantSchema',
      },
      {
        name: 'Domain',
        prop: 'domain',
      },
      {
        cellTemplate: this.rowMetadataAction,
        name: 'Matadata',
        width: 80,
        sortable: false
      },
      {
        cellTemplate: this.rowExtensionAction,
        name: 'Extesion',
        width: 80,
        sortable: false
      }
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Participant Id", "Participant schema", "Domain", "Matadata", "Extesion"].indexOf(col.name) != -1
    });
  }

  extensionRowButtonAction(row: any){
    this.serviceGroupController.showExtension(row);
  }

  metadataRowButtonAction(row: any){
    this.serviceGroupController.showMetadataList(row);
  }

  details(row: any) {
    this.serviceGroupController.showDetails(row);

  }
}
