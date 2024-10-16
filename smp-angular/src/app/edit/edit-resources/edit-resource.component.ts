import {Component, Input, OnInit,} from '@angular/core';
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {PageEvent} from "@angular/material/paginator";
import {DomainRo} from "../../common/model/domain-ro.model";
import {GroupRo} from "../../common/model/group-ro.model";
import {MemberTypeEnum} from "../../common/enums/member-type.enum";
import {
  ResourceDefinitionRo
} from "../../system-settings/admin-extension/resource-definition-ro.model";
import {ResourceRo} from "../../common/model/resource-ro.model";
import {EditResourceController} from "./edit-resource.controller";
import {
  SmpTableColDef
} from "../../common/components/smp-table/smp-table-coldef.model";


@Component({
  templateUrl: './edit-resource.component.html',
  styleUrls: ['./edit-resource.component.css']
})
export class EditResourceComponent implements OnInit, BeforeLeaveGuard {
  groupMembershipType: MemberTypeEnum = MemberTypeEnum.RESOURCE;

  selected: ResourceRo;
  isLoadingResults = false;
  dataSource: EditResourceController;
  // define columns for smp-table
  displayedColumns: string[] = ['identifierValue', 'identifierScheme'];
  columns: SmpTableColDef[];

  constructor(private editResourceController: EditResourceController) {
    this.dataSource = editResourceController;
    this.columns = [
      {
        columnDef: 'identifierScheme',
        header: 'edit.resource.label.identifier.scheme',
        cell: (row: ResourceRo) => row.identifierScheme,
        style: 'flex-grow: 0;flex-basis: 180px;'
      } as SmpTableColDef,
      {
        columnDef: 'identifierValue',
        header: 'edit.resource.label.identifier.value',
        cell: (row: ResourceRo) => row.identifierValue
      } as SmpTableColDef
    ];
  }

  ngOnInit() {
    console.log("EditResourceComponent: ngOnInit  " + this.columns.length);
    this.editResourceController.refreshDataOnDataChange();

    if (!this.selectedResource) {
      this.editResourceController.refreshDomains();
    } else {
      // always refresh resources when selected resource is set
      this.editResourceController.refreshResources();
    }
  }

  ngAfterViewInit(): void {
  }


  onFilterChangedEvent(filter: string) {
    this.editResourceController.applyResourceFilter(filter);
  }

  get hasResources(): boolean {
    return this.editResourceController.data?.length > 0;
  }

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


  get selectedDomainResourceDefs(): ResourceDefinitionRo[] {
    return this.editResourceController._selectedDomainResourceDefs;
  }

  get selectedResource(): ResourceRo {
    return this.editResourceController.selectedResource;
  };

  @Input() set selectedResource(resource: ResourceRo) {
    this.editResourceController.selectedResource = resource;
  };

  onResourceSelected(resource: ResourceRo) {
    this.selectedResource = resource;
  }

  get filterResourceResults(): boolean {
    return !!this.editResourceController.resourcesFilter;
  }

  get disabledResourceFilter(): boolean {
    return !this.editResourceController.filteredData;
  }

  get isLoading(): boolean {
    return this.editResourceController.isLoadingResults;
  }

  get dataLength(): number {
    return this.editResourceController.dataLength;
  }

  isDirty(): boolean {
    return false;
  }

  onPageChanged(page: PageEvent) {
    this.editResourceController.applyResourcePage(page.pageIndex, page.pageSize);
  }

  get hasSubResources(): boolean {
    return this.selectedResourceDefinition?.subresourceDefinitions?.length > 0
  }

  get selectedResourceDefinition(): ResourceDefinitionRo {
    return this.editResourceController.selectedResourceDefinition;
  }
}
