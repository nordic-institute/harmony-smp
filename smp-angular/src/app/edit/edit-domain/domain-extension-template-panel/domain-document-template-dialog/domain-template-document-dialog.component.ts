import {Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {DomainRo} from "../../../../common/model/domain-ro.model";
import {AlertMessageService} from "../../../../common/alert-message/alert-message.service";
import {EditDomainService} from "../../edit-domain.service";
import {DocumentLevelType} from "../../../../common/enums/documetn-reference-type.enum";
import {DomainDocumentTemplateRo} from "../../../../common/model/domain-document-template.ro";
import {ResourceDefinitionRo} from "../../../../system-settings/admin-extension/resource-definition-ro.model";
import {SubresourceDefinitionRo} from "../../../../system-settings/admin-extension/subresource-definition-ro.model";

@Component({
  templateUrl: './domain-template-document-dialog.component.html',
  styleUrls: ['./domain-template-document-dialog.component.css'],
  standalone: false
})
export class DomainDocumentTemplateDialog {

  readonly DocumentLevelTypeOptions = Object.keys(DocumentLevelType)
    .map(el => {
      return {key: el, value: DocumentLevelType[el]}
    });
  formTitle = "";
  templateForm: FormGroup;

  domainResourceDefs: ResourceDefinitionRo[] = [];
  message: string;
  messageType: string = "alert-error";

  _template: DomainDocumentTemplateRo;
  _currentDomain: DomainRo;
  _selectedResourceDef: ResourceDefinitionRo;


  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private editDomainService: EditDomainService,
              public dialogRef: MatDialogRef<DomainDocumentTemplateDialog>,
              private alertService: AlertMessageService,
              private formBuilder: FormBuilder
  ) {
    dialogRef.disableClose = true;//disable default close operation
    this.formTitle = data.formTitle;
    this._currentDomain = data.domain;
    this.domainResourceDefs = data.domainResourceDefs;

    this.templateForm = formBuilder.group({
      'documentLevel': new FormControl({value: null}, Validators.required),
      'resourceDefIdentifier': new FormControl({value: null}, Validators.required),
      'subresourceDefIdentifier': new FormControl({value: null}, Validators.required),
    });
    this.template = data.template;
  }

  get newMode(): boolean {
    return !this._template?.templateId
  }

  @Input() set template(value: DomainDocumentTemplateRo) {
    this._template = value;
    if (!!value) {
      this.templateForm.enable();
      this.templateForm.controls['documentLevel'].setValue(value.documentLevel);
      this.templateForm.controls['resourceDefIdentifier'].setValue(value.resourceDefIdentifier);
      this.templateForm.controls['subresourceDefIdentifier'].setValue(value.subresourceDefIdentifier);
      // control disable enable did not work??
      if (this.newMode) {
        this.templateForm.enable();
        if (value.documentLevel === DocumentLevelType.RESOURCE) {
          this.templateForm.controls['subresourceDefIdentifier'].disable();
        }
      } else {
        this.templateForm.disable();
      }
    } else {
      this.templateForm.disable();
      this.templateForm.controls['documentLevel'].setValue("");
      this.templateForm.controls['resourceDefIdentifier'].setValue("");
      this.templateForm.controls['subresourceDefIdentifier'].setValue("");
    }
    this.templateForm.markAsPristine();
  }

  get template(): DomainDocumentTemplateRo {
    let template = {...this._template};
    template.documentLevel = this.templateForm.get('documentLevel').value;
    template.resourceDefIdentifier = this.templateForm.get('resourceDefIdentifier').value;
    template.subresourceDefIdentifier = this.templateForm.get('subresourceDefIdentifier').value;
    return template;
  }

  onDocumentTypeChanged(documentLevelType: DocumentLevelType) {
    if (documentLevelType === DocumentLevelType.RESOURCE) {
      this.templateForm.controls['subresourceDefIdentifier'].setValue("");
    }
    this.validateSubresourceError();
  }

  get showSubresourceDefField(): boolean {
    return this.templateForm.get('documentLevel')?.value === DocumentLevelType.SUBRESOURCE;
  }


  onResourceDefChanged(resourceDefIdentifier: string) {
    this._selectedResourceDef = this.domainResourceDefs.find(rd => rd.identifier === resourceDefIdentifier);
    // reset subresource def
    this.templateForm.controls['subresourceDefIdentifier'].setValue("");
    this.validateSubresourceError();
  }

  validateSubresourceError() {
    let subCtrl = this.templateForm.controls['subresourceDefIdentifier'];
    let isSubresource = this.templateForm.get('documentLevel').value === DocumentLevelType.SUBRESOURCE;


    if (!isSubresource) {
      subCtrl.setValue("");
      subCtrl.disable();
      subCtrl.setErrors(null);
      return;
    }

    if (!this._selectedResourceDef) {
      subCtrl.setValue("");
      subCtrl.disable();
      subCtrl.setErrors(null);
      return;
    }

    if (this._selectedResourceDef.subresourceDefinitions?.length > 0) {
      subCtrl.enable();
      subCtrl.setErrors({noSubresourceDef: false});
    } else {
      subCtrl.setValue("");
      subCtrl.disable();
      subCtrl.setErrors({noSubresourceDef: true});
    }
  }

  get subresourceDefOptions(): SubresourceDefinitionRo[] {
    return this._selectedResourceDef?.subresourceDefinitions || [];
  }

  get resourceDefOptions(): ResourceDefinitionRo[] {
    return this.domainResourceDefs || [];
  }

  get currentDomain(): DomainRo {
    return this._currentDomain;
  };


  clearAlert() {
    this.message = null;
    this.messageType = null;
  }


  closeDialog() {
    this.dialogRef.close()
  }

  get submitButtonEnabled(): boolean {
    return this.templateForm.valid && this.templateForm.dirty && !this.inputDataError("subresourceDefIdentifier", "noSubresourceDef");
  }

  public onSaveButtonClicked() {

    let template = this.template;
    if (this.newMode) {
      this.create(template);
    }
  }

  public create(template: DomainDocumentTemplateRo) {

    this.editDomainService.createDocumentTemplateObservable(this._currentDomain.domainId, template).subscribe({
      next: (response: DomainDocumentTemplateRo) => {
        this.closeDialog();
      },
      error: (error) => {
        this.alertService.error(error.error?.errorDescription)
      }
    });
  }

  public inputDataError = (controlName: string, errorName: string) => {
    return this.templateForm.controls[controlName].hasError(errorName);
  }
}
