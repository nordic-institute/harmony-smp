import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {ColumnPicker} from '../../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';

import {AlertMessageService} from '../../common/alert-message/alert-message.service';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../../smp.constants";
import {GlobalLookups} from "../../common/global-lookups";
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {SecurityService} from "../../security/security.service";
import {EntityStatus} from "../../common/model/entity-status.model";

@Component({
  moduleId: module.id,
  templateUrl: './extension.component.html',
  styleUrls: ['./extension.component.css']
})
export class ExtensionComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('certificateAliasTemplate') certificateAliasColumn: TemplateRef<any>;
  @ViewChild('domainCodeColumnTemplate') domainCodeColumnTemplate: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;


  baseUrl = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE;
  columnPicker: ColumnPicker = new ColumnPicker();
  filter: any = {};

  constructor(public securityService: SecurityService,

              protected lookups: GlobalLookups,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {

    // check application settings


  }

  ngOnInit() {

  }

  initColumns() {
    this.columnPicker.allColumns = [
      {
        name: 'Domain code',
        title: "Unique domain code.",
        prop: 'domainCode',
        showInitially: true,
        cellTemplate: this.domainCodeColumnTemplate,
        width: 250

      },
      {
        name: 'SML Domain',
        title: "Informative: SML domain name.",
        prop: 'smlSubdomain',
        showInitially: true,
      },
      {
        name: 'Signature CertAlias',
        title: "Certificate for signing REST responses",
        prop: 'signatureKeyAlias',
        showInitially: true,
        cellTemplate: this.certificateAliasColumn,
        width: 150
      },
      {
        name: 'SML SMP Id',
        title: "SMP identifier for SML integration",
        prop: 'smlSmpId',
        showInitially: true,
        width: 150
      },
      {
        name: 'SML ClientCert Alias',
        prop: 'smlClientKeyAlias',
        showInitially: true,
        cellTemplate: this.certificateAliasColumn,
        width: 150
      },
      {
        name: 'Is SML Registered',
        prop: 'smlRegistered',
        showInitially: true,
        width: 120
      },
      {
        name: 'SML ClientCert Auth.',
        prop: 'smlClientCertAuth',
        showInitially: true,
        width: 130
      },
    ];
    this.searchTable.tableColumnInit();
  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  ngAfterViewInit() {
    this.initColumns();
    // if system admin refresh certificate list!
    if (this.securityService.isCurrentUserSystemAdmin()) {
      this.lookups.refreshCertificateLookup();
    }
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
    } else if (row.status === EntityStatus.NEW) {
      return 'table-row-new';
    } else if (row.status === EntityStatus.UPDATED) {
      return 'table-row-updated';
    } else if (row.status === EntityStatus.REMOVED) {
      return 'deleted';
    }
  }

  aliasCssForDomainCodeClass(domain) {
    /*
    let domainWarning = this.getDomainConfigurationWarning(domain)
    if (!!domainWarning) {
      return 'domainWarning';
    } else if (domain.status === EntityStatus.NEW) {
      return 'table-row-new';
    } else if (domain.status === EntityStatus.UPDATED) {
      return 'table-row-updated';
    } else if (domain.status === EntityStatus.REMOVED) {
      return 'deleted';
    }

     */
  }


  details(row: any) {

  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

}
