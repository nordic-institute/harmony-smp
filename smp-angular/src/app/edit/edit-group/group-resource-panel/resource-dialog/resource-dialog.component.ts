import {
  Component,
  ElementRef,
  Inject,
  Input,
  OnInit,
  ViewChild
} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {
  AlertMessageService
} from "../../../../common/alert-message/alert-message.service";
import {VisibilityEnum} from "../../../../common/enums/visibility.enum";
import {GroupRo} from "../../../../common/model/group-ro.model";
import {ResourceRo} from "../../../../common/model/resource-ro.model";
import {DomainRo} from "../../../../common/model/domain-ro.model";
import {
  ResourceDefinitionRo
} from "../../../../system-settings/admin-extension/resource-definition-ro.model";
import {EditGroupService} from "../../edit-group.service";
import {GlobalLookups} from "../../../../common/global-lookups";
import {
  EditResourceService
} from "../../../edit-resources/edit-resource.service";
import {EditDomainService} from "../../../edit-domain/edit-domain.service";
import {
  HttpErrorHandlerService
} from "../../../../common/error/http-error-handler.service";
import {
  DomainPropertyRo
} from "../../../../common/model/domain-property-ro.model";
import {lastValueFrom, Subscription} from "rxjs";
import {
  EditResourceController
} from "../../../edit-resources/edit-resource.controller";
import {
  ConfirmationDialogComponent
} from "../../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {TranslateService} from "@ngx-translate/core";
import StringUtils from "../../../../common/utils/string-utils";


@Component({
    templateUrl: './resource-dialog.component.html',
    styleUrls: ['./resource-dialog.component.css'],
    standalone: false
})
export class ResourceDialogComponent implements OnInit {
  readonly PROPERTY_RESOURCE_SCHEME_VALIDATION_REGEXP_VAL: string = 'identifiersBehaviour.ParticipantIdentifierScheme.validationRegex';
  readonly PROPERTY_RESOURCE_SCHEME_VALIDATION_REGEXP_MSG: string = 'identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage';
  readonly PROPERTY_RESOURCE_SCHEME_MANDATORY: string = 'identifiersBehaviour.scheme.mandatory';

  readonly groupVisibilityOptions = Object.keys(VisibilityEnum)
    .map(el => {
      return {key: el, value: VisibilityEnum[el]}
    });

