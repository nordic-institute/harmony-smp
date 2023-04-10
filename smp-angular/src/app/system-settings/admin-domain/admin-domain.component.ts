import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {AdminDomainService} from "./admin-domain.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EntityStatus} from "../../common/model/entity-status.model";
import {DomainRo} from "./domain-ro.model";
import {AdminKeystoreService} from "../admin-keystore/admin-keystore.service";
import {CertificateRo} from "../user/certificate-ro.model";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {ResourceDefinitionRo} from "../admin-extension/resource-definition-ro.model";
import {ExtensionService} from "../admin-extension/extension.service";
import {ExtensionRo} from "../admin-extension/extension-ro.model";
import {MatTabGroup} from "@angular/material/tabs";
import {CancelDialogComponent} from "../../common/dialogs/cancel-dialog/cancel-dialog.component";
import {DomainPanelComponent} from "./domain-panel/domain-panel.component";
import {DomainResourceTypePanelComponent} from "./domain-resource-type-panel/domain-resource-type-panel.component";
import {DomainSmlIntegrationPanelComponent} from "./domain-sml-panel/domain-sml-integration-panel.component";


@Component({
  moduleId: module.id,
  templateUrl: './admin-domain.component.html',
  styleUrls: ['./admin-domain.component.css']
})
export class AdminDomainComponent implements OnInit, AfterViewInit, BeforeLeaveGuard {
  displayedColumns: string[] = ['domainCode'];
  dataSource: MatTableDataSource<DomainRo> = new MatTableDataSource();
  selected?: DomainRo;
  domainList: DomainRo[] = [];
  keystoreCertificates: CertificateRo[] = [];
  domiSMPResourceDefinitions: ResourceDefinitionRo[] = [];

  currenTabIndex: number = 0;
  handleTabClick;


  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  @ViewChild('domainPanelComponent') domainPanelComponent: DomainPanelComponent;
  @ViewChild('domainResourceTypePanelComponent') domainResourceTypePanelComponent: DomainResourceTypePanelComponent;
  @ViewChild('domainSmlIntegrationPanelComponent') domainSmlIntegrationPanelComponent: DomainSmlIntegrationPanelComponent;


  @ViewChild('domainTabs') domainTabs: MatTabGroup;

  constructor(private domainService: AdminDomainService,
              private keystoreService: AdminKeystoreService,
              private extensionService: ExtensionService,
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
    extensionService.onExtensionsUpdatesEvent().subscribe(updatedExtensions => {
        this.updateExtensions(updatedExtensions);
      }
    );

    extensionService.getExtensions();
    domainService.getDomains();
    keystoreService.getKeystoreData();
  }

  updateExtensions(extensions: ExtensionRo[]) {

    let allResourceDefinition: ResourceDefinitionRo[] = [];
    extensions.forEach(ext => allResourceDefinition.push(...ext.resourceDefinitions))

    this.domiSMPResourceDefinitions = allResourceDefinition;
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
    // currenctly  MatTab has only onTabChanged which is a bit to late. Register new listener to  internal
    // _handleClick handler
    this.registerTabClick();
  }

  registerTabClick(): void {
    // Get the handler reference
    this.handleTabClick = this.domainTabs._handleClick;

    this.domainTabs._handleClick = (tab, header, newTabIndex) => {

      if (newTabIndex == this.currenTabIndex) {
        return;
      }

      if (this.isCurrentTabDirty()) {
        let canChangeTab = this.dialog.open(CancelDialogComponent).afterClosed().toPromise<boolean>();
        canChangeTab.then((canChange: boolean) => {
          if (canChange) {
            // reset
            this.resetCurrentTabData()
            this.handleTabClick.apply(this.domainTabs, [tab, header, newTabIndex]);
            this.currenTabIndex = newTabIndex;
            if (this.isNewDomain()){
              this.selected = null;
            }
          }
        });
      } else {
        this.handleTabClick.apply(this.domainTabs, [tab, header, newTabIndex]);
        this.currenTabIndex = newTabIndex;
      }
    }
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
    } else if (domain.status == EntityStatus.UPDATED) {
      // update value in the array
      let itemIndex = this.domainList.findIndex(item => item.domainId == domain.domainId);
      this.domainList[itemIndex] = domain;
      this.selected = domain;
    }
    else if (domain.status == EntityStatus.REMOVED) {
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

  resetUnsavedDataValidation() {
    // current tab not changed - OK to change it
    if (!this.isCurrentTabDirty()) {
      return true;
    }

    let canChangeTab = this.dialog.open(CancelDialogComponent).afterClosed().toPromise<boolean>();
    canChangeTab.then((canChange: boolean) => {
      if (canChange) {
        // reset
        this.resetCurrentTabData()
      }
    });
  }

  onCreateDomainClicked() {
    this.domainTabs.selectedIndex = 0;
    this.selected = this.newDomain();
    this.domainPanelComponent.setFocus();

  }

  public newDomain(): DomainRo {
    return {
      index: null,
      domainCode: '',
      smlSubdomain: '',
      smlSmpId: '',
      smlParticipantIdentifierRegExp: '',
      smlClientCertHeader: '',
      smlClientKeyAlias: '',
      signatureKeyAlias: '',
      status: EntityStatus.NEW,
      smlRegistered: false,
      smlClientCertAuth: false,
    }
  }

  onSaveEvent(domain: DomainRo){
    if (this.isNewDomain()) {
      this.domainService.createDomain(domain);
    } else {
      this.domainService.updateDomain(domain);
    }
  }

  onDiscardNew(){
    this.selected = null;
  }

  onSaveResourceTypesEvent(domain: DomainRo){
    this.domainService.updateDomainResourceTypes(domain);
  }

  onSaveSmlIntegrationDataEvent(domain: DomainRo){
    this.domainService.updateDomainSMLIntegrationData(domain);
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
      this.domainService.deleteDomains(domain);
  }

  public domainSelected(domainSelected: DomainRo) {
    if (this.selected === domainSelected) {
      return;
    }
    if (this.isCurrentTabDirty()) {
      let canChangeTab = this.dialog.open(CancelDialogComponent).afterClosed().toPromise<boolean>();
      canChangeTab.then((canChange: boolean) => {
        if (canChange) {
          // reset
          this.resetCurrentTabData();
          this.selected = domainSelected;
        }
      });
    } else {
      this.selected = domainSelected;
    }
  }


  isDirty(): boolean {
    return  this.isCurrentTabDirty();
  }

  isCurrentTabDirty(): boolean {

    switch (this.currenTabIndex) {
      case 0:
        return this.domainPanelComponent?.isDirty();
      case 1:
        return this.domainResourceTypePanelComponent?.isDirty();
      case 2:
        return this.domainSmlIntegrationPanelComponent?.isDirty();
    }
    return false;
  }

  isNewDomain():boolean{
    return this.selected!=null && !this.selected.domainId
  }


  resetCurrentTabData(): void {

    switch (this.currenTabIndex) {
      case 0:
        this.domainPanelComponent.onResetButtonClicked();
        break;
      case 1:
        this.domainPanelComponent.onResetButtonClicked();
        break
      case 2:
        this.domainSmlIntegrationPanelComponent.onResetButtonClicked();
        break
    }
  }

  get canNotDelete():boolean{
    return !this.selected || this.domainSmlIntegrationPanelComponent.isDomainRegistered || this.isNewDomain()
  }

  get editMode(): boolean {
    return this.isCurrentTabDirty();
  }
}
