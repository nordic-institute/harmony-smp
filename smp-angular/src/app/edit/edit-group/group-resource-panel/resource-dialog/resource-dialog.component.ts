import {Component, ElementRef, Inject, Input, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AlertMessageService} from "../../../../common/alert-message/alert-message.service";
import {VisibilityEnum} from "../../../../common/enums/visibility.enum";
import {GroupRo} from "../../../../common/model/group-ro.model";
import {ResourceRo} from "../../../../common/model/resource-ro.model";
import {DomainRo} from "../../../../common/model/domain-ro.model";
import {ResourceDefinitionRo} from "../../../../system-settings/admin-extension/resource-definition-ro.model";
import {EditGroupService} from "../../edit-group.service";
import {GlobalLookups} from "../../../../common/global-lookups";
import {EntityStatus} from "../../../../common/enums/entity-status.enum";


@Component({
  templateUrl: './resource-dialog.component.html',
  styleUrls: ['./resource-dialog.component.css']
})
export class ResourceDialogComponent {

  readonly groupVisibilityOptions = Object.keys(VisibilityEnum)
   .map(el => {
      return {key: el, value: VisibilityEnum[el]}
    });

  formTitle = "Resource dialog";
  resourceForm: FormGroup;
  message: string;
  messageType: string = "alert-error";
  group: GroupRo;
  _resource: ResourceRo
  domain:DomainRo;
  domainResourceDefs:ResourceDefinitionRo[];

  participantSchemePattern = '^[a-z0-9]+-[a-z0-9]+-[a-z0-9]+$';
  participantSchemeMessage:string;
  submitInProgress:boolean = false;

  @ViewChild('identifierValue', {static: false}) identifierValue: ElementRef;
  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              public lookups: GlobalLookups,
              public dialogRef: MatDialogRef<ResourceDialogComponent>,
              private editGroupService: EditGroupService,
              private alertService: AlertMessageService,
              private formBuilder: FormBuilder
  ) {

    if (this.lookups.cachedApplicationConfig) {
      this.participantSchemePattern = this.lookups.cachedApplicationConfig.participantSchemaRegExp != null ?
        this.lookups.cachedApplicationConfig.participantSchemaRegExp : ".*"

      this.participantSchemeMessage = this.lookups.cachedApplicationConfig.participantSchemaRegExpMessage;
    }

    dialogRef.disableClose = true;//disable default close operation
    this.formTitle = data.formTitle;


    this.resourceForm = formBuilder.group({
      'identifierValue': new FormControl({value: null}, ),
      'identifierScheme': new FormControl({value: null},[Validators.pattern(this.participantSchemePattern)]),
      'visibility': new FormControl({value: null}),
      'resourceTypeIdentifier': new FormControl({value: null}),
      '': new FormControl({value: null})
    });
    if (!!lookups.cachedApplicationConfig.partyIDSchemeMandatory) {
      this.resourceForm.controls['identifierScheme'].addValidators(Validators.required);
    }
    this.resource = data.resource;
    this.group = data.group;
    this.domain = data.domain;
    this.domainResourceDefs = data.domainResourceDefs;


  }

  get newMode(): boolean {
    return !this._resource?.resourceId
  }

  get resource(): ResourceRo {
    let resource = {...this._resource};
    resource.identifierValue = this.resourceForm.get('identifierValue').value;
    resource.identifierScheme = this.resourceForm.get('identifierScheme').value;
    resource.resourceTypeIdentifier = this.resourceForm.get('resourceTypeIdentifier').value;
    resource.visibility = this.resourceForm.get('visibility').value;
    return resource;
  }

  @Input() set resource(value: ResourceRo) {
    this._resource = value;

    if (!!value) {
      this.resourceForm.enable();
      this.resourceForm.controls['identifierValue'].setValue(value.identifierValue);
      this.resourceForm.controls['identifierScheme'].setValue(value.identifierScheme);
      this.resourceForm.controls['resourceTypeIdentifier'].setValue(value.resourceTypeIdentifier);
      // control disable enable did not work??
      if (this.newMode) {
        this.resourceForm.controls['identifierValue'].enable();
        this.resourceForm.controls['identifierScheme'].enable();
        this.resourceForm.controls['resourceTypeIdentifier'].enable();
      } else {
        this.resourceForm.controls['identifierValue'].disable();
        this.resourceForm.controls['identifierScheme'].disable();
        this.resourceForm.controls['resourceTypeIdentifier'].disable();
      }

      this.resourceForm.controls['visibility'].setValue(value.visibility);

    } else {
      this.resourceForm.disable();
      this.resourceForm.controls['identifierValue'].setValue("");
      this.resourceForm.controls['identifierScheme'].setValue("");
      this.resourceForm.controls['visibility'].setValue("");
      this.resourceForm.controls['resourceTypeIdentifier'].setValue("");
    }

    this.resourceForm.markAsPristine();
  }

  clearAlert() {
    this.message = null;
    this.messageType = null;
  }


  closeDialog() {
    this.dialogRef.close()
  }

  get submitButtonEnabled(): boolean {
    return this.resourceForm.valid && this.resourceForm.dirty && !this.submitInProgress;
  }

  public onSaveButtonClicked() {

    let resource = this.resource;
    if (this.newMode) {
      this.createResource(resource);
    } else {
      this.saveResource(resource);
    }
  }

  public createResource(resource: ResourceRo) {

        this.submitInProgress = true;
        this.editGroupService.createResourceForGroup(this.resource, this.group, this.domain).subscribe((result: ResourceRo) => {
          if (!!result) {
            this.closeDialog();
          }
          this.submitInProgress = false;
        }, (error) => {
          this.alertService.error(error.error?.errorDescription)
          this.submitInProgress = false;
        });

  }

  public saveResource(resource: ResourceRo) {
    this.submitInProgress = true;
    this.editGroupService.updateResourceForGroup(this.resource, this.group, this.domain).subscribe((result: ResourceRo) => {
      if (!!result) {
        this.closeDialog();
      }
      this.submitInProgress = false;
    }, (error) => {
      this.alertService.error(error.error?.errorDescription)
      this.submitInProgress = false;
    });
  }

  public setFocus() {
    setTimeout(() => this.identifierValue.nativeElement.focus());
  }
}
