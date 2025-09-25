import {Component, Input, OnInit,} from '@angular/core';
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {MatTableDataSource} from "@angular/material/table";
import {GroupRo} from "../../../common/model/group-ro.model";
import {EditDomainService} from "../edit-domain.service";
import {ResourceDefinitionRo} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import {SmpTableColDef} from "../../../common/components/smp-table/smp-table-coldef.model";
import {DomainDocumentTemplateRo} from "../../../common/model/domain-document-template.ro";
import {DocumentLevelType} from "../../../common/enums/documetn-reference-type.enum";
import {MatDialog} from "@angular/material/dialog";
import {HttpErrorHandlerService} from "../../../common/error/http-error-handler.service";
import {
  DomainDocumentTemplateDialog
} from "./domain-document-template-dialog/domain-template-document-dialog.component";
import {finalize} from "rxjs/operators";
import {ConfirmationDialogComponent} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {NavigationService} from "../../../window/sidenav/navigation-model.service";
import {EditResourceService} from "../../edit-resources/edit-resource.service";

@Component({
  selector: 'domain-extension-document',
  templateUrl: './domain-extension-template.component.html',
  styleUrls: ['./domain-extension-template.component.scss'],
  standalone: false
})
export class DomainExtensionTemplateComponent implements OnInit, BeforeLeaveGuard {


  private _domain: DomainRo;
  private _domainResourceDefinitions: ResourceDefinitionRo[];
  title: string = ""

  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;

  displayedColumns: string[] = ['documentType', 'resourceDefIdentifier', 'subresourceDefIdentifier'];
  dataSource: MatTableDataSource<DomainDocumentTemplateRo> = new MatTableDataSource();
  columns: SmpTableColDef[];
  private _selected: DomainDocumentTemplateRo;

  constructor(private domainService: EditDomainService,
              private alertService: AlertMessageService,
              private httpErrorHandlerService: HttpErrorHandlerService,
              private dialog: MatDialog,
              private translateService: TranslateService,
              private navigationService: NavigationService,
              private editResourceService: EditResourceService) {
    this.columns = [
      {
        columnDef: 'documentType',
        header: 'domain.extension.template.label.document.level',
        cell: (row: DomainDocumentTemplateRo) => row.documentLevel
      } as SmpTableColDef,
      {
        columnDef: 'resourceDefIdentifier',
        header: 'domain.extension.template.label.resource.definition',
        cell: (row: DomainDocumentTemplateRo) => row.resourceDefIdentifier
      } as SmpTableColDef,
      {
        columnDef: 'subresourceDefIdentifier',
        header: 'domain.extension.template.label.subresource.definition',
        cell: (row: DomainDocumentTemplateRo) => row.subresourceDefIdentifier
      } as SmpTableColDef,

    ];

    this.refreshDomainsResourceDefinitions()
  }

  ngOnInit(): void {
    // filter predicate for search the domain
    this.dataSource.filterPredicate =
      (data: GroupRo, filter: string) => {
        return !filter || -1 != data.groupName.toLowerCase().indexOf(filter.trim().toLowerCase())
      };
  }

  get domain(): DomainRo {
    // no changes for the domain data
    return this._domain;
  }

  @Input()
  set domain(value: DomainRo) {
    (async () => {
      this._domain = value;
      this.refreshDomainsResourceDefinitions()
      if (!!value) {
        this.title = await lastValueFrom(this.translateService.get("domain.group.title.domains.groups.for.domain.code", {domainCode: value.domainCode}));
        this.loadTableData();
      } else {
        this.title = await lastValueFrom(this.translateService.get("domain.group.title.domains.groups"));
        this.isLoadingResults = false;
      }
    })();
  }

  get domainResourceDefinitions(): ResourceDefinitionRo[] {
    // no changes for the domain data
    return this._domainResourceDefinitions;
  }

  @Input() set domainResourceDefinitions(value: ResourceDefinitionRo[]) {
    this._domainResourceDefinitions = value;
  }

  public refresh() {
    this.loadTableData();
  }

