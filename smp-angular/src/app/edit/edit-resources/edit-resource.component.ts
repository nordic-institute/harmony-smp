import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {DomainRo} from "../../common/model/domain-ro.model";
import {GroupRo} from "../../common/model/group-ro.model";
import {MemberTypeEnum} from "../../common/enums/member-type.enum";
import {ResourceDefinitionRo} from "../../system-settings/admin-extension/resource-definition-ro.model";
import {ResourceRo} from "../../common/model/resource-ro.model";
import {EditResourceController} from "./edit-resource.controller";


@Component({
  templateUrl: './edit-resource.component.html',
  styleUrls: ['./edit-resource.component.css']
})
export class EditResourceComponent implements OnInit, BeforeLeaveGuard {
  groupMembershipType: MemberTypeEnum = MemberTypeEnum.RESOURCE;


  displayedColumns: string[] = ['identifierValue', 'identifierScheme'];

  selected: ResourceRo;
  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private editResourceController: EditResourceController) {

  }

  ngOnInit() {
    console.log("EditResourceComponent: ngOnInit")
    if (!this.selectedResource) {
      this.editResourceController.refreshDomains();
    }
  }

  applyResourceFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.editResourceController.filter["filter"] = filterValue.trim().toLowerCase();
    this.editResourceController.refreshResources();
  }

  get resourceList(): ResourceRo[] {
    return this.editResourceController.resourceList;
  };

  get domainList(): DomainRo[] {
    return this.editResourceController.domainList;
  };

  get groupList(): GroupRo[] {
    return this.editResourceController.groupList;
  };

  get selectedDomain(): DomainRo {
    return this.editResourceController.selectedDomain;
  };

  @Input() set selectedDomain(domain: DomainRo) {
    this.editResourceController.selectedDomain = domain;
  };

  get selectedGroup(): GroupRo {
    return this.editResourceController.selectedGroup;
  };

  @Input() set selectedGroup(resource: GroupRo) {
    this.editResourceController.selectedGroup = resource;
  };

  get selectedResource(): ResourceRo {
    return this.editResourceController.selectedResource;
  };

  get selectedDomainResourceDefs(): ResourceDefinitionRo[] {
    return this.editResourceController._selectedDomainResourceDefs;
  }

  @Input() set selectedResource(resource: ResourceRo) {
    this.editResourceController.selectedResource = resource;
  };

  onResourceSelected(resource: ResourceRo) {
    this.selectedResource = resource;
  }

  get filterResourceResults(): boolean {
    return !!this.filter["filter"]
  }

  get disabledResourceFilter(): boolean {
    return !this.editResourceController.selectedGroup;
  }

  isDirty(): boolean {
    return false;
  }

  onPageChanged(page: PageEvent) {
    this.editResourceController.refreshResources();
  }

  get disabledResourcePagination(): boolean {
    return !this.editResourceController.selectedGroup;
  }

  get hasSubResources(): boolean {
    return this.selectedResourceDefinition?.subresourceDefinitions?.length > 0
  }

  get selectedResourceDefinition(): ResourceDefinitionRo {
    return this.editResourceController.selectedResourceDefinition;
  }
}
