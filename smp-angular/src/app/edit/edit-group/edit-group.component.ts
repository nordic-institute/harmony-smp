import {Component, ViewChild} from '@angular/core';
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {MatTableDataSource} from "@angular/material/table";
import {GroupRo} from "../../common/model/group-ro.model";
import {MatPaginator} from "@angular/material/paginator";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {EditGroupService} from "./edit-group.service";


@Component({
  moduleId: module.id,
  templateUrl: './edit-group.component.html',
  styleUrls: ['./edit-group.component.css']
})
export class EditGroupComponent implements BeforeLeaveGuard {
  displayedColumns: string[] = ['groupName'];
  dataSource: MatTableDataSource<GroupRo> = new MatTableDataSource();
  selected?: GroupRo;

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private editGroupService: EditGroupService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {
    /*
        editGroupService.onGroupUpdatedEvent().subscribe((groupList: GroupRo[]) => {
            this.updateGroupList(groupList);
          }
        );

        editGroupService.getUserAdminGroups();

     */
  }


  updateGroupList(groupList: GroupRo[]) {
    this.dataSource.data = groupList;
  }


  isDirty(): boolean {
    return false;
  }

  applyGroupFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  onGroupSelected(group: GroupRo) {
    this.selected = group;
  }


  get groupSelected(): boolean {
    return !!this.selected;
  }
}
