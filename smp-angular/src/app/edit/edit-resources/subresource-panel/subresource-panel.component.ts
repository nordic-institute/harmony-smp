import {AfterViewInit, Component, Input, ViewChild,} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator} from "@angular/material/paginator";
import {GroupRo} from "../../../common/model/group-ro.model";
import {ResourceRo} from "../../../common/model/resource-ro.model";
import {
  AlertMessageService
} from "../../../common/alert-message/alert-message.service";
import {finalize} from "rxjs/operators";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {
  ResourceDefinitionRo
} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {EditResourceService} from "../edit-resource.service";
import {SubresourceRo} from "../../../common/model/subresource-ro.model";
import {MatTableDataSource} from "@angular/material/table";
import {
  ConfirmationDialogComponent
} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {
  SubresourceDialogComponent
} from "./subresource-dialog/subresource-dialog.component";
import {
  SubresourceDefinitionRo
} from "../../../system-settings/admin-extension/subresource-definition-ro.model";
import {
  NavigationNode,
  NavigationService
} from "../../../window/sidenav/navigation-model.service";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import StringUtils from "../../../common/utils/string-utils";
import {
  SmpTableColDef
} from "../../../common/components/smp-table/smp-table-coldef.model";


@Component({
  selector: 'subresource-panel',
  templateUrl: './subresource-panel.component.html',
  styleUrls: ['./subresource-panel.component.scss']
})
export class SubresourcePanelComponent implements AfterViewInit, BeforeLeaveGuard {


  title: string = "";
  @Input() group: GroupRo;
  private _resource: ResourceRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];

  dataSource: MatTableDataSource<SubresourceRo> = new MatTableDataSource();
  selected: SubresourceRo;
  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['identifierValue', 'identifierScheme', 'subresourceTypeIdentifier'];
  columns: SmpTableColDef[];

  constructor(private editResourceService: EditResourceService,
              private navigationService: NavigationService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private translateService: TranslateService) {
    this.translateService.get("subresource.panel.title").subscribe(value => this.title = value);
    this.columns = [
      {
        columnDef: 'identifierScheme',
        header: 'subresource.panel.label.identifier.value',
        cell: (row: SubresourceRo) => row.identifierScheme
      } as SmpTableColDef,
      {
        columnDef: 'identifierValue',
        header: 'subresource.panel.label.identifier.value',
        cell: (row: SubresourceRo) => row.identifierValue
      } as SmpTableColDef,
      {
        columnDef: 'subresourceTypeIdentifier',
        header: 'subresource.panel.label.subresource.type',
        cell: (row: SubresourceRo) => row.subresourceTypeIdentifier
      } as SmpTableColDef
    ];
  }

  ngAfterViewInit() {

    //   this.dataSource.paginator = this.paginator;
  }

  @Input() set resource(resource: ResourceRo) {
    this._resource = resource;
    this.loadSubResources();
  }

  get resource(): ResourceRo {
    return this._resource;

  }

  getSubresourceDefinitions(): SubresourceDefinitionRo[] {
    if (!this._resource) {
      return null;
    }
    if (!this.domainResourceDefs) {
      return null;
    }

    let result: SubresourceDefinitionRo[] = this.domainResourceDefs.find(def => def.identifier == this._resource.resourceTypeIdentifier)?.subresourceDefinitions;
    return result

  }


  loadSubResources() {
    if (!this._resource) {
      return;
    }

    this.isLoadingResults = true;
    this.editResourceService.getSubResourcesForResource(this._resource)
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe((result: SubresourceRo[]) => {
          this.dataSource.data = [...result];

        }
      );
  }


  applySubResourceFilter(filterValue: string) {

    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  get createSubResourceDisabled(): boolean {
    return !this._resource;
  }

  public async onCreateResourceButtonClicked() {
    let subResDef = this.getSubresourceDefinitions();
    this.dialog.open(SubresourceDialogComponent, {
      data: {
        resource: this._resource,
        subresourceDefs: subResDef,
        subresource: this.createSubresource(subResDef),

        formTitle: await lastValueFrom(this.translateService.get("subresource.panel.subresource.dialog.title"))
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }

  createSubresource(subResDef: SubresourceDefinitionRo[]): SubresourceRo {

    return {
      subresourceTypeIdentifier: !!subResDef && subResDef.length > 0 ? subResDef[0].identifier : "",
      identifierValue: "",
    }
  }

  public refresh() {
    if (this.paginator) {
      this.paginator.firstPage();
    }
    this.loadSubResources();
  }

  public onEditSelectedButtonClicked() {
    this.showSubresourceEditPanel(this.selected)
  }

  public async showSubresourceEditPanel(subresource: SubresourceRo) {
    if (!this.navigationService.selected) {
      this.navigationService.select(null);
      return;
    }
    this.editResourceService.selectedResource = this.resource;
    this.editResourceService.selectedSubresource = subresource;

    let node: NavigationNode = await this.createNew();
    this.navigationService.selected.children = [node]
    this.navigationService.select(node);

  }

  public async createNew() {
    return {
      code: "subresource-document",
      icon: "description",
      name: await lastValueFrom(this.translateService.get("subresource.panel.label.subresource.name")),
      routerLink: "subresource-document",
      selected: true,
      tooltip: "",
      transient: true,
      i18n: "navigation.label.edit.subresource.document"
    }
  }

  public async onDeleteSelectedButtonClicked() {
    if (!this._resource || !this._resource.resourceId) {
      this.alertService.error(await lastValueFrom(this.translateService.get("subresource.panel.error.delete.resource.data")));
      return;
    }

    if (!this.selected || !this.selected.subresourceId) {
      this.alertService.error(await lastValueFrom(this.translateService.get("subresource.panel.error.delete.subresource.data")));
      return;
    }

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("subresource.panel.delete.confirmation.dialog.title")),
        description: await lastValueFrom(this.translateService.get("subresource.panel.delete.confirmation.dialog.description", {
          identifierScheme: StringUtils.toEmpty(this.selected.identifierScheme),
          identifierValue: this.selected.identifierValue
        }))
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteSubResource(this.selected, this._resource);
      }
    });
  }

  deleteSubResource(subresource: SubresourceRo, resource: ResourceRo) {

    this.isLoadingResults = true;
    this.editResourceService.deleteSubresourceFromResource(subresource, resource)
      .pipe(
        finalize(() => {
          this.refresh();
          this.isLoadingResults = false;
        }))
      .subscribe(async (result: SubresourceRo) => {
          if (result) {
            this.alertService.success(await lastValueFrom(this.translateService.get("subresource.panel.success.delete", {
              identifierScheme: StringUtils.toEmpty(this.selected.identifierScheme),
              identifierValue: this.selected.identifierValue
            })));
            this.selected = null;
          }
        }, (error) => {
          this.alertService.error(error.error?.errorDescription);
        }
      );
  }

  public onResourceSelected(resource: ResourceRo) {
    this.selected = resource;
  }

  get disabledForm(): boolean {
    return !this._resource;
  }

  isDirty(): boolean {
    return false
  }
}






