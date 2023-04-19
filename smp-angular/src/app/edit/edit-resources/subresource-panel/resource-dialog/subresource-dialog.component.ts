import {Component, ElementRef, Inject, Input, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AlertMessageService} from "../../../../common/alert-message/alert-message.service";
import {VisibilityEnum} from "../../../../common/enums/visibility.enum";
import {GroupRo} from "../../../../common/model/group-ro.model";
import {ResourceRo} from "../../../../common/model/resource-ro.model";
import {DomainRo} from "../../../../common/model/domain-ro.model";
import {ResourceDefinitionRo} from "../../../../system-settings/admin-extension/resource-definition-ro.model";
import {EditGroupService} from "../../../edit-group/edit-group.service";
import {SubresourceRo} from "../../../../common/model/subresource-ro.model";
import {EditResourceService} from "../../edit-resource.service";



@Component({
  templateUrl: './subresource-dialog.component.html',
  styleUrls: ['./subresource-dialog.component.css']
})
export class SubresourceDialogComponent {
  formTitle = "Subresource dialog";
  resourceForm: FormGroup;
  message: string;
  messageType: string = "alert-error";
  @Input() resource: ResourceRo
  _subresource: SubresourceRo
  domain:DomainRo;
  subresourceDefs:ResourceDefinitionRo[];

  @ViewChild('identifierValue', {static: false}) identifierValue: ElementRef;
  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              public dialogRef: MatDialogRef<SubresourceDialogComponent>,
              private editGroupService: EditResourceService,
              private alertService: AlertMessageService,
              private formBuilder: FormBuilder
  ) {
    dialogRef.disableClose = true;//disable default close operation
    this.formTitle = data.formTitle;


    this.resourceForm = formBuilder.group({
      'identifierValue': new FormControl({value: null}),
      'identifierScheme': new FormControl({value: null}),
      'subresourceTypeIdentifier': new FormControl({value: null}),
      '': new FormControl({value: null})
    });
    this.resource = data.resource;
    this.subresource = data.subresource;
    this.subresourceDefs = data.subresourceDefs;
    this.domain = data.domain;
  }

  get newMode(): boolean {
    return !this._subresource?.subresourceId
  }

  get subresource(): SubresourceRo {
    let entity = {...this._subresource};
    entity.identifierScheme = this.resourceForm.get('identifierValue').value;
    entity.identifierValue = this.resourceForm.get('identifierScheme').value;
    entity.subresourceTypeIdentifier = this.resourceForm.get('subresourceTypeIdentifier').value;
    return entity;
  }

  @Input() set subresource(value: SubresourceRo) {
    this._subresource = value;

    if (!!value) {
      this.resourceForm.enable();
      this.resourceForm.controls['identifierValue'].setValue(value.identifierValue);
      this.resourceForm.controls['identifierScheme'].setValue(value.identifierScheme);
      this.resourceForm.controls['subresourceTypeIdentifier'].setValue(value.subresourceTypeIdentifier);
      // control disable enable did not work??
      if (this.newMode) {
        this.resourceForm.controls['identifierValue'].enable();
        this.resourceForm.controls['identifierScheme'].enable();
        this.resourceForm.controls['subresourceTypeIdentifier'].enable();
      } else {
        this.resourceForm.controls['identifierValue'].disable();
        this.resourceForm.controls['identifierScheme'].disable();
        this.resourceForm.controls['subresourceTypeIdentifier'].disable();
      }
    } else {
      this.resourceForm.disable();
      this.resourceForm.controls['identifierValue'].setValue("");
      this.resourceForm.controls['identifierScheme'].setValue("");
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
    return this.resourceForm.valid && this.resourceForm.dirty;
  }

  public onSaveButtonClicked() {

    let resource = this.resource;
    if (this.newMode) {
      this.createResource(resource);
    }
  }

  public createResource(resource: ResourceRo) {

        this.editGroupService.createSubResourceForResource(this.subresource, this.resource).subscribe((result: SubresourceRo) => {
          if (!!result) {
            this.closeDialog();
          }
        }, (error) => {
          this.alertService.error(error.error?.errorDescription)
        });

  }


  public setFocus() {
    setTimeout(() => this.identifierValue.nativeElement.focus());
  }
}
