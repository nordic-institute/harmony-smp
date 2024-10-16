import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {EditDomainService} from "./edit-domain.service";
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {DomainRo} from "../../common/model/domain-ro.model";
import {
  CancelDialogComponent
} from "../../common/dialogs/cancel-dialog/cancel-dialog.component";
import {MatTabGroup} from "@angular/material/tabs";
import {MemberTypeEnum} from "../../common/enums/member-type.enum";
import {firstValueFrom} from "rxjs";
import {
  HttpErrorHandlerService
} from "../../common/error/http-error-handler.service";
import {
  SmpTableColDef
} from "../../common/components/smp-table/smp-table-coldef.model";

@Component({
  templateUrl: './edit-domain.component.html',
  styleUrls: ['./edit-domain.component.css']
})
export class EditDomainComponent implements OnInit, AfterViewInit, BeforeLeaveGuard {

  membershipType: MemberTypeEnum = MemberTypeEnum.DOMAIN;
  dataSource: MatTableDataSource<DomainRo> = new MatTableDataSource();
  selected: DomainRo;
  domainList: DomainRo[] = [];
  currenTabIndex: number = 0;
  handleTabClick: any;

  displayedColumns: string[] = ['domainCode', "visibility"];
  columns: SmpTableColDef[];

  loading: boolean = false;
  @ViewChild('domainTabs') domainTabs: MatTabGroup;

  constructor(private domainService: EditDomainService,
              private httpErrorHandlerService: HttpErrorHandlerService,
              private dialog: MatDialog) {
    this.columns = [
      {
        columnDef: 'domainCode',
        header: 'edit.domain.label.domain.code',
        cell: (row: DomainRo) => row.domainCode
      } as SmpTableColDef,
      {
        columnDef: 'visibility',
        header: 'edit.domain.label.domain.visibility',
        cell: (row: DomainRo) => row.visibility
      } as SmpTableColDef
    ];


    this.refreshDomains();
  }

  ngOnInit(): void {
    // filter predicate for search the domain
    this.dataSource.filterPredicate =
      (data: DomainRo, filter: string) => {
        return !filter || -1 != data.domainCode.toLowerCase().indexOf(filter.trim().toLowerCase())
      };
  }

  ngAfterViewInit(): void {
    // MatTab has only onTabChanged which is a bit to late. Register new listener to  internal
    // _handleClick handler

    this.registerTabClick();
  }


  refreshDomains() {
    this.loading = true;
    this.domainService.getDomainsForDomainAdminUserObservable()
      .subscribe({
        next: (result: DomainRo[]) => {
          this.updateDomainList(result)
          this.loading = false;
        }, error: (error: any) => {
          this.loading = false;
          this.httpErrorHandlerService.handleHttpError(error);
        }
      });
  }

  registerTabClick(): void {
    if (!this.domainTabs) {
      // tabs are not yet initialized
      return;
    }
    // Get the handler reference
    this.handleTabClick = this.domainTabs._handleClick;

    this.domainTabs._handleClick = (tab, header, newTabIndex) => {

      if (newTabIndex == this.currenTabIndex) {
        return;
      }

      if (this.isCurrentTabDirty()) {
        let canChangeTab = firstValueFrom(this.dialog.open(CancelDialogComponent).afterClosed());
        canChangeTab.then((canChange: boolean) => {
          if (canChange) {
            // reset
            this.handleTabClick.apply(this.domainTabs, [tab, header, newTabIndex]);
            this.currenTabIndex = newTabIndex;

          }
        });
      } else {
        this.handleTabClick.apply(this.domainTabs, [tab, header, newTabIndex]);
        this.currenTabIndex = newTabIndex;
      }
    }
  }

  updateDomainList(domainList: DomainRo[]) {
    this.domainList = domainList
    this.dataSource.data = this.domainList;

    if (!!this.domainList && this.domainList.length > 0) {
      this.selected = this.domainList[0];
    }
  }

  applyDomainFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }


  public domainSelected(domainSelected: DomainRo) {
    if (this.selected === domainSelected) {
      return;
    }
    if (this.isCurrentTabDirty()) {
      let canChangeTab = firstValueFrom(this.dialog.open(CancelDialogComponent).afterClosed());
      canChangeTab.then((canChange: boolean) => {
        if (canChange) {
          // reset
          this.selected = domainSelected;
        }
      });
    } else {
      this.selected = domainSelected;
    }
  }

  isCurrentTabDirty(): boolean {
    return false;
  }

  isDirty(): boolean {
    return this.isCurrentTabDirty();
  }

  get canNotDelete(): boolean {
    return !this.selected;
  }

  get editMode(): boolean {
    return this.isCurrentTabDirty();
  }
}
