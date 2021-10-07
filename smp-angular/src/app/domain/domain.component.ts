import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';

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
import {SmpInfoService} from "../app-info/smp-info.service";
import {SmpInfo} from "../app-info/smp-info.model";
import {SmlIntegrationService} from "./sml-integration.service";
import {SMLResult} from "./sml-result.model";

@Component({
  moduleId: module.id,
  templateUrl: './domain.component.html',
  styleUrls: ['./domain.component.css']
})
export class DomainComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('signKeyColumnTemplate') signKeyColumnTemplate: TemplateRef<any>;
  @ViewChild('smlKeyColumnTemplate') smlKeyColumnTemplate: TemplateRef<any>;
  @ViewChild('domainCodeColumnTemplate') domainCodeColumnTemplate: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;


  baseUrl = SmpConstants.REST_DOMAIN;
  columnPicker: ColumnPicker = new ColumnPicker();
  domainController: DomainController;
  filter: any = {};
  isSMPIntegrationOn: boolean = false;


  constructor(public securityService: SecurityService,
              protected smpInfoService: SmpInfoService,
              protected smlIntegrationService: SmlIntegrationService,
              protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertService,
              public dialog: MatDialog) {

    // check application settings
    this.smpInfoService.getSmpInfo().subscribe((smpInfo: SmpInfo) => {
        this.isSMPIntegrationOn = smpInfo.smlIntegrationOn;
      }
    );

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
        title: "Unique domain code.",
        cellTemplate: this.domainCodeColumnTemplate,
        width: 250

      },
      {
        name: 'SML Domain',
        title: "Informative: SML domain name.",
        prop: 'smlSubdomain',

      },
      {
        name: 'Signature CertAlias',
        title: "Certificate for signing REST responses",
        cellTemplate: this.signKeyColumnTemplate,
        width: 150
      },

      {
        name: 'SML SMP Id',
        title: "SMP identifier for SML integration",
        prop: 'smlSmpId',
        width: 150
      },
      {
        name: 'SML ClientCert Alias',
        cellTemplate: this.smlKeyColumnTemplate,
        width: 150
      },
      {
        name: 'Is SML Registered',
        prop: 'smlRegistered',
        width: 120
      },
      {
        name: 'SML BueCoat Auth.',
        prop: 'smlBlueCoatAuth',
        width: 130
      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Domain code', 'SML Domain', 'Signature CertAlias', 'SML SMP Id', 'SML ClientCert Alias', 'Is SML Registered', 'SML BueCoat Auth.'].indexOf(col.name) != -1
    });
  }

  certificateAliasExists(alias: string): boolean {
    if (alias) {
      return this.lookups.cachedCertificateAliasList.includes(alias);
    } else {

      return false;
    }
  }

  aliasCssClass(alias: string, row) {
    if (!this.certificateAliasExists(alias)) {
      return 'missingKey';
    } else if (row.status === SearchTableEntityStatus.NEW) {
      return 'table-row-new';
    } else if (row.status === SearchTableEntityStatus.UPDATED) {
      return 'table-row-updated';
    } else if (row.status === SearchTableEntityStatus.REMOVED) {
      return 'deleted';
    }
  }

  aliasCssForDomainCodeClass(domain) {
    let domainWarning = this.getDomainConfigurationWarning(domain)
    if (!!domainWarning) {
      return 'domainWarning';
    } else if (domain.status === SearchTableEntityStatus.NEW) {
      return 'table-row-new';
    } else if (domain.status === SearchTableEntityStatus.UPDATED) {
      return 'table-row-updated';
    } else if (domain.status === SearchTableEntityStatus.REMOVED) {
      return 'deleted';
    }
  }
  getDomainConfigurationWarning(domain: DomainRo) {
    let msg =null;
    if (!domain.signatureKeyAlias) {
      msg = "The domain should have a defined signature CertAlias."
    }
    if (this.lookups.cachedApplicationInfo.smlIntegrationOn) {
      if( !domain.smlSmpId || !domain.smlClientCertHeader){
        msg = (!msg?"": msg+" ") + "For SML integration the SMP SMP ID and SML client certificate must be defined!"
      }
    }
    return msg;
  }

  details(row: any) {
    this.domainController.showDetails(row);
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  enableSMLRegister(): boolean {
    if (!this.selectedOneRow || !this.isSMPIntegrationOn) {
      return false;
    }
    let domainRo = (this.searchTable.selected[0] as DomainRo);

    if (!domainRo.smlClientCertHeader && domainRo.smlBlueCoatAuth) {
      return false;
    }
    if (!domainRo.smlClientKeyAlias && !domainRo.smlBlueCoatAuth) {
      return false;
    }

    if (domainRo.status != SearchTableEntityStatus.PERSISTED) {
      return false;
    }
    // entity must be first persisted in order to be enabled to registering to SML
    return !domainRo.smlRegistered;
  }

  enableSMLUnregister(): boolean {
    if ( !this.selectedOneRow || !this.isSMPIntegrationOn) {
      return false;
    }
    let domainRo = (this.searchTable.selected[0] as DomainRo);

    if (!domainRo.smlClientCertHeader && domainRo.smlBlueCoatAuth) {
      return false;
    }
    if (!domainRo.smlClientKeyAlias && !domainRo.smlBlueCoatAuth) {
      return false;
    }

    if (domainRo.status != SearchTableEntityStatus.PERSISTED) {
      return false;
    }

    // entity must be first persisted in order to be enabled to registering to SML
    return domainRo.smlRegistered;
  }

  get selectedOneRow() : boolean{
    return this.searchTable?.selected.length === 1
  }

  smlUnregisterSelectedDomain() {
    if (!this.selectedOneRow) {
      return false;
    }

    let domainRo = (this.searchTable.selected[0] as DomainRo);

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Unregister domain to SML!",
        description: "Action will unregister domain: " + domainRo.domainCode + " and all its service groups from SML. Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.smlUnregisterDomain(domainRo);
      }
    })
  }

  smlRegisterSelectedDomain() {
    if (this.searchTable.selected.length !== 1) {
      return false;
    }

    let domainRo = (this.searchTable.selected[0] as DomainRo);

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Register domain to SML!",
        description: "Action will register domain: " + domainRo.domainCode + " and all its service groups to SML. Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.smlRegisterDomain(domainRo);
      }
    })
  }

  smlRegisterDomain(domain: DomainRo) {
    this.searchTable.showSpinner=true;
    this.smlIntegrationService.registerDomainToSML$(domain.domainCode).toPromise().then((res: SMLResult) => {
        this.searchTable.showSpinner=false;
        if (res) {
          if (res.success) {
            this.alertService.success("Domain " + domain.domainCode + " registered to sml!");
            this.lookups.refreshDomainLookup();
            domain.smlRegistered = true;
          } else {
            this.alertService.exception('Error occurred while registering domain:' + domain.domainCode, res.errorMessage);
          }
        } else {
          this.alertService.exception('Error occurred while registering domain:' + domain.domainCode, "Unknown error. Check logs.");
        }
      },
      err => {
        this.searchTable.showSpinner=false;
        this.alertService.exception('Error occurred while registering domain:' + domain.domainCode, err);
      }
    )
  }

  smlUnregisterDomain(domain: DomainRo) {
    this.searchTable.showSpinner=true;
    this.smlIntegrationService.unregisterDomainToSML$(domain.domainCode).toPromise().then((res: SMLResult) => {
        this.searchTable.showSpinner=false;
        if (res) {
          if (res.success) {
            this.alertService.success("Domain " + domain.domainCode + " unregistered from sml!");
            this.lookups.refreshDomainLookup();
            domain.smlRegistered = false;
          } else {
            this.alertService.exception('Error occurred while unregistering domain:' + domain.domainCode, res.errorMessage);
          }
        } else {
          this.alertService.exception('Error occurred while registering domain:' + domain.domainCode, "Unknown error. Check logs.");
        }
      }
      ,
      err => {
        this.searchTable.showSpinner=false;
        this.alertService.exception('Error occurred while unregistering domain:' + domain.domainCode, err);
      }
    )

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
