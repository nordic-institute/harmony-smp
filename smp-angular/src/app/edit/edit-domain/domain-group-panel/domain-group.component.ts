import {Component, Input, OnInit,} from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {
  AlertMessageService
} from "../../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {finalize} from "rxjs/operators";
import {MatTableDataSource} from "@angular/material/table";
import {GroupRo} from "../../../common/model/group-ro.model";
import {EditDomainService} from "../edit-domain.service";
import {GroupDialogComponent} from "./group-dialog/group-dialog.component";
import {VisibilityEnum} from "../../../common/enums/visibility.enum";
import {
  ConfirmationDialogComponent
} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {
  ResourceDefinitionRo
} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {
  ManageMembersDialogComponent
} from "../../../common/dialogs/manage-members-dialog/manage-members-dialog.component";
import {MemberTypeEnum} from "../../../common/enums/member-type.enum";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import {
  SmpTableColDef
} from "../../../common/components/smp-table/smp-table-coldef.model";
import {
  EditResourceController
} from "../../edit-resources/edit-resource.controller";

@Component({
  selector: 'domain-group-panel',
  templateUrl: './domain-group.component.html',
  styleUrls: ['./domain-group.component.scss']
})
export class DomainGroupComponent implements OnInit, BeforeLeaveGuard {


  private _domain: DomainRo;
  private _domainResourceDefinitions: ResourceDefinitionRo[];
  title: string = ""

  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;

  displayedColumns: string[] = ['groupName', 'visibility', 'groupDescription'];
  dataSource: MatTableDataSource<GroupRo> = new MatTableDataSource();
  columns: SmpTableColDef[];
  selectedGroup: GroupRo;

  constructor(private editDomainService: EditDomainService,
              private editResourceController: EditResourceController,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private formBuilder: FormBuilder,
              private translateService: TranslateService) {
    this.columns = [
      {
        columnDef: 'groupName',
        header: 'domain.group.label.group.name',
        cell: (row: GroupRo) => row.groupName
      } as SmpTableColDef,
      {
        columnDef: 'visibility',
        header: 'domain.group.label.group.visibility',
        cell: (row: GroupRo) => row.visibility
      } as SmpTableColDef,
      {
        columnDef: 'groupDescription',
        header: 'domain.group.label.group.description',
        cell: (row: GroupRo) => row.groupDescription
      } as SmpTableColDef
    ];
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

  loadTableData() {
    this.selectedGroup = null;
    if (!this._domain) {
      this.dataSource.data = null;
      return;
    }
    this.isLoadingResults = true;
    this.editDomainService.getDomainGroupsObservable(this._domain.domainId)
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe((result: GroupRo[]) => {

          this.dataSource.data = result;
          this.resultsLength = result.length;
          this.isLoadingResults = false;
        }, (error) => {
          this.alertService.error(error.error?.errorDescription)
        }
      );
  }


  isDirty(): boolean {
    return false;
  }

  async onAddButtonClicked() {
    this.dialog.open(GroupDialogComponent, {
      data: {
        domain: this._domain,
        group: this.createGroup(),
        formTitle: await lastValueFrom(this.translateService.get("domain.group.group.details.dialog.title"))
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  };

  async onEditSelectedGroupMembersButtonClicked() {
    this.dialog.open(ManageMembersDialogComponent, {
      data: {
        membershipType: MemberTypeEnum.GROUP,
        domain: this._domain,
        group: this.selectedGroup,
        formTitle: await lastValueFrom(this.translateService.get("domain.group.manage.members.dialog.title"))
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  };

  onEditSelectedButtonClicked() {
    this.showEditDialogForGroup(this.selectedGroup);
  };

  async showEditDialogForGroup(group: GroupRo) {
    this.dialog.open(GroupDialogComponent, {
      data: {
        domain: this._domain,
        group: group,
        formTitle: await lastValueFrom(this.translateService.get("domain.group.group.details.dialog.title"))
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  };

  onEditGroupMembersButtonClicked() {
    this.showEditDialogForGroup(this.selectedGroup);
  };

  async onDeleteSelectedButtonClicked() {
    if (!this._domain || !this._domain.domainId) {
      this.alertService.error(await lastValueFrom(this.translateService.get("domain.group.error.delete")));
      return;
    }
    if (!this.selectedGroup || !this.selectedGroup.groupId) {
      this.alertService.error(await lastValueFrom(this.translateService.get("domain.group.error.delete")));
      return;
    }

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("domain.group.delete.confirmation.dialog.title", {groupName: this.selectedGroup.groupName})),
        description: await lastValueFrom(this.translateService.get("domain.group.delete.confirmation.dialog.description"))
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteGroup(this._domain, this.selectedGroup);
      }
    });
  }


  deleteGroup(domain: DomainRo, group: GroupRo) {
    this.editDomainService.deleteDomainGroupObservable(domain.domainId, group.groupId).subscribe(async (result: GroupRo) => {
        if (result) {
          this.alertService.success(await lastValueFrom(this.translateService.get("domain.group.success.delete", {groupName: result.groupName})));
          this.onGroupSelected(null);
          this.editResourceController.dataChanged = true;
          this.refresh()
        }
      }, (error) => {
        this.alertService.error(error.error?.errorDescription)
      }
    )
  };

  public createGroup(): GroupRo {
    return {
      visibility: VisibilityEnum.Public
    } as GroupRo
  }


  onGroupSelected(group: GroupRo) {
    this.selectedGroup = group;
  }


  get groupSelected(): boolean {
    return !!this.selectedGroup;
  }

  get domainNotSelected() {
    return !this._domain
  }

  applyGroupFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

}
