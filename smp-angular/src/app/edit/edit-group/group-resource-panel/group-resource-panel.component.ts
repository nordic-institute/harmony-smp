import {Component, Input, ViewChild,} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {GroupRo} from "../../../common/model/group-ro.model";
import {ResourceRo} from "../../../common/model/resource-ro.model";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {EditGroupService} from "../edit-group.service";
import {finalize} from "rxjs/operators";
import {TableResult} from "../../../common/model/table-result.model";
import {ConfirmationDialogComponent} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {ResourceDialogComponent} from "./resource-dialog/resource-dialog.component";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {ResourceDefinitionRo} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {VisibilityEnum} from "../../../common/enums/visibility.enum";


@Component({
  selector: 'group-resource-panel',
  templateUrl: './group-resource-panel.component.html',
  styleUrls: ['./group-resource-panel.component.scss']
})
export class GroupResourcePanelComponent implements BeforeLeaveGuard {

  title: string = "Group resources";
  private _group: GroupRo;
  @Input() resource: ResourceRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];
  displayedColumns: string[] = ['identifierValue', 'identifierScheme'];
  data: ResourceRo[] = [];
  selected: ResourceRo;
  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private editGroupService: EditGroupService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {
  }


  @Input() set group(value: GroupRo) {
    if (this._group == value) {
      return;
    }
    this._group = value;
    this.title = "Group resources" + (!!this._group?": [" +this._group.groupName+"]":"")
    if (!!value) {
      this.loadGroupResources();
    } else {
      this.isLoadingResults = false;
    }
  }

  get group(){
    return this._group;
  }

  onPageChanged(page: PageEvent) {
    this.loadGroupResources();
  }

  loadGroupResources() {
    if (!this._group) {
      return;
    }

    this.isLoadingResults = true;
    this.editGroupService.getGroupResourcesForGroupAdminObservable(this._group,this.domain, this.filter, this.paginator.pageIndex, this.paginator.pageSize)
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe((result: TableResult<ResourceRo>) => {
          this.data = [...result.serviceEntities];
          this.resultsLength = result.count;
          this.isLoadingResults = false;
        }
      );
  }


  applyResourceFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.filter["filter"] = filterValue.trim().toLowerCase();
    this.refresh();
  }

  get createResourceDisabled(): boolean {
    return !this._group;
  }

  public onCreateResourceButtonClicked() {
    this.showResourceEditDialog(this.crateResource());
  }

  crateResource ():ResourceRo {
    return {
      resourceTypeIdentifier: !!this.domainResourceDefs && this.domainResourceDefs.length>0 ? this.domainResourceDefs[0].identifier:"",
      identifierValue: "",
      smlRegistered: false,
      visibility: VisibilityEnum.Public

    }
  }

  public refresh() {
    if (this.paginator) {
      this.paginator.firstPage();
    }
    this.loadGroupResources();
  }
  public onEditSelectedButtonClicked() {
    this.showResourceEditDialog(this.selected)
  }
  public showResourceEditDialog(resource: ResourceRo) {
    this.dialog.open(ResourceDialogComponent, {
      data: {
        resource: resource,
        group: this._group,
        domain: this.domain,
        domainResourceDefs: this.domainResourceDefs,
        formTitle: "Group details dialog"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }

  public onDeleteSelectedButtonClicked() {
    if (!this._group || !this._group.groupId) {
      this.alertService.error("Can not delete group because of invalid domain data. Is group selected?");
      return;
    }

    if (!this.selected || !this.selected.resourceId) {
      this.alertService.error("Can not delete resource because of invalid resource data. Is resource selected?");
      return;
    }

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete Resource with scheme from DomiSMP",
        description: "Action will permanently delete resource  [" + this.selected.identifierScheme + "] and identifier: [" + this.selected.identifierValue + "]! " +
          "Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteResource(this.group, this.selected);
      }
    });
  }

  deleteResource(group: GroupRo, resource: ResourceRo) {
    this.isLoadingResults = true;
    this.editGroupService.deleteResourceFromGroup(resource, this._group, this.domain)
      .pipe(
        finalize(() => {
          this.refresh();
          this.isLoadingResults = false;
        }))
      .subscribe((result: ResourceRo) => {
          if(result) {
            this.alertService.success("Resource  [" + this.selected.identifierScheme + "] and identifier: [" + this.selected.identifierValue + "] deleted.");
          }
        }, (error)=> {
          this.alertService.error(error.error?.errorDescription);
        }
      );
  }


  public onResourceSelected(resource: ResourceRo) {
    this.selected = resource;
  }

  get disabledForm(): boolean {
    return !this._group;
  }

  isDirty(): boolean {
    return false
  }
}