  formTitle = "";
  resourceForm: FormGroup;
  message: string;
  messageType: string = "alert-error";
  group: GroupRo;
  _resource: ResourceRo
  domain: DomainRo;
  domainResourceDefs: ResourceDefinitionRo[];
  resourceSchemePattern = '^[a-z0-9]+-[a-z0-9]+-[a-z0-9]+$';
  resourceSchemeMessage: string;
  resourceSchemeMandatory: boolean = false;
  submitInProgress: boolean = false;
  private domainPropertyUpdatedEventSub: Subscription = Subscription.EMPTY;
  @ViewChild('identifierValue', {static: false}) identifierValue: ElementRef;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              public lookups: GlobalLookups,
              public dialogRef: MatDialogRef<ResourceDialogComponent>,
              private editGroupService: EditGroupService,
              private editResourceService: EditResourceService,
              private editResourceController: EditResourceController,
              private alertService: AlertMessageService,
              private httpErrorHandlerService: HttpErrorHandlerService,
              private editDomainService: EditDomainService,
              private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private translateService: TranslateService
  ) {

    if (this.lookups.cachedApplicationConfig) {
      this.resourceSchemePattern = this.lookups.cachedApplicationConfig.participantSchemaRegExp != null ?
        this.lookups.cachedApplicationConfig.participantSchemaRegExp : ".*"
      this.resourceSchemeMessage = this.lookups.cachedApplicationConfig.participantSchemaRegExpMessage;
    }
    dialogRef.disableClose = true;//disable default close operation
    this.formTitle = data.formTitle;


    this.resourceForm = formBuilder.group({
      'identifierValue': new FormControl({value: null},),
      'identifierScheme': new FormControl({value: null},),
      'visibility': new FormControl({value: null}),
      'reviewEnabled': new FormControl({value: null}),
      'resourceTypeIdentifier': new FormControl({value: null}),
      '': new FormControl({value: null})
    });
    this.resourceSchemeMandatory = !!lookups.cachedApplicationConfig.partyIDSchemeMandatory

    this.resource = data.resource;
    this.group = data.group;
    this.domain = data.domain;
    this.domainResourceDefs = data.domainResourceDefs;
    this.updateControlSchemeValidation();
  }

  ngOnInit(): void {
    // subscribe to domain property update event to update
    // registered domain properties for regexp validation
    this.domainPropertyUpdatedEventSub = this.editDomainService.onDomainPropertyUpdatedEvent()
      .subscribe((updateDomainList: DomainPropertyRo[]): void => {
          this.updateDomainPropertyList(updateDomainList);
        }
      );
    this.editDomainService.getDomainProperties(this.domain);
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
    resource.reviewEnabled = this.resourceForm.get('reviewEnabled').value;
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
      this.resourceForm.controls['reviewEnabled'].setValue(value.reviewEnabled);

    } else {
      this.resourceForm.disable();
      this.resourceForm.controls['identifierValue'].setValue("");
      this.resourceForm.controls['identifierScheme'].setValue("");
      this.resourceForm.controls['visibility'].setValue("");
      this.resourceForm.controls['resourceTypeIdentifier'].setValue("");
    }

    this.resourceForm.markAsPristine();
  }

  /**
   * Update registered domain properties for regexp validation of the participant scheme
   *
   * @param updateDomainList
   */
  public updateDomainPropertyList(updateDomainList: DomainPropertyRo[]): void {
    if (!updateDomainList) {
      return;
    }
    updateDomainList.forEach((element: DomainPropertyRo) => {
      if (element.property === this.PROPERTY_RESOURCE_SCHEME_VALIDATION_REGEXP_VAL) {
        let value = element.systemDefault ? element.systemDefaultValue : element.value;
        this.resourceSchemePattern = value;
      } else if (element.property === this.PROPERTY_RESOURCE_SCHEME_VALIDATION_REGEXP_MSG) {
        let value = element.systemDefault ? element.systemDefaultValue : element.value;
        this.resourceSchemeMessage = value;
      } else if (element.property === this.PROPERTY_RESOURCE_SCHEME_MANDATORY) {
        let value = element.systemDefault ? element.systemDefaultValue : element.value;
        this.resourceSchemeMandatory = value === 'true';
      }
    });
    this.updateControlSchemeValidation();
  }

  /**
   * Update the validation of the participant scheme control
   */
  private updateControlSchemeValidation(): void {
    let schemeControl = this.resourceForm.controls['identifierScheme'];
    schemeControl.clearValidators();
    schemeControl.addValidators([Validators.pattern(this.resourceSchemePattern)]);
    if (this.resourceSchemeMandatory) {
      schemeControl.addValidators([Validators.required]);
    }
    schemeControl.updateValueAndValidity();
  }

  clearAlert() {
    this.message = null;
    this.messageType = null;
  }


  closeDialog() {
    this.dialogRef.close()
  }

  async onVisibilityChanged(event: any) {
    let showWarning: boolean = this._resource?.visibility === VisibilityEnum.Public && event.target.value === VisibilityEnum.Private;

    if (showWarning) {

      let confirmationDescriptionKey:string = "resource.details.panel.visibility.change.confirmation.dialog.description";
      let confirmationDescriptionParameters: any = {};

      // check if resource is referenced by document references
      if (this._resource.documentReferenceInfo &&
        this._resource.documentReferenceInfo.sharingEnabled
        && this._resource.documentReferenceInfo.referencedByCount > 0) {
        confirmationDescriptionKey = "resource.details.panel.visibility.change.confirmation.dialog.description.referenced";
        confirmationDescriptionParameters["referenceCount"] = this._resource.documentReferenceInfo.referencedByCount;
      }

      this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: await lastValueFrom(this.translateService.get("resource.details.panel.visibility.change.confirmation.dialog.title")),
          description: await lastValueFrom(this.translateService.get(confirmationDescriptionKey, confirmationDescriptionParameters))
        }
      }).afterClosed().subscribe(result => {
        if (!result) {
          // prevent default does not work in case of "async"
          this.resourceForm.controls['visibility'].setValue(VisibilityEnum.Public);
          this.resourceForm.controls['visibility'].markAsPristine();
        }
      });
    }
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
    this.editGroupService.createResourceForGroup(this.resource, this.group, this.domain).subscribe({
      next: (result: ResourceRo) => {
        if (!!result) {
          // refresh the domains/groups/resources for user to see the changes
          this.editResourceController.dataChanged = true;
          this.closeDialog();
        }
        this.submitInProgress = false;
      }, error: (error) => {
        this.httpErrorHandlerService.handleHttpError(error);
        this.submitInProgress = false;
      }
    });
  }

  public saveResource(resource: ResourceRo): void {
    this.submitInProgress = true;
    this.editResourceService.updateResourceForGroup(resource, this.group, this.domain).subscribe({
      next: (result: ResourceRo): void => {
        if (!!result) {
          // refresh the domains/groups/resources for user to see the changes
          this.editResourceController.dataChanged = true;
          this.closeDialog();
        }
        this.submitInProgress = false;
      }, error: (error: any): void => {
        this.httpErrorHandlerService.handleHttpError(error);
        this.submitInProgress = false;
      }
    });
  }

  public setFocus() {
    setTimeout(() => this.identifierValue.nativeElement.focus());
  }
}
