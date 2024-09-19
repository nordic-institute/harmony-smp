import {Component, Input,} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {GroupRo} from "../../../common/model/group-ro.model";
import {ResourceRo} from "../../../common/model/resource-ro.model";
import {
  AlertMessageService
} from "../../../common/alert-message/alert-message.service";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {
  ResourceDefinitionRo
} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {EditResourceService} from "../edit-resource.service";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {VisibilityEnum} from "../../../common/enums/visibility.enum";
import {
  NavigationNode,
  NavigationService
} from "../../../window/sidenav/navigation-model.service";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import {
  WindowSpinnerService
} from "../../../common/services/window-spinner.service";
import {EditResourceController} from "../edit-resource.controller";


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

  title = "";
  visibilityDescription = "";
  private _resource: ResourceRo;
  @Input() private group: GroupRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];

  resourceForm: FormGroup;


  constructor(private editResourceService: EditResourceService,
              private editResourceController: EditResourceController,
              private navigationService: NavigationService,
              private alertService: AlertMessageService,
              private windowSpinnerService: WindowSpinnerService,
              private dialog: MatDialog,
              private formBuilder: FormBuilder,
              private translateService: TranslateService) {
    this.resourceForm = formBuilder.group({
      'identifierValue': new FormControl({value: null}),
      'identifierScheme': new FormControl({value: null}),
      'visibility': new FormControl({value: null}),
      'resourceTypeIdentifier': new FormControl({value: null}),
      'reviewEnabled': new FormControl({value: null}),
      '': new FormControl({value: null})
    });
    this.translateService.get("resource.details.panel.title").subscribe(value => this.title = value);
    (async () => await this.updateVisibilityDescription())();
  }

  get resource(): ResourceRo {
    let resource = {...this._resource};
    resource.identifierScheme = this.resourceForm.get('identifierScheme').value;
    resource.identifierValue = this.resourceForm.get('identifierValue').value;
    resource.resourceTypeIdentifier = this.resourceForm.get('resourceTypeIdentifier').value;
    resource.visibility = this.resourceForm.get('visibility').value;
    resource.reviewEnabled = this.resourceForm.get('reviewEnabled').value;
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
      this.resourceForm.controls['reviewEnabled'].setValue(value.reviewEnabled);
      // only allow visibility and reviewEnabled changes for group-admin and resource-admin
      this.resourceForm.controls['visibility'].enable();
      this.resourceForm.controls['reviewEnabled'].enable();

    } else {
      this.resourceForm.controls['identifierValue'].setValue("");
      this.resourceForm.controls['identifierScheme'].setValue("");
      this.resourceForm.controls['resourceTypeIdentifier'].setValue("");
      this.resourceForm.controls['visibility'].setValue("");
      this.resourceForm.controls['reviewEnabled'].setValue(false);
    }
    (async () => await this.updateVisibilityDescription())();
    this.resourceForm.markAsPristine();
  }

  async onShowButtonDocumentClicked() {
    // set selected resource
    this.editResourceService.selectedResource = this.resource;

    let node: NavigationNode = await this.createNewDocumentNavigationNode();
    this.navigationService.selected.children = [node]
    this.navigationService.select(node);
  }

  public async createNewDocumentNavigationNode() {
    return {
      code: "resource-document",
      icon: "note",
      name: await lastValueFrom(this.translateService.get("resource.details.panel.label.resource.name")),
      routerLink: "resource-document",
      selected: true,
      tooltip: "",
      transient: true,
      i18n: "navigation.label.edit.resource.document"
    }
  }

  isDirty(): boolean {
    return false;
  }

  async updateVisibilityDescription() {
    if (this.resourceForm.get('visibility').value == VisibilityEnum.Private) {
      this.visibilityDescription = await lastValueFrom(this.translateService.get("resource.details.panel.label.resource.visibility.private"));
    } else if (this.group?.visibility == VisibilityEnum.Private) {
      this.visibilityDescription = await lastValueFrom(this.translateService.get("resource.details.panel.label.resource.visibility.private.group"));
    } else if (this.domain?.visibility == VisibilityEnum.Private) {
      this.visibilityDescription = await lastValueFrom(this.translateService.get("resource.details.panel.label.resource.visibility.private.domain"));
    } else {
      this.visibilityDescription = await lastValueFrom(this.translateService.get("resource.details.panel.label.resource.visibility.public"));
    }
  }

  get submitButtonEnabled(): boolean {
    return this.resourceForm.valid && this.resourceForm.dirty;
  }

  get resetButtonEnabled(): boolean {
    return this.resourceForm.dirty;
  }

  public onSaveButtonClicked(): void {
    this.windowSpinnerService.showSpinner = true;
    let updatedResource: ResourceRo = this.resource;
    this.editResourceService.updateResourceForGroup(updatedResource, this.group, this.domain).subscribe({
      next: (result: ResourceRo): void => {
        try {
          if (!!result) {
            this.alertService.successForTranslation("resource.details.panel.alert.resource.saved");
            this.editResourceController.selectedResourceUpdated(result);
            this.resource = result;
            this.resourceForm.markAsPristine();
          }
        } finally {
          this.windowSpinnerService.showSpinner = false;
        }
      }, error: (err: any): void => {
        this.alertService.error(err.error?.errorDescription)
        this.windowSpinnerService.showSpinner = false;
      }
    });
  }

  public onResetButtonClicked() {
    this.resourceForm.reset(this._resource);
  }
}
