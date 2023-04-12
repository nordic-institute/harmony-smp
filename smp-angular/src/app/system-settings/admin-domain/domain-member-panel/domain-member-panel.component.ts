import {Component, EventEmitter, Input, Output, ViewChild,} from '@angular/core';
import {DomainRo} from "../domain-ro.model";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AdminDomainService} from "../admin-domain.service";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MemberRo} from "./member-ro.model";
import {finalize} from "rxjs/operators";
import {TableResult} from "./table-result.model";
import {MemberDialogComponent} from "./member-dialog/member-dialog.component";
import {MembershipService} from "./membership.service";
import {MembershipRoleEnum} from "../../../common/enums/membership-role.enum";
import {MemberTypeEnum} from "../../../common/enums/member-type.enum";


@Component({
  selector: 'domain-member-panel',
  templateUrl: './domain-member-panel.component.html',
  styleUrls: ['./domain-member-panel.component.scss']
})
export class DomainMemberPanelComponent implements BeforeLeaveGuard {
  @Output() onSaveSmlIntegrationDataEvent: EventEmitter<DomainRo> = new EventEmitter();
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
    this.dialog.open(MemberDialogComponent, {
      data: {
        domain: this._domain,
        member: this.selectedMember,
        formTitle: "Edit member role for domain"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }
  public onDeleteSelectedButtonClicked() {
    this.membershipService.deleteMemberFromDomain(this._domain.domainId, this.selectedMember).subscribe(value => {
      this.refresh();
    });;
  }

  isDirty(): boolean {
    return this.domainForm.dirty;
  }

  get domainNotSelected() {
    return !this._domain
  }
}






