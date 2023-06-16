import {Component, Input, ViewChild,} from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {finalize} from "rxjs/operators";
import {MatTableDataSource} from "@angular/material/table";
import {GroupRo} from "../../../common/model/group-ro.model";
import {EditDomainService} from "../edit-domain.service";
import {GroupDialogComponent} from "./group-dialog/group-dialog.component";
import {VisibilityEnum} from "../../../common/enums/visibility.enum";
import {MatPaginator} from "@angular/material/paginator";
import {ConfirmationDialogComponent} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {ResourceDefinitionRo} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {
  ManageMembersDialogComponent
} from "../../../common/dialogs/manage-members-dialog/manage-members-dialog.component";
import {MemberTypeEnum} from "../../../common/enums/member-type.enum";

@Component({
  selector: 'domain-group-panel',
  templateUrl: './domain-group.component.html',
  styleUrls: ['./domain-group.component.scss']
})
export class DomainGroupComponent implements BeforeLeaveGuard {


  private _domain: DomainRo;
  private _domainResourceDefinitions: ResourceDefinitionRo[];
  title: string = "Domain groups"

  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;

  displayedColumns: string[] = ['groupName', 'visibility', 'groupDescription'];
  dataSource: MatTableDataSource<GroupRo> = new MatTableDataSource();

  selectedGroup: GroupRo;

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private editDomainService: EditDomainService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private formBuilder: FormBuilder) {
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

  @Input() set domain(value: DomainRo) {
    this._domain = value;
    if (!!value) {
      this.title  = "Domain groups for ["+value.domainCode+"]"
      this.loadTableData();
    } else {
      this.title  = "Domain groups"
      this.isLoadingResults = false;
    }
  }

  get domainResourceDefinitions(): ResourceDefinitionRo[] {
    // no changes for the domain data
    return this._domainResourceDefinitions;
  }

  @Input() set domainResourceDefinitions(value:  ResourceDefinitionRo[]) {
    this._domainResourceDefinitions = value;
  }

  public refresh() {
    if (this.paginator) {
      this.paginator.firstPage();
    }
    this.loadTableData();
  }

  loadTableData() {
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
          this.isLoadingResults = false;
        }, (error) => {
          this.alertService.error(error.error?.errorDescription)
        }
      );
  }


  isDirty(): boolean {
    return false;
  }

  onAddButtonClicked() {
    this.dialog.open(GroupDialogComponent, {
      data: {
        domain: this._domain,
        group: this.createGroup(),
        formTitle: "Group details dialog"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  };
  onEditSelectedGroupMembersButtonClicked() {
    this.dialog.open(ManageMembersDialogComponent, {
      data: {
        membershipType: MemberTypeEnum.GROUP,
        domain: this._domain,
        group: this.selectedGroup,
        formTitle: "Resource members management dialog"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  };
  onEditSelectedButtonClicked() {
    this.showEditDialogForGroup(this.selectedGroup);
  };

  showEditDialogForGroup(group: GroupRo) {
    this.dialog.open(GroupDialogComponent, {
      data: {
        domain: this._domain,
        group: group,
        formTitle: "Group details dialog"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  };

  onEditGroupMembersButtonClicked() {
    this.showEditDialogForGroup(this.selectedGroup);
  };

  onDeleteSelectedButtonClicked() {
    if (!this._domain || !this._domain.domainId) {
      this.alertService.error("Can not delete group because of invalid domain data. Is group selected?");
      return;
    }
    if (!this.selectedGroup || !this.selectedGroup.groupId) {
      this.alertService.error("Can not delete group because of invalid domain data. Is group selected?");
      return;
    }

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete Group " + this.selectedGroup.groupName + " from DomiSMP",
        description: "Action will permanently delete group! <br/><br/> Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteGroup(this._domain, this.selectedGroup);
      }
    });
  }


  deleteGroup(domain: DomainRo, group: GroupRo) {
    this.editDomainService.deleteDomainGroupObservable(domain.domainId, group.groupId).subscribe((result: GroupRo) => {
        if (result) {
          this.alertService.success("Domain group [" + result.groupName + "] deleted");
          this.onGroupSelected(null);
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

  applyGroupFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }

  }

}
