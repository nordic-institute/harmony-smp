import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator} from "@angular/material/paginator";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {EditDomainService} from "../edit-domain/edit-domain.service";
import {DomainRo} from "../../common/model/domain-ro.model";
import {EditGroupService} from "./edit-group.service";
import {GroupRo} from "../../common/model/group-ro.model";
import {MemberTypeEnum} from "../../common/enums/member-type.enum";
import {ResourceDefinitionRo} from "../../system-settings/admin-extension/resource-definition-ro.model";


@Component({
  moduleId: module.id,
  templateUrl: './edit-group.component.html',
  styleUrls: ['./edit-group.component.css']
})
export class EditGroupComponent implements AfterViewInit, BeforeLeaveGuard {
  groupMembershipType: MemberTypeEnum = MemberTypeEnum.GROUP;
  domainList: DomainRo[] = [];
  groupList: GroupRo[] = [];

  _selectedDomain: DomainRo;
  _selectedGroup: GroupRo;
  _selectedDomainResourceDef: ResourceDefinitionRo[];

  get selectedDomain(): DomainRo {
    return this._selectedDomain;
  };

  @Input() set selectedDomain(domain: DomainRo) {
    this._selectedDomain = domain;
    if (!!this.selectedDomain) {
      this.refreshGroups();
      this.refreshDomainsResourceDefinitions();
    } else {
      this.groupList = [];
      this._selectedDomainResourceDef = [];
    }

  };

  get selectedGroup(): GroupRo {
    return this._selectedGroup;
  };

  @Input() set selectedGroup(group: GroupRo) {
    this._selectedGroup = group;
  };


  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private domainService: EditDomainService,
              private groupService: EditGroupService,
              private alertService: AlertMessageService) {

  }

  ngAfterViewInit() {
    this.refreshDomains();
  }

  refreshDomains() {
    this.domainService.getDomainsForGroupAdminUserObservable()
      .subscribe((result: DomainRo[]) => {
        this.updateDomainList(result)
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  refreshGroups() {
    if (!this.selectedDomain) {
      this.updateGroupList([]);
      return;
    }
    this.groupService.getDomainGroupsForGroupAdmin(this.selectedDomain)
      .subscribe((result: GroupRo[]) => {
        this.updateGroupList(result)
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  refreshDomainsResourceDefinitions() {
    this.domainService.getDomainResourceDefinitionsObservable(this.selectedDomain)
      .subscribe((result: ResourceDefinitionRo[]) => {
        this._selectedDomainResourceDef = result
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  updateDomainList(list: DomainRo[]) {
    this.domainList = list;
    if (!!this.domainList && this.domainList.length > 0) {

      this.selectedDomain = this.domainList[0];
    }
  }

  updateGroupList(list: GroupRo[]) {
    this.groupList = list
    if (!!this.groupList && this.groupList.length > 0) {
      this.selectedGroup = this.groupList[0];
    }
  }

  public onGroupSelected(event) {
    this.selectedGroup = event.value;
  }


  isDirty(): boolean {
    return false;
  }

}
