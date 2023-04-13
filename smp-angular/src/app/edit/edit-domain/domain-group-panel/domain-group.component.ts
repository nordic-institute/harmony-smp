import {Component, Input,} from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {finalize} from "rxjs/operators";
import {MatTableDataSource} from "@angular/material/table";
import {GroupRo} from "../../../common/model/group-ro.model";
import {EditDomainService} from "../edit-domain.service";

@Component({
  selector: 'domain-group-panel',
  templateUrl: './domain-group.component.html',
  styleUrls: ['./domain-group.component.scss']
})
export class DomainGroupComponent implements BeforeLeaveGuard {


  private _domain: DomainRo;

  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;

  displayedColumns: string[] = ['groupName', 'visibility', 'groupDescription'];
  dataSource: MatTableDataSource<GroupRo> = new MatTableDataSource();

  selectedGroup: GroupRo;

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
      this.loadTableData();
    } else {
      this.isLoadingResults = false;
    }
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
  };

  onEditSelectedButtonClicked() {
  };

  onDeleteSelectedButtonClicked() {
    if (!this._domain || !this._domain.domainId) {
      return;
    }
    if (!this.selectedGroup || !this.selectedGroup.groupId) {
      return;
    }

    this.editDomainService.deleteDomainGroupObservable(this._domain.domainId, this.selectedGroup.groupId).subscribe((result: GroupRo) => {
        if (result) {
          this.alertService.success("Domain group [" + result.groupName + "] deleted");
        }
      }, (error) => {
        this.alertService.error(error.error?.errorDescription)
    }
    )
  };

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
