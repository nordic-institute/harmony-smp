import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator} from "@angular/material/paginator";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {EditDomainService} from "../edit-domain/edit-domain.service";
import {DomainRo} from "../../common/model/domain-ro.model";
import {GroupRo} from "../../common/model/group-ro.model";
import {MemberTypeEnum} from "../../common/enums/member-type.enum";
import {ResourceDefinitionRo} from "../../system-settings/admin-extension/resource-definition-ro.model";
import {EditGroupService} from "../edit-group/edit-group.service";
import {ResourceRo} from "../../common/model/resource-ro.model";
import {EditResourceService} from "./edit-resource.service";
import {group} from "@angular/animations";
import {TableResult} from "../../common/model/table-result.model";


@Component({
  moduleId: module.id,
  templateUrl: './edit-resource.component.html',
  styleUrls: ['./edit-resource.component.css']
})
export class EditResourceComponent implements AfterViewInit, BeforeLeaveGuard {
  groupMembershipType: MemberTypeEnum = MemberTypeEnum.RESOURCE;
  domainList: DomainRo[] = [];
  groupList: GroupRo[] = [];
  resourceList: ResourceRo[] = [];
  _selectedDomain: DomainRo;
  _selectedGroup: GroupRo;
  _selectedResource: ResourceRo;
  _selectedDomainResourceDef: ResourceDefinitionRo[];

  displayedColumns: string[] = ['identifierValue', 'identifierScheme'];

  data: ResourceRo[] = [];
  selected: ResourceRo;
  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;

  @ViewChild(MatPaginator) paginator: MatPaginator;

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

  @Input() set selectedGroup(resource: GroupRo) {
    this._selectedGroup = resource;
    if (!!this._selectedGroup) {
      this.refreshResources();
    } else {
      this.resourceList = [];
    }
  };
  get selectedResource(): ResourceRo {
    return this._selectedResource;
  };

  @Input() set selectedResource(resource: ResourceRo) {
    this._selectedResource = resource;
  };

  onResourceSelected(resource: ResourceRo){
    this.selectedResource = resource;
  }

  constructor(private domainService: EditDomainService,
              private groupService: EditGroupService,
              private resourceService: EditResourceService,
              private alertService: AlertMessageService) {

  }

  ngAfterViewInit() {
    this.refreshDomains();
  }

  refreshDomains() {
    this.domainService.getDomainsForResourceAdminUserObservable()
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
    this.groupService.getDomainGroupsForResourceAdminObservable(this.selectedDomain)
      .subscribe((result: GroupRo[]) => {
        this.updateGroupList(result)
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  refreshResources() {
    if (!this.selectedGroup) {
      this.updateResourceList([]);
      return;
    }

    this.resourceService.getGroupResourcesForResourceAdminObservable(this.selectedGroup, this.selectedDomain,  this.filter, this.paginator.pageIndex, this.paginator.pageSize)
      .subscribe((result: TableResult<ResourceRo>) => {
        console.log("got resources: " + JSON.stringify(result))
        this.updateResourceList(result.serviceEntities)
        this.data = [...result.serviceEntities];
        this.resultsLength = result.count;
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

  updateResourceList(list: ResourceRo[]) {
    this.resourceList = list
    if (!!this.resourceList && this.resourceList.length > 0) {
      this.selectedResource = this.resourceList[0];
    }
  }

  applyResourceFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.filter["filter"] = filterValue.trim().toLowerCase();
    this.refreshResources();
  }

  get disabledResourceFilter(): boolean{
    return !this._selectedGroup;
  }

  isDirty(): boolean {
    return false;
  }

}
