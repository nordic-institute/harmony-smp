import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material';

import {AlertService} from '../alert/alert.service';
import {DomainController} from './domain-controller';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {GlobalLookups} from "../common/global-lookups";
import {SearchTableComponent} from "../common/search-table/search-table.component";
import {SecurityService} from "../security/security.service";
import {DomainRo} from "./domain-ro.model";
import {ConfirmationDialogComponent} from "../common/confirmation-dialog/confirmation-dialog.component";
import {SearchTableEntityStatus} from "../common/search-table/search-table-entity-status.model";
import {KeystoreEditDialogComponent} from "./keystore-edit-dialog/keystore-edit-dialog.component";

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

    // if system admin refresh certificate list!
    if (this.securityService.isCurrentUserSystemAdmin()) {
      this.lookups.refreshCertificateLookup();
    }
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
        name: 'ClientCert Alias',
        prop: 'smlClientKeyAlias',
      },

      {
        name: 'Signature CertAlias',
        prop: 'signatureKeyAlias',
        width: 120
      },
      {
        name: 'Is SML Registered',
        prop: 'smlRegistered',
        width: 120
      },
      {
        name: 'SML BueCoat Auth.',
        prop: 'smlBlueCoatAuth',
        width: 120
      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Domain code', 'SML Domain', 'SML SMP Id', 'ClientCert Alias', 'Signature CertAlias','Is SML Registered','SML BueCoat Auth.'].indexOf(col.name) != -1
    });
  }

  details(row: any) {
    this.domainController.showDetails(row);
  }

  // for dirty guard...
  isDirty (): boolean {
    return this.searchTable.isDirty();
  }



  enableSMLRegister(): boolean {
    if (this.searchTable.selected.length !== 1) {
      return false;
    }
    let domainRo =  (this.searchTable.selected[0] as DomainRo);
    // entity must be first persisted in order to be enabled to registering to SML
    return !domainRo.smlRegistered && domainRo.status !==SearchTableEntityStatus.NEW;
  }

  enableSMLUnregister(): boolean {
    if (this.searchTable.selected.length !== 1) {
      return false;
    }
    let domainRo =  (this.searchTable.selected[0] as DomainRo);
    // entity must be first persisted in order to be enabled to registering to SML
    return domainRo.smlRegistered && domainRo.status !==SearchTableEntityStatus.NEW;
  }

  smlUnregisterSelectedDomain() {
    if (this.searchTable.selected.length !== 1) {
      return false;
    }

    let domainRo =  (this.searchTable.selected[0] as DomainRo);

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Unregister domain from SML!",
        description: "Action will unregister domain: "+domainRo.domainCode +" and all its service groups from SML. Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        domainRo.smlRegistered=false;
      }
    })
  }

  smlRegisterSelectedDomain() {
    if (this.searchTable.selected.length !== 1) {
      return false;
    }

    let domainRo =  (this.searchTable.selected[0] as DomainRo);

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Register domain to SML!",
        description: "Action will register domain: "+domainRo.domainCode +" and all its service groups to SML. Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        domainRo.smlRegistered=true;
      }
    })
  }

  openEditKeystoreDialog() {
    const formRef: MatDialogRef<any> = this.dialog.open(KeystoreEditDialogComponent);
    formRef.afterClosed().subscribe(result => {
      if (result) {
        // close
      }
    });
  }
}
