import {Component, Input, ViewChild,} from '@angular/core';
import {DomainRo} from "../../model/domain-ro.model";
import {AdminDomainService} from "../../../system-settings/admin-domain/admin-domain.service";
import {AlertMessageService} from "../../alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MemberRo} from "../../model/member-ro.model";
import {finalize} from "rxjs/operators";
import {TableResult} from "../../model/table-result.model";
import {MemberDialogComponent} from "../../dialogs/member-dialog/member-dialog.component";
import {MembershipService} from "./membership.service";
import {MembershipRoleEnum} from "../../enums/membership-role.enum";
import {MemberTypeEnum} from "../../enums/member-type.enum";
import {GroupRo} from "../../model/group-ro.model";
import {Observable} from "rxjs";
import {SearchTableResult} from "../../search-table/search-table-result.model";
import {ConfirmationDialogComponent} from "../../dialogs/confirmation-dialog/confirmation-dialog.component";
import {ResourceRo} from "../../model/resource-ro.model";


@Component({
  selector: 'domain-member-panel',
  templateUrl: './membership-panel.component.html',
  styleUrls: ['./membership-panel.component.scss']
})
export class MembershipPanelComponent implements BeforeLeaveGuard {

  @Input() membershipType: MemberTypeEnum = MemberTypeEnum.DOMAIN;

  private _domain: DomainRo;
  private _group: GroupRo;
  private _resource: ResourceRo;


  displayedColumns: string[] = ['username', 'fullName', 'roleType', 'memberOf'];
  data: MemberRo[] = [];
  selectedMember: MemberRo;
  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private domainService: AdminDomainService,
              private membershipService: MembershipService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {


  }

  ngAfterViewInit() {
    this.loadMembershipData();
  }

  get title() {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:

        return "Direct Domain members" + (!!this._domain ? ": [" + this._domain.domainCode + "]" : "")
      case MemberTypeEnum.GROUP:
        return "Direct Group members" + (!!this._group ? ": [" + this._group.groupName + "]" : "")
      case MemberTypeEnum.RESOURCE:
        return "Resource direct members"
    }
  }

  get domain(): DomainRo {
    return this._domain;
  }

  @Input() set domain(value: DomainRo) {
    this._domain = value;

    if (!!value) {
      if (this.membershipType == MemberTypeEnum.DOMAIN) {
        this.loadMembershipData();
      }
    } else {
      this.isLoadingResults = false;
    }
  }

  get group(): GroupRo {
    return this._group;
  }

  @Input() set group(value: GroupRo) {
    this._group = value;

    if (!!value) {
      if (this.membershipType == MemberTypeEnum.GROUP) {
        this.loadMembershipData();
      }
    } else {
      this.isLoadingResults = false;
    }
  }
  get resource(): ResourceRo {
    return this._resource;
  }

  @Input() set resource(value: ResourceRo) {
    this._resource = value;

    if (!!value) {
      if (this.membershipType == MemberTypeEnum.RESOURCE) {
        this.loadMembershipData();
      }
    } else {
      this.isLoadingResults = false;
    }
  }

  onPageChanged(page: PageEvent) {
    this.loadMembershipData();
  }

  public loadMembershipData() {

    let membershipService: Observable<SearchTableResult> = this.getMembershipListService();
    if (!membershipService) {
      return;
    }
    this.isLoadingResults = true;
    membershipService
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe((result: TableResult<MemberRo>) => {
          this.data = [...result.serviceEntities];
          this.resultsLength = result.count;
          this.isLoadingResults = false;
        }
      );
  }

  applyMemberFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.filter["filter"] = filterValue.trim().toLowerCase();
    this.refresh();
  }

  get inviteMemberDisabled(): boolean {
    return !this._domain && !this._group;
  }

  public memberSelected(member: MemberRo) {
    this.selectedMember = member;
  }

  public onAddMemberButtonClicked() {
    this.showEditDialogForMember(this.createMember())
  }

  public refresh() {
    if (this.paginator) {
      this.paginator.firstPage();
    }
    this.loadMembershipData();
  }

  public createMember(): MemberRo {
    return {
      memberOf: this.membershipType,
      roleType: MembershipRoleEnum.VIEWER
    } as MemberRo
  }

  public onEditSelectedButtonClicked() {
    this.showEditDialogForMember(this.selectedMember);
  }

  public showEditDialogForMember(member: MemberRo) {
    this.dialog.open(MemberDialogComponent, {
      data: {
        membershipType: this.membershipType,
        domain: this._domain,
        group: this._group,
        resource: this._resource,
        member: member,
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }

  public onDeleteSelectedButtonClicked() {


    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Remove member",
        description: "Action will remove member  [" + this.selectedMember.username + "]! " +
          "Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.getDeleteMembershipService().subscribe(value => {
            this.refresh();
          }, (error) => {
            this.alertService.error(error.error?.errorDescription);
          }
        );
      }
    });
  }

  isDirty(): boolean {
    return false
  }

  get entityNotSelected() {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        return !this._domain;
      case MemberTypeEnum.GROUP:
        return !this._group;
      case MemberTypeEnum.RESOURCE:
        return !this._resource;
    }
  }

  protected getMembershipListService(): Observable<SearchTableResult> {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:

        return !this._domain?null:this.membershipService.getDomainMembersObservable(this._domain.domainId, this.filter, this.paginator.pageIndex, this.paginator.pageSize);
      case MemberTypeEnum.GROUP:
        return !this._group?null: this.membershipService.getGroupMembersObservable(this._group.groupId, this._domain.domainId, this.filter, this.paginator.pageIndex, this.paginator.pageSize);
      case MemberTypeEnum.RESOURCE:
        return  !this._resource?null: this.membershipService.getResourceMembersObservable(this._resource, this._group, this._domain, this.filter, this.paginator.pageIndex, this.paginator.pageSize);
    }
  }

  protected getDeleteMembershipService(): Observable<MemberRo> {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        return this.membershipService.deleteMemberFromDomain(this._domain.domainId, this.selectedMember);
      case MemberTypeEnum.GROUP:
        return this.membershipService.deleteMemberFromGroup(this._group.groupId, this._domain.domainId, this.selectedMember);
      case MemberTypeEnum.RESOURCE:
        return this.membershipService.deleteMemberFromResource(this._resource, this._group, this._domain, this.selectedMember);
    }
  }
}






