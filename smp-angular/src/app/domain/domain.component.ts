import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog} from '@angular/material';

import {AlertService} from '../alert/alert.service';
import {DomainController} from './domain-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableComponent} from "../common/search-table/search-table.component";
import {SecurityService} from "../security/security.service";

@Component({
  moduleId: module.id,
  templateUrl:'./domain.component.html',
  styleUrls: ['./domain.component.css']
})
export class DomainComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;

  baseUrl = SmpConstants.REST_DOMAIN;
  columnPicker: ColumnPicker = new ColumnPicker();
  domainController: DomainController;
  filter: any = {};


  constructor(public securityService: SecurityService,
              protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertService,
              public dialog: MatDialog) {
  }

  ngOnInit() {
    this.domainController = new DomainController(this.http, this.lookups, this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Domain code',
        prop: 'domainCode',
        width: 275

      },
      {
        name: 'SML Domain',
        prop: 'smlSubdomain',
        width: 275
      },
      {
        name: 'SML SMP Id',
        prop: 'smlSmpId',
        width: 120
      },
      {
        name: 'ClientCert Header',
        prop: 'smlClientCertHeader',
      },
      {
        name: 'ClientCert Alias',
        prop: 'smlClientKeyAlias',
      },

      {
        name: 'Signature CertAlias',
        prop: 'signatureKeyAlias',
        width: 120
      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Domain code", "SML Domain", "SML SMP Id", "ClientCert Header", "ClientCert Alias", "Signature CertAlias"].indexOf(col.name) != -1
    });
  }

  details(row: any) {
    this.domainController.showDetails(row);
  }

  // for dirty guard...
  isDirty (): boolean {
    return this.searchTable.isDirty();
  }
}
