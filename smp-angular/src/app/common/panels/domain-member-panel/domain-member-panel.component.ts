import {Component, Input, ViewChild,} from '@angular/core';
import {DomainRo} from "../../model/domain-ro.model";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
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


@Component({
  selector: 'domain-member-panel',
  templateUrl: './domain-member-panel.component.html',
  styleUrls: ['./domain-member-panel.component.scss']
})
export class DomainMemberPanelComponent implements BeforeLeaveGuard {

  private _domain: DomainRo;
  domainForm: FormGroup;

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
              private dialog: MatDialog,
              private formBuilder: FormBuilder) {

    this.domainForm = formBuilder.group({
      'domainCode': new FormControl({value: '', readonly: true})
    });

  }

  ngAfterViewInit() {
    if (!!this._domain) {
      this.loadTableData();
    }
  }

  get domain(): DomainRo {
    let newDomain = {...this._domain};

    return newDomain;
  }

  @Input() set domain(value: DomainRo) {
    this._domain = value;

    if (!!value) {
      this.loadTableData();
    } else {
      this.isLoadingResults = false;
    }
  }


  onPageChanged(page: PageEvent) {
    this.loadTableData();
  }

  loadTableData() {
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

  applyMemberFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.filter["filter"] = filterValue.trim().toLowerCase();
    this.refresh();
  }

  get submitButtonEnabled(): boolean {
    return this.domainForm.valid && this.domainForm.dirty;
  }

  get inviteMemberDisabled(): boolean {
    return !this._domain;
  }

  public memberSelected(member: MemberRo) {
    this.selectedMember = member;
  }

  public onAddMemberButtonClicked() {
    // add member
    this.dialog.open(MemberDialogComponent, {
      data: {
        domain: this._domain,
        member: this.createDomainMember(),
        formTitle: "Invite new member to domain"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }

  public refresh() {
    if (this.paginator) {
      this.paginator.firstPage();
    }
    this.loadTableData();
  }

  public createDomainMember(): MemberRo {
    return {
      memberOf: MemberTypeEnum.DOMAIN,
      roleType: MembershipRoleEnum.VIEWER
    } as MemberRo
  }

  public onEditSelectedButtonClicked() {
    this.showEditDailogForMember(this.selectedMember);
  }

  public showEditDailogForMember(member: MemberRo) {
    this.dialog.open(MemberDialogComponent, {
      data: {
        domain: this._domain,
        member: member,
        formTitle: "Edit member role for domain"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }

  public onDeleteSelectedButtonClicked() {
    this.membershipService.deleteMemberFromDomain(this._domain.domainId, this.selectedMember).subscribe(value => {
      this.refresh();
    });
    ;
  }

  isDirty(): boolean {
    return this.domainForm.dirty;
  }

  get domainNotSelected() {
    return !this._domain
  }

}






