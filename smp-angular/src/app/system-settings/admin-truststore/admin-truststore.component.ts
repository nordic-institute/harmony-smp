import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {CertificateRo} from "../user/certificate-ro.model";
import {AdminTruststoreService} from "./admin-truststore.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EntityStatus} from "../../common/model/entity-status.model";


@Component({
  moduleId: module.id,
  templateUrl: './admin-truststore.component.html',
  styleUrls: ['./admin-truststore.component.css']
})
export class AdminTruststoreComponent implements AfterViewInit {
  displayedColumns: string[] = ['alias'];
  dataSource: MatTableDataSource<CertificateRo> = new MatTableDataSource();
  selected?: CertificateRo;

  trustedCertificateList: CertificateRo[];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private truststoreService: AdminTruststoreService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {

    truststoreService.onTruststoreUpdatedEvent().subscribe(updatedTruststore => {
        this.updateTruststoreCertificates(updatedTruststore);
      }
    );

    truststoreService.onTruststoreEntryUpdatedEvent().subscribe(updatedCertificate => {
        this.updateTruststoreCertificate(updatedCertificate);
      }
    );
    truststoreService.getTruststoreData();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  updateTruststoreCertificates(truststoreCertificates: CertificateRo[]) {
    this.trustedCertificateList = truststoreCertificates
    this.dataSource.data = this.trustedCertificateList;
  }

  updateTruststoreCertificate(certificateRo: CertificateRo) {

    if (certificateRo == null) {
      return;
    }

    if (certificateRo.status == EntityStatus.NEW) {
      this.trustedCertificateList.push(certificateRo)
      this.selected = certificateRo;
      this.alertService.success("Certificate: [" + certificateRo.certificateId + "] with alias [" + certificateRo.alias + "] is imported!");
    } else if (certificateRo.status == EntityStatus.REMOVED) {
      this.alertService.success("Certificate: [" + certificateRo.certificateId + "] with alias [" + certificateRo.alias + "] is removed!");
      this.selected = null;
      this.trustedCertificateList = this.trustedCertificateList.filter(item => item.alias !== certificateRo.alias)
    } else if (certificateRo.status == EntityStatus.ERROR) {
      this.alertService.error("ERROR: " + certificateRo.actionMessage);
    }
    this.dataSource.data = this.trustedCertificateList;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  public certificateSelected(selected: CertificateRo) {
    this.selected = selected;
  }


  uploadCertificate(event) {
    const file = event.target.files[0];
    this.truststoreService.uploadCertificate$(file);
  }

  onDeleteSelectedCertificateClicked() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete certificate " + this.selected.alias + " from truststore",
        description: "Action will permanently delete certificate from truststore! Do you wish to continue?"
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

}
