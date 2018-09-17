import {Component, TemplateRef, ViewChild} from "@angular/core";
import {ColumnPickerBase} from "../common/column-picker/column-picker-base";
import {MdDialog, MdDialogRef} from "@angular/material";
import {ServicegroupDetailsDialogComponent} from "./servicegroup-details-dialog/servicegroup-details-dialog.component";
import {Http} from "@angular/http";
import {AlertService} from "../alert/alert.service";
import {ServiceGroupController} from "./servicegroup-controller";

@Component({
  moduleId: module.id,
  templateUrl:'./servicegroup.component.html',
  styleUrls: ['./servicegroup.component.css']
})
export class ServiceGroupComponent {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>
  @ViewChild('rowActions') rowActions: TemplateRef<any>;

  columnPicker: ColumnPickerBase = new ColumnPickerBase();
  serviceGroupController: ServiceGroupController;
  filter: any = {};

  constructor(protected http: Http, protected alertService: AlertService, public dialog: MdDialog) {
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
