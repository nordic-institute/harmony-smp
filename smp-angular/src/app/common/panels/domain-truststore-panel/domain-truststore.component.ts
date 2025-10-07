import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {DomainTruststoreService} from "./domain-truststore.service";
import {lastValueFrom, Subscription} from "rxjs";
import {SmpTableColDef} from "../../components/smp-table/smp-table-coldef.model";
import {CertificateRo} from "../../model/certificate-ro.model";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {AlertMessageService} from "../../alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {EntityStatus} from "../../enums/entity-status.enum";
import {ConfirmationDialogComponent} from "../../dialogs/confirmation-dialog/confirmation-dialog.component";
import {DomainRo} from "../../model/domain-ro.model";


@Component({
    selector: 'domain-truststore-panel',
    templateUrl: './domain-truststore.component.html',
    styleUrls: ['./domain-truststore.component.css'],
    standalone: false
})
export class DomainTruststoreComponent implements OnInit,  OnDestroy, BeforeLeaveGuard {
  displayedColumns: string[] = ['alias'];
  dataSource: MatTableDataSource<CertificateRo> = new MatTableDataSource();
  trustedCertificateList: CertificateRo[];
  selected?: CertificateRo;
  columns: SmpTableColDef[];

  private updateTruststoreCertificatesSub: Subscription = Subscription.EMPTY;
  private updateTruststoreCertificateSub: Subscription = Subscription.EMPTY;

  @Input()
  domain: DomainRo;

  // purpose of this value is to reset the file input after the file is uploaded
  inputFileValue: string = '';

  constructor(private truststoreService: DomainTruststoreService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private translateService: TranslateService) {
    this.columns = [
      {
        columnDef: 'alias',
        header: 'admin.truststore.label.alias',
        tooltip: (row: CertificateRo) => row?.certificateId,
        cell: (row: CertificateRo) => row.alias
      } as SmpTableColDef,
    ];

    this.updateTruststoreCertificatesSub = truststoreService.onTruststoreUpdatedEvent().subscribe(updatedTruststore => {
        this.updateTruststoreCertificates(updatedTruststore);
      }
    );

    this.updateTruststoreCertificateSub = truststoreService.onTruststoreEntryUpdatedEvent().subscribe(updatedCertificate => {
        this.updateTruststoreCertificate(updatedCertificate);
      }
    );
    truststoreService.getTruststoreData();
  }

  ngOnInit(): void {
    // filter predicate for search the domain
    this.dataSource.filterPredicate  =
      (data: CertificateRo, filter: string) => {return !filter || -1!=data.alias.toLowerCase().indexOf(filter.trim().toLowerCase()) };
  }

  ngOnDestroy(): void {
    this.updateTruststoreCertificatesSub.unsubscribe();
    this.updateTruststoreCertificateSub.unsubscribe();
  }

  updateTruststoreCertificates(truststoreCertificates: CertificateRo[]) {
    this.trustedCertificateList = truststoreCertificates
    this.dataSource.data = this.trustedCertificateList;
  }

  async updateTruststoreCertificate(certificateRo: CertificateRo) {

    if (certificateRo == null) {
      return;
    }

    if (certificateRo.status == EntityStatus.NEW) {
      this.trustedCertificateList.push(certificateRo)
      this.selected = certificateRo;
      this.alertService.success(await lastValueFrom(this.translateService.get("admin.truststore.success.import", {
        certificateId: certificateRo.certificateId,
        alias: certificateRo.alias
      })));
    } else if (certificateRo.status == EntityStatus.REMOVED) {
      this.alertService.success(await lastValueFrom(this.translateService.get("admin.truststore.success.remove", {
        certificateId: certificateRo.certificateId,
        alias: certificateRo.alias
      })));
      this.selected = null;
      this.trustedCertificateList = this.trustedCertificateList.filter(item => item.alias !== certificateRo.alias)
    } else if (certificateRo.status == EntityStatus.ERROR) {
      this.alertService.error(await lastValueFrom(this.translateService.get("admin.truststore.error", {actionMessage: certificateRo.actionMessage})));
    }
    this.dataSource.data = this.trustedCertificateList;
    // if new cert is added - go to last page
    if (certificateRo.status == EntityStatus.NEW) {
      if(this.dataSource.paginator) {
        this.dataSource.paginator.lastPage();
      }
    }
  }

  applyCertificateFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  public onCertificateSelected(selected: CertificateRo) {
    this.selected = selected;
  }

  uploadCertificate(event) {
    const file = event.target.files[0];
    this.truststoreService.uploadCertificate$(file);
    // reset the file input
    this.inputFileValue = '';
  }

  async onDeleteSelectedCertificateClicked() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("admin.truststore.delete.confirmation.dialog.title", {alias: this.selected.alias})),
        description: await lastValueFrom(this.translateService.get("admin.truststore.delete.confirmation.dialog.description"))
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteCertificateFromTruststore(this.selected.alias);
      }
    });
  }

  deleteCertificateFromTruststore(alias: string) {
    this.truststoreService.deleteCertificateFromTruststore(alias);
  }

  isDirty(): boolean {
    return false;
  }
}
