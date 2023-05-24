import {Component, Input,} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {GroupRo} from "../../../common/model/group-ro.model";
import {ResourceRo} from "../../../common/model/resource-ro.model";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {ResourceDefinitionRo} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {EditResourceService} from "../edit-resource.service";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {VisibilityEnum} from "../../../common/enums/visibility.enum";
import {NavigationNode, NavigationService} from "../../../window/sidenav/navigation-model.service";


@Component({
  selector: 'resource-detail-panel',
  templateUrl: './resource-details-panel.component.html',
  styleUrls: ['./resource-details-panel.component.scss']
})
export class ResourceDetailsPanelComponent implements BeforeLeaveGuard {

  readonly groupVisibilityOptions = Object.keys(VisibilityEnum)
    .map(el => {
      return {key: el, value: VisibilityEnum[el]}
    });

  title: string = "Resources";
  private _resource: ResourceRo;
  @Input() private group: GroupRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];

  resourceForm: FormGroup;


  constructor(private editResourceService: EditResourceService,
              private navigationService: NavigationService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private formBuilder: FormBuilder) {
    this.resourceForm = formBuilder.group({
      'identifierValue': new FormControl({value: null}),
      'identifierScheme': new FormControl({value: null}),
      'visibility': new FormControl({value: null}),
      'resourceTypeIdentifier': new FormControl({value: null}),
      '': new FormControl({value: null})
    });
  }

  get resource(): ResourceRo {
    let resource = {...this._resource};
    resource.identifierScheme = this.resourceForm.get('identifierScheme').value;
    resource.identifierValue = this.resourceForm.get('identifierValue').value;
    resource.resourceTypeIdentifier = this.resourceForm.get('resourceTypeIdentifier').value;
    resource.visibility = this.resourceForm.get('visibility').value;
    return resource;
  }

  @Input() set resource(value: ResourceRo) {
    this._resource = value;
    this.resourceForm.disable();
    if (!!value) {
      this.resourceForm.controls['identifierValue'].setValue(value.identifierValue);
      this.resourceForm.controls['identifierScheme'].setValue(value.identifierScheme);
      this.resourceForm.controls['resourceTypeIdentifier'].setValue(value.resourceTypeIdentifier);
      this.resourceForm.controls['visibility'].setValue(value.visibility);

    } else {
      this.resourceForm.controls['identifierValue'].setValue("");
      this.resourceForm.controls['identifierScheme'].setValue("");
      this.resourceForm.controls['resourceTypeIdentifier'].setValue("");
      this.resourceForm.controls['visibility'].setValue("");
    }
    this.resourceForm.markAsPristine();
  }

  onShowButtonDocumentClicked(){
    // set selected resource
    this.editResourceService.selectedResource = this.resource;

    let node:NavigationNode = this.createNew();
    this.navigationService.selected.children = [node]
    this.navigationService.select(node);
  }

  public createNew():NavigationNode{
    return {
      code: "resource-document",
      icon: "note",
      name: "Edit resource document",
      routerLink: "resource-document",
      selected: true,
      tooltip: "",
      transient: true
    }
  }

  isDirty(): boolean {
    return false;
  }

  get visibilityDescription(): string {
    if (this.resourceForm.get('visibility').value == VisibilityEnum.Private) {
      return "The private resource is accessible only to the resource members!"
    }
    if (this.group.visibility == VisibilityEnum.Private) {
      return "The resource belongs to the private group. The resource is accessible only to the members of the group (direct and indirect group members)!"
    }
    if (this.domain.visibility == VisibilityEnum.Private) {
      return "The resource belongs to the private domain. The resource is accessible only to the members of the domain (direct and indirect domain members)!"
    }
    return "The resource is public on the public group and the public domain. The resource data is accessible to all users."
  }
}






