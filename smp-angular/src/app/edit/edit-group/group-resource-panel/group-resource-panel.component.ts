import {Component, Input,} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {PageEvent} from "@angular/material/paginator";
import {GroupRo} from "../../../common/model/group-ro.model";
import {ResourceRo} from "../../../common/model/resource-ro.model";
import {
  AlertMessageService
} from "../../../common/alert-message/alert-message.service";
import {EditGroupService} from "../edit-group.service";
import {finalize} from "rxjs/operators";
import {TableResult} from "../../../common/model/table-result.model";
import {
  ConfirmationDialogComponent
} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {
  ResourceDialogComponent
} from "./resource-dialog/resource-dialog.component";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {
  ResourceDefinitionRo
} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {VisibilityEnum} from "../../../common/enums/visibility.enum";
import {
  ManageMembersDialogComponent
} from "../../../common/dialogs/manage-members-dialog/manage-members-dialog.component";
import {MemberTypeEnum} from "../../../common/enums/member-type.enum";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import StringUtils from "../../../common/utils/string-utils";
import {MatTableDataSource} from "@angular/material/table";
import {
  SmpTableColDef
} from "../../../common/components/smp-table/smp-table-coldef.model";
import {
  EditResourceController
} from "../../edit-resources/edit-resource.controller";

@Component({
  selector: 'group-resource-panel',
  templateUrl: './group-resource-panel.component.html',
  styleUrls: ['./group-resource-panel.component.scss']
})
export class GroupResourcePanelComponent implements BeforeLeaveGuard {

  title: string = "";
  private _group: GroupRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];
  dataSource: MatTableDataSource<ResourceRo> = new MatTableDataSource();
  selected: ResourceRo;
  filter: any = {};
  dataLength = 0;
  pageSize = 10;
  pageIndex = 0;
  isLoadingResults = false;

  displayedColumns: string[] = ['identifierValue', 'identifierScheme', "resourceTypeIdentifier"];
  columns: SmpTableColDef[];

  constructor(private editGroupService: EditGroupService,
              private editResourceController: EditResourceController,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private translateService: TranslateService) {

    this.columns = [
      {
        columnDef: 'identifierScheme',
        header: 'group.resource.panel.label.identifier.scheme',
        cell: (row: ResourceRo) => row.identifierScheme
      } as SmpTableColDef,
      {
        columnDef: 'identifierValue',
        header: 'group.resource.panel.label.identifier.value',
        cell: (row: ResourceRo) => row.identifierValue
      } as SmpTableColDef,
      {
        columnDef: 'resourceTypeIdentifier',
        header: 'group.resource.panel.label.resource.type',
        cell: (row: ResourceRo) => row.resourceTypeIdentifier
      } as SmpTableColDef
    ];

  }


  @Input()
  set group(value: GroupRo) {
    (async () => {
      if (this._group == value) {
        return;
      }
      this._group = value;
      this.title = await lastValueFrom(this.translateService.get("group.resource.panel.title", {groupName: (!!this._group ? ": [" + this._group.groupName + "]" : "")}));
      if (!!this._group) {
        this.loadGroupResources();
      } else {
        this.isLoadingResults = false;
      }
    })();
  }

  get group() {
    return this._group;
  }

  onPageChanged(page: PageEvent) {
    this.pageIndex = page.pageIndex;
    this.pageSize = page.pageSize;
    this.loadGroupResources();
  }

  loadGroupResources() {

    this.onResourceSelected(null);
    if (!this._group) {
      return;
    }

    this.isLoadingResults = true;
    this.editGroupService.getGroupResourcesForGroupAdminObservable(this._group, this.domain, this.filter, this.pageIndex, this.pageSize)
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe((result: TableResult<ResourceRo>) => {
          this.dataSource.data = [...result.serviceEntities];
          this.dataLength = result.count;
          this.isLoadingResults = false;
        }
      );
  }


  applyResourceFilter(filterValue: string) {
    this.filter["filter"] = filterValue.trim().toLowerCase();
    this.refresh();
  }

  get createResourceDisabled(): boolean {
    return !this._group;
  }

  public onCreateResourceButtonClicked() {
    this.showResourceEditDialog(this.createResource());
  }

  createResource(): ResourceRo {
    return {
      resourceTypeIdentifier: !!this.domainResourceDefs && this.domainResourceDefs.length > 0 ? this.domainResourceDefs[0].identifier : "",
      identifierValue: "",
      smlRegistered: false,
      visibility: VisibilityEnum.Public
    }
  }

  public refresh() {
    this.loadGroupResources();
  }

  async onEditSelectedGroupMembersButtonClicked() {
    this.dialog.open(ManageMembersDialogComponent, {
      data: {
        membershipType: MemberTypeEnum.RESOURCE,
        domain: this.domain,
        group: this._group,
        resource: this.selected,
        formTitle: await lastValueFrom(this.translateService.get("group.resource.panel.manage.members.dialog.title"))
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  };

  public onEditSelectedButtonClicked() {
    this.showResourceEditDialog(this.selected)
  }

  public async showResourceEditDialog(resource: ResourceRo) {
    this.dialog.open(ResourceDialogComponent, {
      data: {
        resource: resource,
        group: this._group,
        domain: this.domain,
        domainResourceDefs: this.domainResourceDefs,
        formTitle: await lastValueFrom(this.translateService.get("group.resource.panel.resource.details.dialog.title"))
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }

  public async onDeleteSelectedButtonClicked() {
    if (!this._group || !this._group.groupId) {
      this.alertService.error(await lastValueFrom(this.translateService.get("group.resource.panel.error.delete.group")));
      return;
    }

    if (!this.selected || !this.selected.resourceId) {
      this.alertService.error(await lastValueFrom(this.translateService.get("group.resource.panel.error.delete.resource")));
      return;
    }

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("group.resource.panel.delete.confirmation.dialog.title")),
        description: await lastValueFrom(this.translateService.get("group.resource.panel.delete.confirmation.dialog.description", {
          identifierScheme: StringUtils.toEmpty(this.selected.identifierScheme),
          identifierValue: this.selected.identifierValue
        }))
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteResource(this.group, this.selected);
      }
    });
  }

  deleteResource(group: GroupRo, resource: ResourceRo) {
    this.isLoadingResults = true;
    this.editGroupService.deleteResourceFromGroup(resource, this._group, this.domain)
      .pipe(
        finalize(() => {
          this.editResourceController.dataChanged = true;
          this.refresh();
          this.isLoadingResults = false;
        }))
      .subscribe({
          next: async (result: ResourceRo) => {
            if (result) {
              this.alertService.success(await lastValueFrom(this.translateService.get("group.resource.panel.success.delete", {
                identifierScheme: StringUtils.toEmpty(this.selected.identifierScheme),
                identifierValue: this.selected.identifierValue
              })));
            }
          }, error: (error: any) => {
            this.alertService.error(error.error?.errorDescription);
          }
        }
      );
  }


  public onResourceSelected(resource: ResourceRo) {
    this.selected = resource;
  }

  get disabledForm(): boolean {
    return !this._group;
  }

  isDirty(): boolean {
    return false
  }
}






