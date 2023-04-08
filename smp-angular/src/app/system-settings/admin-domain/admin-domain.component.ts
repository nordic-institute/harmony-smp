import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {AdminDomainService} from "./admin-domain.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EntityStatus} from "../../common/model/entity-status.model";
import {DomainRo} from "../domain/domain-ro.model";
import {AdminKeystoreService} from "../admin-keystore/admin-keystore.service";
import {CertificateRo} from "../user/certificate-ro.model";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";


@Component({
  moduleId: module.id,
  templateUrl: './admin-domain.component.html',
  styleUrls: ['./admin-domain.component.css']
})
export class AdminDomainComponent implements OnInit, AfterViewInit, BeforeLeaveGuard {
  displayedColumns: string[] = ['domainCode'];
  dataSource: MatTableDataSource<DomainRo> = new MatTableDataSource();
  selected?: DomainRo;
  domainList: DomainRo[];
  keystoreCertificates: CertificateRo[];


  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private domainService: AdminDomainService,
              private keystoreService: AdminKeystoreService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {


    domainService.onDomainUpdatedEvent().subscribe(updatedTruststore => {
        this.updateDomainList(updatedTruststore);
      }
    );
    domainService.onDomainEntryUpdatedEvent().subscribe(updatedCertificate => {
        this.updateDomain(updatedCertificate);
      }
    );

    keystoreService.onKeystoreUpdatedEvent().subscribe(keystoreCertificates => {
        this.keystoreCertificates = keystoreCertificates;
      }
    );
    domainService.getDomains();
    keystoreService.getKeystoreData();
  }

  ngOnInit(): void {
    // filter predicate for search the domain
    this.dataSource.filterPredicate =
      (data: DomainRo, filter: string) => {
        return !filter || -1 != data.domainCode.toLowerCase().indexOf(filter.trim().toLowerCase())
      };
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  updateDomainList(domainList: DomainRo[]) {
    this.domainList = domainList
    this.dataSource.data = this.domainList;
  }

  updateDomain(domain: DomainRo) {

    if (domain == null) {
      return;
    }

    if (domain.status == EntityStatus.NEW) {
      this.domainList.push(domain)
      this.selected = domain;
      this.alertService.success("Domain: [" + domain.domainCode + "] was created!");
    } else if (domain.status == EntityStatus.REMOVED) {
      this.alertService.success("Domain: [" + domain.domainCode + "]  is removed!");
      this.selected = null;
      this.domainList = this.domainList.filter(item => item.domainCode !== domain.domainCode)
    } else if (domain.status == EntityStatus.ERROR) {
      this.alertService.error("ERROR: " + domain.actionMessage);
    }
    this.dataSource.data = this.domainList;
  }

  applyDomainFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  onCreateDomainClicked() {

  }

  onDeleteSelectedDomainClicked() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete domain " + this.selected.domainCode + " from DomiSMP",
        description: "Action will permanently delete domain! Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteDomain(this.selected);
      }
    });
  }

  deleteDomain(domain: DomainRo) {

  }

  public domainSelected(selected: DomainRo) {
    this.selected = selected;
  }

  isDirty(): boolean {
    return false;
  }


}
