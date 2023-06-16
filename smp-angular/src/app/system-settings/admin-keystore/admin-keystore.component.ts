import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {CertificateRo} from "../user/certificate-ro.model";
import {AdminKeystoreService} from "./admin-keystore.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {EntityStatus} from "../../common/enums/entity-status.enum";
import {KeystoreImportDialogComponent} from "./keystore-import-dialog/keystore-import-dialog.component";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {Subscription} from "rxjs";


@Component({
  moduleId: module.id,
  templateUrl: './admin-keystore.component.html',
  styleUrls: ['./admin-keystore.component.css']
})
export class AdminKeystoreComponent implements OnInit, OnDestroy, AfterViewInit, BeforeLeaveGuard {
  displayedColumns: string[] = ['alias'];
  dataSource: MatTableDataSource<CertificateRo> = new MatTableDataSource();
  keystoreCertificates: CertificateRo[];
  selected?: CertificateRo;

  private updateKeystoreCertificatesSub: Subscription = Subscription.EMPTY;
  private updateKeystoreEntriesSub: Subscription = Subscription.EMPTY;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private keystoreService: AdminKeystoreService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {

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

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  updateKeystoreCertificates(keystoreCertificates: CertificateRo[]) {
    this.keystoreCertificates = keystoreCertificates;
    this.dataSource.data = this.keystoreCertificates;
  }

  updateKeystoreEntries(certificateRos: CertificateRo[]) {

    if (certificateRos == null || certificateRos.length == 0) {
      return;
    }
    let aliasAdded: string[] = []
    let aliasDeleted: string[] = []
    let errorsDetected: string[] = []

    certificateRos.forEach((certificateRo) => {

      if (certificateRo.status == EntityStatus.NEW) {
        this.keystoreCertificates.push(certificateRo)
        this.selected = certificateRo;
        aliasAdded.push(certificateRo.alias);
      } else if (certificateRo.status == EntityStatus.REMOVED) {
        aliasDeleted.push(certificateRo.alias);

        this.keystoreCertificates = this.keystoreCertificates.filter(item => item.alias !== certificateRo.alias)
      } else if (certificateRo.status == EntityStatus.ERROR) {
        errorsDetected.push(certificateRo.actionMessage);
      }
    });
    let msg = aliasAdded.length > 0 ? "Certificates added [" + aliasAdded + "]." : "";
    msg += aliasDeleted.length > 0 ? "Certificates deleted [" + aliasDeleted + "]" : "";
    msg += errorsDetected.length > 0 ? "Errors detected [" + errorsDetected + "]" : "";

    this.alertService.success(msg);

    this.selected = null;
    this.dataSource.data = this.keystoreCertificates;
    // show the last page
    this.paginator.lastPage();

  }


  applyKeyAliasFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  public certificateSelected(selected: CertificateRo) {
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

  onDeleteSelectedCertificateClicked() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete key [" + this.selected.alias + "] from keystore",
        description: "Action will permanently delete key from keystore! <br/><br/>Do you wish to continue?"
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
