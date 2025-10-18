import {AfterViewInit, Component, Input, OnDestroy, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {PageEvent} from "@angular/material/paginator";
import {AdminDomainService} from "./admin-domain.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EntityStatus} from "../../common/enums/entity-status.enum";
import {DomainRo} from "../../common/model/domain-ro.model";
import {AdminKeystoreService} from "../admin-keystore/admin-keystore.service";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {ResourceDefinitionRo} from "../admin-extension/resource-definition-ro.model";
import {ExtensionService} from "../admin-extension/extension.service";
import {ExtensionRo} from "../admin-extension/extension-ro.model";
import {MatTabGroup} from "@angular/material/tabs";
import {CancelDialogComponent} from "../../common/dialogs/cancel-dialog/cancel-dialog.component";
import {DomainPanelComponent} from "./domain-panel/domain-panel.component";
import {DomainResourceTypePanelComponent} from "./domain-resource-type-panel/domain-resource-type-panel.component";
import {DomainSmlIntegrationPanelComponent} from "./domain-sml-panel/domain-sml-integration-panel.component";
import {MemberTypeEnum} from "../../common/enums/member-type.enum";
import {firstValueFrom, lastValueFrom, Subscription} from "rxjs";
import {VisibilityEnum} from "../../common/enums/visibility.enum";
import {CertificateRo} from "../../common/model/certificate-ro.model";
import {GlobalLookups} from "../../common/global-lookups";
import {TranslateService} from "@ngx-translate/core";
import {SmpTableColDef} from "../../common/components/smp-table/smp-table-coldef.model";
import {TableResult} from "../../common/model/table-result.model";
import {SmpTableComponent} from "../../common/components/smp-table/smp-table.component";


@Component({
  templateUrl: './admin-domain.component.html',
  styleUrls: ['./admin-domain.component.css'],
  standalone: false
})
export class AdminDomainComponent implements OnInit, OnDestroy, AfterViewInit, BeforeLeaveGuard {
  readonly membershipType: MemberTypeEnum = MemberTypeEnum.DOMAIN;
  displayedColumns: string[] = ['domainCode', 'adminCnt'];
  columns: SmpTableColDef[];
  dataSource: MatTableDataSource<DomainRo> = new MatTableDataSource();
  selected?: DomainRo;
  domainList: DomainRo[] = [];
  keystoreCertificates: CertificateRo[] = [];
  domiSMPResourceDefinitions: ResourceDefinitionRo[] = [];

  currenTabIndex: number = 0;
  handleTabClick = null;

  private domainUpdatedEventSub: Subscription = Subscription.EMPTY;
  private domainEntryUpdatedEventSub: Subscription = Subscription.EMPTY;
  _isLoadingResults = false;
  dataLength: number = 0;
  pageIndex: number = 0;
  pageSize: number = 10;
  filterValue: string;

  warningMessage: string = "";

  @ViewChild('domainPanelComponent') domainPanelComponent: DomainPanelComponent;
  @ViewChild('domainResourceTypePanelComponent') domainResourceTypePanelComponent: DomainResourceTypePanelComponent;
  @ViewChild('domainSmlIntegrationPanelComponent') domainSmlIntegrationPanelComponent: DomainSmlIntegrationPanelComponent;
  @ViewChild('domainTable') domainTable: SmpTableComponent;
  @ViewChild('adminCountColumn', {static: true}) adminCountColumn: TemplateRef<any>;

  @ViewChild('domainTabs') domainTabs: MatTabGroup;

  constructor(private domainService: AdminDomainService,
              private keystoreService: AdminKeystoreService,
              private extensionService: ExtensionService,
              protected lookups: GlobalLookups,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private translateService: TranslateService) {

    this.columns = [
      {
        columnDef: 'domainCode',
        header: 'admin.domain.label.domain.code',
        cell: (row: DomainRo) => row.domainCode,
        class: (row: DomainRo) => ({"datatable-row-error": this.hasRowErrors(row)}),
      } as SmpTableColDef,
      {
        columnDef: 'adminCnt',
        header: 'admin.domain.label.domain.admin.count',
        cell: (row: DomainRo) => row.adminMemberCount?.toString() || '0',
        class: (row: DomainRo) => ({"datatable-row-error": this.hasRowErrors(row)}),
        style: "max-width: 80px; width: 50px; display: flex; justify-content: right;"
      } as SmpTableColDef,
    ];

    this.domainUpdatedEventSub = domainService.onDomainUpdatedEvent()
      .subscribe((result: TableResult<DomainRo>): void => {
          this.updateDomainList(result.serviceEntities, result.count, result.page, result.pageSize);
        }
      );

    this.domainEntryUpdatedEventSub = domainService.onDomainEntryUpdatedEvent()
      .subscribe((updateEntry: DomainRo): void => {
          this.updateDomain(updateEntry);
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
    domainService.getDomains(this.filterValue, this.pageIndex, this.pageSize);
    keystoreService.getKeystoreData();
  }

  ngOnDestroy(): void {
    this.domainUpdatedEventSub.unsubscribe();
    this.domainEntryUpdatedEventSub.unsubscribe();
  }

  updateExtensions(extensions: ExtensionRo[]): void {
    let allResourceDefinition: ResourceDefinitionRo[] = [];
    extensions.forEach(ext => allResourceDefinition.push(...ext.resourceDefinitions))
    this.domiSMPResourceDefinitions = allResourceDefinition;
  }

  get showWarning() {
    return this.hasRowErrors(this.selected);
  }

  async updateShowWarningMessage() {
    let message = await lastValueFrom(this.translateService.get("domain.panel.warning.domain.configuration.prefix"));
    if (!this.selected?.signatureKeyAlias) {
      message += await lastValueFrom(this.translateService.get("domain.panel.warning.domain.configuration.option.signature.key"));
    }
    if (!this.domainResourceTypes(this.selected)?.length) {
      message += await lastValueFrom(this.translateService.get("domain.panel.warning.domain.configuration.option.resource.type"));
    }
    if (!this.selected?.adminMemberCount || this.selected?.adminMemberCount < 1) {
      message += await lastValueFrom(this.translateService.get("domain.panel.warning.domain.configuration.option.admin.member"));
    }
    message += "</ul>"; // No need to translate this part

    this.warningMessage = message;
  }

  domainResourceTypes(domain: DomainRo): ResourceDefinitionRo[] {
    if (!domain || !domain.resourceDefinitions) {
      return [];
    }
    return this.domiSMPResourceDefinitions.filter(resType => domain.resourceDefinitions.includes(resType.identifier))
  }

  hasRowErrors(domain: DomainRo): boolean {
    return !!domain?.domainId && (!this.domainResourceTypes(domain)?.length
      || !domain.signatureKeyAlias
      || !domain.adminMemberCount
      || domain.adminMemberCount < 1)
  }

  ngOnInit(): void {
    // filter predicate for search the domain
    this.dataSource.filterPredicate =
      (data: DomainRo, filter: string) => {
        return !filter || -1 != data.domainCode.toLowerCase().indexOf(filter.trim().toLowerCase())
      };
  }

  ngAfterViewInit() {
    // currently  MatTab has only onTabChanged which is a bit to late. Register new listener to  internal
    // _handleClick handler
    this.registerTabClick();
  }

  registerTabClick(): void {
    if (!this.domainTabs) {
      return;
    }
    // Get the handler reference
    this.handleTabClick = this.domainTabs._handleClick;

    this.domainTabs._handleClick = (tab, header, newTabIndex) => {

      if (newTabIndex == this.currenTabIndex) {
        return;
      }

      if (this.isCurrentTabDirty()) {
        let canChangeTab = firstValueFrom(this.dialog.open(CancelDialogComponent).afterClosed());
        canChangeTab.then((canChange: boolean) => {
          if (canChange) {
            // reset
            this.resetCurrentTabData()
            this.handleTabClick.apply(this.domainTabs, [tab, header, newTabIndex]);
            this.currenTabIndex = newTabIndex;
            if (this.isNewDomain()) {
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

  updateDomainList(domainList: DomainRo[],
                   totalDataSize: number = 0,
                   pageIndex: number = -1,
                   pageSize: number = -1) {
    let currR: DomainRo = this.selected;
    this.selected = null;
    this.domainList = domainList
    this.dataSource.data = this.domainList;

    this.dataLength = totalDataSize;
    if (pageIndex !== -1) {
      this.pageIndex = pageIndex;
    }
    if (pageSize !== -1) {
      this.pageSize = pageSize;
    }

    if (!!currR) {
      this.selected = domainList.find(d =>
        d.domainCode == currR.domainCode);
    }

    if (!this.selected && !!domainList && domainList.length > 0) {
      this.selected = domainList[0];
    }
  }

  async updateDomain(domain: DomainRo) {
    if (domain == null) {
      return;
    }
    this.updateShowWarningMessage();
    if (domain.status == EntityStatus.NEW) {
      this.domainList.push(domain)
      this.selected = domain;
      this.alertService.success(await lastValueFrom(this.translateService.get("admin.domain.success.create", {domainCode: domain.domainCode})));
    } else if (domain.status == EntityStatus.UPDATED) {
      // update value in the array
      let itemIndex = this.domainList.findIndex(item => item.domainId == domain.domainId);
      this.domainList[itemIndex] = domain;
      this.selected = domain;
    } else if (domain.status == EntityStatus.REMOVED) {
      this.alertService.success(await lastValueFrom(this.translateService.get("admin.domain.success.remove", {domainCode: domain.domainCode})));
      this.selected = null;
      this.domainList = this.domainList.filter(item => item.domainCode !== domain.domainCode)
    } else if (domain.status == EntityStatus.ERROR) {
      this.alertService.error(await lastValueFrom(this.translateService.get("admin.domain.error", {actionMessage: domain.actionMessage})));
    }
    this.dataSource.data = this.domainList;

    if (domain.status == EntityStatus.NEW) {
      this.domainTable.lastPage();
    }
  }

  applyDomainFilter(filterValue: string) {
    this.filterValue = filterValue.trim().toLowerCase();
    this.refreshDomainList();
  }

  refreshDomainList() {
    this.domainService.getDomains(this.filterValue,
      this.pageIndex,
      this.pageSize);
  }

  onPageChanged(page: PageEvent) {
    this.pageIndex = page.pageIndex;
    this.pageSize = page.pageSize;
    this.refreshDomainList();
  }


  resetUnsavedDataValidation() {
    // current tab not changed - OK to change it
    if (!this.isCurrentTabDirty()) {
      return true;
    }

    let canChangeTab = firstValueFrom(this.dialog.open(CancelDialogComponent).afterClosed());
    canChangeTab.then((canChange: boolean) => {
      if (canChange) {
        // reset
        this.resetCurrentTabData()
      }
    });
  }

  onCreateDomainClicked() {
    this.selected = this.newDomain();
    if (!this.handleTabClick) {
      this.registerTabClick();
    }
    if (!!this.domainTabs) {
      this.domainTabs.selectedIndex = 0;
      this.domainPanelComponent.setFocus();
    }
  }

  public newDomain(): DomainRo {
    return {
      index: null,
      visibility: VisibilityEnum.Public,
      domainCode: '',
      smlSubdomain: '',
      smlSmpId: '',
      smlParticipantIdentifierRegExp: '',
      smlClientKeyAlias: '',
      signatureKeyAlias: '',
      status: EntityStatus.NEW,
      smlRegistered: false,
      smlClientCertAuth: false,
      adminMemberCount: 0,
    }
  }

  onSaveEvent(domain: DomainRo) {
    if (this.isNewDomain()) {
      this.domainService.createDomain(domain);
    } else {
      this.domainService.updateDomain(domain);
    }
  }

  onDiscardNew() {
    this.selected = null;
  }

  onSaveResourceTypesEvent(domain: DomainRo) {
    this.domainService.updateDomainResourceTypes(domain);
  }

  onSaveSmlIntegrationDataEvent(domain: DomainRo) {
    this.domainService.updateDomainSMLIntegrationData(domain);

  }

  onSavePropertiesDataEvent(domain: DomainRo) {
    this.domainService.updateDomainData(domain);
  }


  onDeleteSelectedDomainClicked() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete domain [" + this.selected.domainCode + "] from DomiSMP",
        description: "Action will permanently delete domain! <br/><br/>Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteDomain(this.selected);
      }
    });
  }

  deleteDomain(domain: DomainRo) {
    this.domainService.deleteDomain(domain);
  }

  public domainSelected(domainSelected: DomainRo) {
    if (domainSelected && !this.handleTabClick) {
      this.registerTabClick();
    }


    if (this.selected == domainSelected) {
      return;
    }

    if (this.isCurrentTabDirty()) {
      let canChangeTab = firstValueFrom(this.dialog.open(CancelDialogComponent).afterClosed());
      canChangeTab.then((canChange: boolean) => {
        if (canChange) {
          // reset
          this.resetCurrentTabData();
          this.updateShowWarningMessage();
          this.selected = domainSelected;
        }
      });
    } else {
      this.updateShowWarningMessage();
      this.selected = domainSelected;
    }
  }


  isDirty(): boolean {
    return this.isCurrentTabDirty();
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

  /**
   * Method checks if domain entity is set and domainId does not exists.
   * @return true if domain is set and domainId does not exists otherwise false
   */
  isNewDomain(): boolean {
    if (!this.selected) {
      return false;
    }
    return !this.selected.domainId
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

  /**
   * The domain can not be deleted if it is  not selected or it is registered in the SML
   * or it is new domain
   */
  get canNotDelete(): boolean {
    return !this.selected || this.isNewDomain() || this.isSelectedSMPRegister;

  }

  /**
   *  Method returns true if the SML integration is enabled and the domain is  registered
   *
   */
  get isSelectedSMPRegister(): boolean {
    return this.isSMLIntegrationEnabled && this.selected?.smlRegistered;
  }

  get isSMLIntegrationEnabled() {
    return !!this.lookups.cachedApplicationConfig?.smlIntegrationOn
  }

  get editMode(): boolean {
    return this.isCurrentTabDirty();
  }


  // this flag is used to trigger data refresh when data is needed.
  //The data can be changed for the user when adding/creating new resource in the edit group
  @Input() set isLoadingResults(value: boolean) {
    this._isLoadingResults = value;
  }

  get isLoadingResults(): boolean {
    return this._isLoadingResults;
  }
}
