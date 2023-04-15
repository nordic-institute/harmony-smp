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


@Component({
  selector: 'domain-member-panel',
  templateUrl: './membership-panel.component.html',
  styleUrls: ['./membership-panel.component.scss']
})
export class MembershipPanelComponent implements BeforeLeaveGuard {

  @Input() membershipType: MemberTypeEnum = MemberTypeEnum.DOMAIN;

  private _domain: DomainRo;
  private _group: GroupRo;


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
    if (!!this._domain) {
      this.loadDomainMembers();
    }
  }

  get title() {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        return "Domain direct members"
      case MemberTypeEnum.GROUP:
        return "Group direct members"
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
      this.loadDomainMembers();
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
      this.loadGroupMembers();
    } else {
      this.isLoadingResults = false;
    }
  }


  onPageChanged(page: PageEvent) {
    this.loadMembershipData();
  }

  public loadMembershipData() {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        this.loadDomainMembers();
        break;
      case MemberTypeEnum.GROUP:
        this.loadGroupMembers();
        break;
      case MemberTypeEnum.RESOURCE:
        break;
    }
  }

  loadDomainMembers() {
    if (!this._domain) {
      return;
    }

    this.isLoadingResults = true;
    this.membershipService.getDomainMembersObservable(this._domain.domainId, this.filter, this.paginator.pageIndex, this.paginator.pageSize)
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


  loadGroupMembers() {
    if (!this._group) {
      return;
    }

    this.isLoadingResults = true;
    this.membershipService.getGroupMembersObservable(this._group.groupId, this.filter, this.paginator.pageIndex, this.paginator.pageSize)
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
    // add member
    this.dialog.open(MemberDialogComponent, {
      data: {
        membershipType: this.membershipType,
        domain: this._domain,
        group: this._group,
        member: this.createMember(),
        formTitle: "Invite new member"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
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
        member: this.selectedMember,
        formTitle: "Edit member role for" + this.title
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }

  public onDeleteSelectedButtonClicked() {

    this.getDeleteMembershipService().subscribe(value => {
      this.refresh();
    }, (error) => {
      this.alertService.error(error.error?.errorDescription);
      }
    );
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
        return false;
    }
  }

  protected getMembershipListService(): Observable<SearchTableResult> {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        return this.membershipService.getDomainMembersObservable(this._domain.domainId, this.filter, this.paginator.pageIndex, this.paginator.pageSize);
      case MemberTypeEnum.GROUP:
        return this.membershipService.getGroupMembersObservable(this._group.groupId, this.filter, this.paginator.pageIndex, this.paginator.pageSize);
      case MemberTypeEnum.RESOURCE:
        return null;
    }
  }

  protected getDeleteMembershipService(): Observable<MemberRo> {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        return this.membershipService.deleteMemberFromDomain(this._domain.domainId, this.selectedMember);
      case MemberTypeEnum.GROUP:
        return this.membershipService.deleteMemberFromGroup(this._group.groupId, this.selectedMember);
      case MemberTypeEnum.RESOURCE:
        return null;
    }
  }
}






