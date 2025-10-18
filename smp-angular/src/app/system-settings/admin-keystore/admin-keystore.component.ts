import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {AdminKeystoreService} from "./admin-keystore.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {EntityStatus} from "../../common/enums/entity-status.enum";
import {KeystoreImportDialogComponent} from "./keystore-import-dialog/keystore-import-dialog.component";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {lastValueFrom, Subscription} from "rxjs";
import {CertificateRo} from "../../common/model/certificate-ro.model";
import {TranslateService} from "@ngx-translate/core";
import {SmpTableColDef} from "../../common/components/smp-table/smp-table-coldef.model";

@Component({
    templateUrl: './admin-keystore.component.html',
    styleUrls: ['./admin-keystore.component.css'],
    standalone: false
})
export class AdminKeystoreComponent implements OnInit, OnDestroy, BeforeLeaveGuard {
  displayedColumns: string[] = ['alias', 'entry-type'];
  dataSource: MatTableDataSource<CertificateRo> = new MatTableDataSource();
  keystoreCertificates: CertificateRo[];
  selected?: CertificateRo;
  columns: SmpTableColDef[];

  private updateKeystoreCertificatesSub: Subscription = Subscription.EMPTY;
  private updateKeystoreEntriesSub: Subscription = Subscription.EMPTY;

  tooltipKeyPair = '';
  tooltipCertificate = '';

  constructor(private keystoreService: AdminKeystoreService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private translateService: TranslateService) {

    this.translateService.get("admin.keystore.label.key.pair").subscribe(title => this.tooltipKeyPair = title);
    this.translateService.get("admin.keystore.label.certificate").subscribe(title => this.tooltipCertificate = title);

    this.columns = [
      {
        columnDef: 'alias',
        header: 'admin.keystore.label.alias',
        class: (row: CertificateRo) => ({ "datatable-row-error": row.invalid }),
        tooltip: (row: CertificateRo) => row?.certificateId,
        cell: (row: CertificateRo) => row.alias
      } as SmpTableColDef,
      {
        columnDef: 'entry-type',
        header: 'admin.keystore.label.type',
        tooltip: (row: CertificateRo) => !!row.containingKey ? this.tooltipKeyPair: this.tooltipCertificate,
        icon: (row: CertificateRo) => !!row.containingKey ? "key": "article",
        class: (row: CertificateRo) => ({ "datatable-row-error": row.invalid }),
        cell: (row: CertificateRo) => "",
        style: "max-width: 80px; width: 50px; display: flex; justify-content: center;"
      } as SmpTableColDef
    ];

    this.updateKeystoreCertificatesSub = keystoreService.onKeystoreUpdatedEvent().subscribe(keystoreCertificates => {
        this.updateKeystoreCertificates(keystoreCertificates);
      }
    );

    this.updateKeystoreEntriesSub = keystoreService.onKeystoreEntryUpdatedEvent().subscribe(updatedCertificate => {
        this.updateKeystoreEntries(updatedCertificate);
      }
    );
    keystoreService.getKeystoreData();
  }

  ngOnInit(): void {
    // filter predicate for search the domain
    this.dataSource.filterPredicate =
      (data: CertificateRo, filter: string) => {
        return !filter || -1 != data.alias.toLowerCase().indexOf(filter.trim().toLowerCase())
      };
  }
  ngOnDestroy(): void {
    this.updateKeystoreCertificatesSub.unsubscribe();
    this.updateKeystoreEntriesSub.unsubscribe();
  }

  updateKeystoreCertificates(keystoreCertificates: CertificateRo[]) {
    this.keystoreCertificates = keystoreCertificates;
    this.dataSource.data = this.keystoreCertificates;
  }

  async updateKeystoreEntries(certificateRos: CertificateRo[]) {
    if (certificateRos == null || certificateRos.length == 0) {
      return;
    }
    let dataAdded: string[] = []
    let dataDeleted: string[] = []
    let errorsDetected: string[] = []

    certificateRos.forEach((certificateRo) => {

      if (certificateRo.status == EntityStatus.NEW) {
        this.keystoreCertificates.push(certificateRo)
        this.selected = certificateRo;
        dataAdded.push( "<li>" + certificateRo.alias + " - " + certificateRo.certificateId + "</li>");
      } else if (certificateRo.status == EntityStatus.REMOVED) {
        dataDeleted.push( "<li>" + certificateRo.alias + " - " + certificateRo.certificateId + "</li>");
        this.keystoreCertificates = this.keystoreCertificates.filter(item => item.alias !== certificateRo.alias)
      } else if (certificateRo.status == EntityStatus.ERROR) {
        errorsDetected.push(certificateRo.actionMessage);
      }
    });
    let msg = dataAdded.length > 0 ? await lastValueFrom(this.translateService.get("admin.keystore.success.certificates.added", {data: "<ul>"+dataAdded+ "</ul>"})) : "";
    msg += dataDeleted.length > 0 ? await lastValueFrom(this.translateService.get("admin.keystore.success.certificates.deleted", {data: "<ul>"+dataDeleted + "</ul>"})) : "";
    msg += errorsDetected.length > 0 ? await lastValueFrom(this.translateService.get("admin.keystore.success.errors.detected", {errors: errorsDetected})) : "";

    this.alertService.success(msg, false, 4, true);

    this.selected = null;
    this.dataSource.data = this.keystoreCertificates;
    // show the last page
    if(this.dataSource.paginator) {
      this.dataSource.paginator.lastPage();
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

  openImportKeystoreDialog() {
    const formRef: MatDialogRef<any> = this.dialog.open(KeystoreImportDialogComponent);
    formRef.afterClosed().subscribe(result => {
      if (result) {
        // import
      }
    });
  }

  async onDeleteSelectedCertificateClicked() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("admin.keystore.delete.confirmation.dialog.title", {alias: this.selected.alias})),
        description: await lastValueFrom(this.translateService.get("admin.keystore.delete.confirmation.dialog.description"))
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteCertificateFromTruststore(this.selected.alias);
      }
    });
  }

  deleteCertificateFromTruststore(alias: string) {
    this.keystoreService.deleteEntryFromKeystore(alias);
  }

  isDirty(): boolean {
    return false;
  }
}
