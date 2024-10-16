import {Component, Input,} from '@angular/core';
import {DomainRo} from "../../model/domain-ro.model";
import {
  AdminDomainService
} from "../../../system-settings/admin-domain/admin-domain.service";
import {AlertMessageService} from "../../alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {PageEvent} from "@angular/material/paginator";
import {MemberRo} from "../../model/member-ro.model";
import {finalize} from "rxjs/operators";
import {TableResult} from "../../model/table-result.model";
import {
  MemberDialogComponent
} from "../../dialogs/member-dialog/member-dialog.component";
import {MembershipRoleEnum} from "../../enums/membership-role.enum";
import {MemberTypeEnum} from "../../enums/member-type.enum";
import {GroupRo} from "../../model/group-ro.model";
import {lastValueFrom, Observable} from "rxjs";
import {SearchTableResult} from "../../search-table/search-table-result.model";
import {
  ConfirmationDialogComponent
} from "../../dialogs/confirmation-dialog/confirmation-dialog.component";
import {ResourceRo} from "../../model/resource-ro.model";
import {TranslateService} from "@ngx-translate/core";
import {MembershipService} from "../../services/membership.service";
import {
  SmpTableColDef
} from "../../components/smp-table/smp-table-coldef.model";
import {MatTableDataSource} from "@angular/material/table";


@Component({
  selector: 'domain-member-panel',
  templateUrl: './membership-panel.component.html',
  styleUrls: ['./membership-panel.component.scss']
})
export class MembershipPanelComponent implements BeforeLeaveGuard {

  pageSize: number = 10;
  pageIndex: number = 0;
  dataLength: number = 0;
  @Input() membershipType: MemberTypeEnum = MemberTypeEnum.DOMAIN;


  private _domain: DomainRo;
  private _group: GroupRo;
  private _resource: ResourceRo;

  columns: SmpTableColDef[];

  dataSource: MatTableDataSource<MemberRo> = new MatTableDataSource();
  selectedMember: MemberRo;
  filter: any = {};

  isLoadingResults = false;
  formTitle = "";

  //@ViewChild('memberPaginator') paginator: MatPaginator;

  constructor(private domainService: AdminDomainService,
              private membershipService: MembershipService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private translateService: TranslateService) {
    (async () => await this.updateTitle()) ();

    this.columns = [
      {
        columnDef: 'username',
        header: 'membership.panel.label.username',
        cell: (row: MemberRo) => row.username
      } as SmpTableColDef,
      {
        columnDef: 'fullName',
        header: 'membership.panel.label.full.name',
        cell: (row: MemberRo) => row.fullName
      } as SmpTableColDef,
      {
        columnDef: 'roleType',
        header: 'membership.panel.label.role.type',
        cell: (row: MemberRo) => row.roleType
      } as SmpTableColDef,
      {
        columnDef: 'hasPermissionToReview',
        header: 'membership.panel.label.member.of',
        cell: (row: MemberRo) => row.hasPermissionReview
      } as SmpTableColDef,
      {
        columnDef: 'memberOf',
        header: 'membership.panel.label.permission.review',
        cell: (row: MemberRo) => row.memberOf
      } as SmpTableColDef

    ];

  }
  ngAfterViewInit() {
    this.loadMembershipData();
  }

  async updateTitle() {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        this.formTitle = await lastValueFrom(this.translateService.get("membership.panel.title.domain", {value: (!!this._domain ? ": [" + this._domain.domainCode + "]" : "")}));
        break;
      case MemberTypeEnum.GROUP:
        this.formTitle =  await lastValueFrom(this.translateService.get("membership.panel.title.group", {value: (!!this._group ? ": [" + this._group.groupName + "]" : "")}));
        break;
      case MemberTypeEnum.RESOURCE:
        this.formTitle = await lastValueFrom(this.translateService.get("membership.panel.title.resource"));
        break;
    }
  }

  get domain(): DomainRo {
    return this._domain;
  }

  public get membershipCount(): number {
    return this.dataLength;
  }

  public get displayedColumns(): string[] {
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        return  ['username', 'fullName', 'roleType'];
      case MemberTypeEnum.GROUP:
        return ['username', 'fullName', 'roleType'];
      case MemberTypeEnum.RESOURCE:
        return ['username', 'fullName', 'roleType', 'hasPermissionToReview'];
    }
  }

  @Input() set domain(value: DomainRo) {
    this._domain = value;
    if (!!value) {
      if (this.membershipType === MemberTypeEnum.DOMAIN) {
        this.loadMembershipData();
      }
    }
  }

  get group(): GroupRo {
    return this._group;
  }

  @Input() set group(value: GroupRo) {
    this._group = value;

    if (!!value) {
      if (this.membershipType === MemberTypeEnum.GROUP) {
        this.loadMembershipData();
      }
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
    }
  }

  onPageChanged(page: PageEvent) {
    this.pageIndex = page.pageIndex;
    this.pageSize = page.pageSize;
    this.loadMembershipData();
  }

  public loadMembershipData() {
    this.memberSelected(null)
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
          this.dataSource.data = [...result.serviceEntities];
          this.dataLength = result.count;
          this.isLoadingResults = false;
        }
      );
  }

  applyMemberFilter(filterValue: string) {
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

  public async onDeleteSelectedButtonClicked() {


    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("membership.panel.delete.confirmation.dialog.title")),
        description: await lastValueFrom(this.translateService.get("membership.panel.delete.confirmation.dialog.description", {username: this.selectedMember.username}))
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
    let page = this.pageIndex
    let pageSize = this.pageSize;
    switch (this.membershipType) {
      case MemberTypeEnum.DOMAIN:
        return !this._domain ? null : this.membershipService.getDomainMembersObservable(this._domain.domainId, this.filter, page, pageSize);
      case MemberTypeEnum.GROUP:
        return !this._group ? null : this.membershipService.getGroupMembersObservable(this._group.groupId, this._domain.domainId, this.filter, page, pageSize);
      case MemberTypeEnum.RESOURCE:
        return !this._resource ? null : this.membershipService.getResourceMembersObservable(this._resource, this._group, this._domain, this.filter, page, pageSize);
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






