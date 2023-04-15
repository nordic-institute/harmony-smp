import {Component, ViewChild} from '@angular/core';
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator} from "@angular/material/paginator";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {EditDomainService} from "../edit-domain/edit-domain.service";
import {DomainRo} from "../../common/model/domain-ro.model";
import {EditGroupService} from "./edit-group.service";
import {GroupRo} from "../../common/model/group-ro.model";
import {MemberTypeEnum} from "../../common/enums/member-type.enum";


@Component({
  moduleId: module.id,
  templateUrl: './edit-group.component.html',
  styleUrls: ['./edit-group.component.css']
})
export class EditGroupComponent implements BeforeLeaveGuard {
  groupMembershipType:MemberTypeEnum = MemberTypeEnum.GROUP;
  domainList: DomainRo[] = [];
  groupList: GroupRo[] = [];

  selectedDomain: DomainRo;
  selectedGroup: GroupRo;

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private domainService: EditDomainService,
              private groupService: EditGroupService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {

    domainService.onDomainUpdatedEvent().subscribe(list => {
        this.updateDomainList(list);
      }
    );

    groupService.onGroupUpdatedEvent().subscribe(list => {
        this.updateGroupList(list);
      }
    );
    domainService.getDomainsForGroupAdminUser();
  }

  updateDomainList(list: DomainRo[]) {
    this.domainList = list
  }

  updateGroupList(list: GroupRo[]) {
    this.groupList = list
  }

  public onDomainSelected(event) {
    this.selectedDomain = event.value;
    if (!!this.selectedDomain) {
      this.groupService.getDomainGroupsForGroupAdmin(this.selectedDomain.domainId);
    }

  }

  public onGroupSelected(event) {
    this.selectedGroup = event.value;
  }


  isDirty(): boolean {
    return false;
  }

}
