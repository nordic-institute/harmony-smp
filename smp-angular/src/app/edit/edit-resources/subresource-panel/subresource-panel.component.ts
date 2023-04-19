import {Component, Input, OnInit, ViewChild,} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {MatPaginator} from "@angular/material/paginator";
import {GroupRo} from "../../../common/model/group-ro.model";
import {ResourceRo} from "../../../common/model/resource-ro.model";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {finalize} from "rxjs/operators";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {ResourceDefinitionRo} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {EditResourceService} from "../edit-resource.service";
import {SubresourceRo} from "../../../common/model/subresource-ro.model";
import {MatTableDataSource} from "@angular/material/table";
import {ConfirmationDialogComponent} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {SubresourceDialogComponent} from "./resource-dialog/subresource-dialog.component";
import {SubresourceDefinitionRo} from "../../../system-settings/admin-extension/subresource-definition-ro.model";
import {NavigationNode, NavigationService} from "../../../window/sidenav/navigation-model.service";


@Component({
  selector: 'subresource-panel',
  templateUrl: './subresource-panel.component.html',
  styleUrls: ['./subresource-panel.component.scss']
})
export class SubresourcePanelComponent implements OnInit, BeforeLeaveGuard {


  title: string = "Subresources";
  @Input() group: GroupRo;
  private _resource: ResourceRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];
  displayedColumns: string[] = ['identifierValue', 'identifierScheme'];
  dataSource: MatTableDataSource<SubresourceRo> = new MatTableDataSource();
  selected: SubresourceRo;
  filter: any = {};
  resultsLength = 0;
  isLoadingResults = false;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private editResourceService: EditResourceService,
              private navigationService: NavigationService,
              private alertService: AlertMessageService,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    // filter predicate for search the domain
    /*
        this.dataSource.filterPredicate =
          (data: SubresourceRo, filter: string) => {
            return !filter || -1 != data.subresourceId.toLowerCase().indexOf(filter.trim().toLowerCase());

          };

     */
  }

  @Input() set resource(resource: ResourceRo) {
    this._resource = resource;
    this.loadSubResources();
  }

  get resource(): ResourceRo {
    return this._resource;

  }

  getSubresourceDefinitions(): SubresourceDefinitionRo[] {
    if (!this._resource) {
      return null;
    }
    if (!this.domainResourceDefs) {
      return null;
    }

    let result: SubresourceDefinitionRo[] = this.domainResourceDefs.find(def => def.identifier == this._resource.resourceTypeIdentifier)?.subresourceDefinitions;
    return result

  }


  loadSubResources() {
    if (!this._resource) {
      return;
    }

    this.isLoadingResults = true;
    this.editResourceService.getSubResourcesForResource(this._resource)
      .pipe(
        finalize(() => {
          this.isLoadingResults = false;
        }))
      .subscribe((result: SubresourceRo[]) => {
          this.dataSource.data = [...result];

        }
      );
  }


  applySubResourceFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  get createSubResourceDisabled(): boolean {
    return !this._resource;
  }

  public onCreateResourceButtonClicked() {
    let subResDef = this.getSubresourceDefinitions();
    this.dialog.open(SubresourceDialogComponent, {
      data: {
        resource: this._resource,
        subresourceDefs: subResDef,
        subresource: this.createSubresource(subResDef),

        formTitle: "Create Subresourcedialog"
      }
    }).afterClosed().subscribe(value => {
      this.refresh();
    });
  }

  createSubresource(subResDef:SubresourceDefinitionRo[]): SubresourceRo {

    return {
      subresourceTypeIdentifier: !!subResDef && subResDef.length > 0 ?subResDef[0].identifier : "",
      identifierValue: "",
    }
  }

  public refresh() {
    if (this.paginator) {
      this.paginator.firstPage();
    }
    this.loadSubResources();
  }

  public onEditSelectedButtonClicked() {
    this.showSubresourceEditPanel(this.selected)
  }

  public showSubresourceEditPanel(subresource: SubresourceRo) {
    this.editResourceService.selectedResource = this.resource;
    this.editResourceService.selectedSubresource = subresource;

    let node:NavigationNode = this.createNew();
    this.navigationService.selected.children = [node]
    this.navigationService.select(node);

  }

  public createNew():NavigationNode{
    return {
      code: "subresource-document",
      icon: "description",
      name: "Edit subresource document",
      routerLink: "subresource-document",
      selected: true,
      tooltip: "",
      transient: true
    }
  }

  public onDeleteSelectedButtonClicked() {
    if (!this._resource || !this._resource.resourceId) {
      this.alertService.error("Can not delete subresource because of invalid resource data. Is resource selected?");
      return;
    }

    if (!this.selected || !this.selected.subresourceId) {
      this.alertService.error("Can not delete subresource because of invalid subresource data. Is subresource selected?");
      return;
    }

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete Resource with scheme from DomiSMP",
        description: "Action will permanently delete subresource  [" + this.selected.identifierScheme + "] and identifier: [" + this.selected.identifierValue + "]! " +
          "Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteSubResource(this.selected, this._resource);
      }
    });
  }

  deleteSubResource(subresource: SubresourceRo, resource: ResourceRo) {

    this.isLoadingResults = true;
    this.editResourceService.deleteSubresourceFromResource(subresource, resource)
      .pipe(
        finalize(() => {
          this.refresh();
          this.isLoadingResults = false;
        }))
      .subscribe((result: SubresourceRo) => {
          if (result) {
            this.alertService.success("Subresource  [" + this.selected.identifierScheme + "] and identifier: [" + this.selected.identifierValue + "] deleted.");
          }
        }, (error) => {
          this.alertService.error(error.error?.errorDescription);
        }
      );

  }


  public onResourceSelected(resource: ResourceRo) {
    this.selected = resource;
  }

  get disabledForm(): boolean {
    return !this._resource;
  }

  isDirty(): boolean {
    return false
  }
}