  refreshDomainsResourceDefinitions() {
    if (!this.domain) {
      this._domainResourceDefinitions = [];
      return;
    }

    this.domainService.getDomainResourceDefinitionsObservable(this.domain)
      .subscribe({
        next: (result: ResourceDefinitionRo[]) => {
          this._domainResourceDefinitions = result
        }, error: (error: any) => {
          this.httpErrorHandlerService.handleHttpError(error);
        }
      });
  }


  loadTableData() {

    this._selected = null;
    if (!this._domain) {
      this.dataSource.data = null;
      return;
    }
    this.isLoadingResults = true;
    this.domainService.getDomainDocumentTemplatesObservable(this._domain.domainId)
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe({
        next: (result: DomainDocumentTemplateRo[]) => {
          this.dataSource.data = result;
          this.resultsLength = result.length;
          this.isLoadingResults = false;
        },
        error: (error) => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }


  isDirty(): boolean {
    return false;
  }

  onAddButtonClicked() {
    this.showTemplateDialog(this.createTemplateDocument());
  };

  createTemplateDocument(): DomainDocumentTemplateRo {
    return {
      domainCode: this._domain?.domainCode,
      templateId: null,
      resourceDefIdentifier: null,
      subresourceDefIdentifier: null,
      documentLevel: DocumentLevelType.RESOURCE,
    } as DomainDocumentTemplateRo;

  };

  showTemplateDialog(docTemplate: DomainDocumentTemplateRo) {
    this.dialog.open(DomainDocumentTemplateDialog, {
      data: {
        template: docTemplate,
        domain: this.domain,
        domainResourceDefs: this.domainResourceDefinitions,
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  };


  onEditSelectedButtonClicked() {
    this.openDocumentEditor(this.selectedItem);
  };


  async onDeleteSelectedButtonClicked() {
    if (!this._domain || !this._domain.domainId) {
      this.alertService.error(await lastValueFrom(this.translateService.get("domain.extension.template.error.delete")));
      return;
    }
    if (!this._selected || !this._selected.templateId) {
      this.alertService.error(await lastValueFrom(this.translateService.get("domain.extension.template.error.delete")));
      return;
    }

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("domain.extension.template.delete.confirmation.dialog.title")),
        description: await lastValueFrom(this.translateService.get("domain.extension.template.delete.confirmation.dialog.description"))
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteDocumentTemplate(this._domain, this.selectedItem);
      }
    });
  }


  deleteDocumentTemplate(domain: DomainRo, item: DomainDocumentTemplateRo) {
    this.domainService.deleteDocumentTemplateObservable(domain.domainId, item.templateId).subscribe({
        next: async (result: DomainDocumentTemplateRo) => {
          if (result) {
            this.alertService.success(await lastValueFrom(this.translateService.get("domain.extension.template.success.delete")));
            this.onItemSelected(null);
            this.refresh()
          }
        }, error: (error) => {
          this.alertService.error(error.error?.errorDescription)
        }
      }
    )
  };


  onItemSelected(item: DomainDocumentTemplateRo) {
    this._selected = item;
  }


  get selectedItem(): DomainDocumentTemplateRo {
    return this._selected;
  }

  get isItemSelected(): boolean {
    return !!this._selected;
  }

  get isDomainNotSelected() {
    return !this._domain
  }

  applyDocumentFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  async openDocumentEditor(row: DomainDocumentTemplateRo) {
    // set selected resource
    this.editResourceService.selectedDomain = this.domain;
    this.editResourceService.selectedDomainDocumentTemplate = row;
    let node = await this.createDomainDocumentTemplateNavigationNode();
    this.navigationService.selected.children = [node]
    this.navigationService.select(node);
  }

  public async createDomainDocumentTemplateNavigationNode() {
    return {
      code: "edit-domain-document-template",
      icon: "extension",
      name: await lastValueFrom(this.translateService.get("domain.extension.label.edit.document.template")),
      children: [],
      routerLink: "edit-domain-document-template",
      selected: true,
      tooltip: "",
      transient: true,
      i18n: "navigation.label.edit.document.template"
    }
  }
}
