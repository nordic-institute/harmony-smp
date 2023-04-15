import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {EditDomainService} from "./edit-domain.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {DomainRo} from "../../common/model/domain-ro.model";
import {CancelDialogComponent} from "../../common/dialogs/cancel-dialog/cancel-dialog.component";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTabGroup} from "@angular/material/tabs";
import {MemberTypeEnum} from "../../common/enums/member-type.enum";


@Component({
  moduleId: module.id,
  templateUrl: './edit-domain.component.html',
  styleUrls: ['./edit-domain.component.css']
})
export class EditDomainComponent implements OnInit, AfterViewInit, BeforeLeaveGuard {

  membershipType:MemberTypeEnum = MemberTypeEnum.DOMAIN;
  displayedColumns: string[] = ['domainCode'];
  dataSource: MatTableDataSource<DomainRo> = new MatTableDataSource();
  selected?: DomainRo;
  domainList: DomainRo[] = [];

  currenTabIndex: number = 0;
  handleTabClick: any;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('domainTabs') domainTabs: MatTabGroup;

  constructor(private domainService: EditDomainService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {

        domainService.onDomainUpdatedEvent().subscribe(updatedTruststore => {
            this.updateDomainList(updatedTruststore);
          }
        );

        domainService.getDomainsForDomainAdminUser();
  }


  ngOnInit(): void {
    // filter predicate for search the domain
    this.dataSource.filterPredicate =
      (data: DomainRo, filter: string) => {
        return !filter || -1 != data.domainCode.toLowerCase().indexOf(filter.trim().toLowerCase())
      };
  }

  ngAfterViewInit() {

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    // MatTab has only onTabChanged which is a bit to late. Register new listener to  internal
    // _handleClick handler
    this.registerTabClick();
  }

  registerTabClick(): void {
    // Get the handler reference
    this.handleTabClick = this.domainTabs._handleClick;

    this.domainTabs._handleClick = (tab, header, newTabIndex) => {

      if (newTabIndex == this.currenTabIndex) {
        return;
      }

      if (this.isCurrentTabDirty()) {
        let canChangeTab = this.dialog.open(CancelDialogComponent).afterClosed().toPromise<boolean>();
        canChangeTab.then((canChange: boolean) => {
          if (canChange) {
            // reset
            this.resetCurrentTabData()
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
  }

  applyDomainFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  resetUnsavedDataValidation() {
    // current tab not changed - OK to change it
    if (!this.isCurrentTabDirty()) {
      return true;
    }

    let canChangeTab = this.dialog.open(CancelDialogComponent).afterClosed().toPromise<boolean>();
    canChangeTab.then((canChange: boolean) => {
      if (canChange) {
        // reset
        this.resetCurrentTabData()
      }
    });
  }


  public domainSelected(domainSelected: DomainRo) {
    if (this.selected === domainSelected) {
      return;
    }
    if (this.isCurrentTabDirty()) {
      let canChangeTab = this.dialog.open(CancelDialogComponent).afterClosed().toPromise<boolean>();
      canChangeTab.then((canChange: boolean) => {
        if (canChange) {
          // reset
          this.resetCurrentTabData();
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
    return  this.isCurrentTabDirty();
  }



  resetCurrentTabData(): void {


  }

  get canNotDelete(): boolean {
    return !this.selected;
  }

  get editMode(): boolean {
    return this.isCurrentTabDirty();
  }
}
