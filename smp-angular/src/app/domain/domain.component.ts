import {Component, TemplateRef, ViewChild} from "@angular/core";
import {ColumnPickerBase} from "../common/column-picker/column-picker-base";
import {MdDialog, MdDialogRef} from "@angular/material";

import {Http} from "@angular/http";
import {AlertService} from "../alert/alert.service";
import {DomainController} from "./domain-controller";
;


@Component({
  moduleId: module.id,
  templateUrl:'./domain.component.html',
  styleUrls: ['./domain.component.css'],
  providers: [],
})

export class DomainComponent {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>

  @ViewChild('rowActions') rowActions: TemplateRef<any>;


  columnPicker: ColumnPickerBase = new ColumnPickerBase();

  domainController: DomainController;
  filter: any = {};

  constructor(protected http: Http, protected alertService: AlertService, public dialog: MdDialog) {
  }

  ngOnInit() {
    this.domainController = new DomainController(this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Domain Id',
        prop: 'domainId',
        width: 275
      },
      {
        name: 'ClientCert Header',
        prop: 'bdmslClientCertHeader',
      },
      {
        name: 'ClientCert Alias',
        prop: 'bdmslClientCertAlias',
      },
      {
        name: 'SMP Id',
        prop: 'bdmslSmpId',
        width: 120
      },
      {
        name: 'Signature CertAlias',
        prop: 'signatureCertAlias',
        width: 120
      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Domain Id", "ClientCert Header", "ClientCert Alias", "SMP Id"].indexOf(col.name) != -1
    });

  }



  details(row: any) {
    this.domainController.showDetails(row);

  }
}
