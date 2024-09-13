import {Component, Input, ViewChild, ViewEncapsulation,} from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
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
import {DocumentRo} from "../../../common/model/document-ro.model";
import {
  NavigationService
} from "../../../window/sidenav/navigation-model.service";
import {
  DocumentWizardDialogComponent
} from "../document-wizard-dialog/document-wizard-dialog.component";
import {
  ConfirmationDialogComponent
} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {
  SmpEditorComponent
} from "../../../common/components/smp-editor/smp-editor.component";
import {EntityStatus} from "../../../common/enums/entity-status.enum";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import {
  DocumentVersionsStatus
} from "../../../common/enums/document-versions-status.enum";
import {
  HttpErrorHandlerService
} from "../../../common/error/http-error-handler.service";

@Component({
  templateUrl: './resource-document-panel.component.html',
  styleUrls: ['./resource-document-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ResourceDocumentPanelComponent implements BeforeLeaveGuard {
  readonly reviewAllowedStatusList: DocumentVersionsStatus[] =[DocumentVersionsStatus.DRAFT, DocumentVersionsStatus.REJECTED, DocumentVersionsStatus.RETIRED];
  readonly editableDocStatusList: DocumentVersionsStatus[] =[DocumentVersionsStatus.DRAFT, DocumentVersionsStatus.REJECTED, DocumentVersionsStatus.RETIRED];
  readonly publishableDocStatusList: DocumentVersionsStatus[] =[DocumentVersionsStatus.DRAFT, DocumentVersionsStatus.APPROVED, DocumentVersionsStatus.RETIRED];


  private _resource: ResourceRo;

  _document: DocumentRo;
  @Input() private group: GroupRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];

  @ViewChild("smpDocumentEditor") documentEditor: SmpEditorComponent;


  resourceForm: FormGroup;
  documentForm: FormGroup;

  constructor(private editResourceService: EditResourceService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private navigationService: NavigationService,
              private formBuilder: FormBuilder,
              private translateService: TranslateService,
              private httpErrorHandlerService: HttpErrorHandlerService) {
    this.resourceForm = formBuilder.group({
      'identifierValue': new FormControl({value: null}),
      'identifierScheme': new FormControl({value: null}),
      'visibility': new FormControl({value: null}),
      'resourceTypeIdentifier': new FormControl({value: null}),
    });
    this.documentForm = formBuilder.group({
      'mimeType': new FormControl({value: null}),
      'name': new FormControl({value: null}),
      'currentResourceVersion': new FormControl({value: null}),
      'payloadCreatedOn': new FormControl({value: null}),
      'payloadVersion': new FormControl({value: null}),
      'payload': new FormControl({value: null}),
      'properties': new FormControl({value: null}),
      'documentVersionStatus': new FormControl({value: null}),
      'documentVersionEvents': new FormControl({value: null}),
      'documentVersions': new FormControl({value: null}),
    });
    this.resource = editResourceService.selectedResource

    this.documentForm.controls['payload'].setValue("")
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
    if (!this._resource) {
      this.navigationService.navigateToHome();
      return;
    }

    this.resourceForm.enable();
    this.resourceForm.controls['identifierValue'].setValue(value.identifierValue);
    this.resourceForm.controls['identifierScheme'].setValue(value.identifierScheme);
    this.resourceForm.controls['resourceTypeIdentifier'].setValue(value.resourceTypeIdentifier);
    this.resourceForm.controls['visibility'].setValue(value.visibility);
    // control disable enable did not work??

    this.resourceForm.controls['identifierValue'].disable();
    this.resourceForm.controls['identifierScheme'].disable();
    this.resourceForm.controls['resourceTypeIdentifier'].disable();
    this.resourceForm.controls['visibility'].disable();
    this.resourceForm.markAsPristine();
    // load current document for the resource
    this.loadDocumentForVersion();
  }

  @Input() set document(value: DocumentRo) {
    this._document = value;
    this.documentForm.disable();
    console.log("Document with properties: " + value?.properties)
    if (!!value) {
      this.documentEditor.mimeType = value.mimeType;
      this.documentForm.controls['mimeType'].setValue(value.mimeType);
      this.documentForm.controls['name'].setValue(value.name);
      this.documentForm.controls['currentResourceVersion'].setValue(value.currentResourceVersion);
      this.documentForm.controls['payloadVersion'].setValue(value.payloadVersion);
      this.documentForm.controls['payloadCreatedOn'].setValue(value.payloadCreatedOn);
      this.documentForm.controls['payload'].setValue(value.payload);
      this.documentForm.controls['properties'].setValue(value.properties);
      this.documentForm.controls['documentVersionStatus'].setValue(value.documentVersionStatus);
      this.documentForm.controls['documentVersionEvents'].setValue(value.documentVersionEvents);
      this.documentForm.controls['documentVersions'].setValue(value.documentVersions);
      // the method documentVersionsExists already uses the current value to check if versions exists
      if (this.documentVersionsExists) {
        this.documentForm.controls['payloadVersion'].enable();
      }
      if (this.documentEditable) {
        this.documentForm.controls['payload'].enable();
      }
    } else {
      this.documentForm.controls['name'].setValue("");
      this.documentForm.controls['payload'].setValue("");
      this.documentForm.controls['currentResourceVersion'].setValue("");
      this.documentForm.controls['payloadVersion'].setValue("");
      this.documentForm.controls['payloadCreatedOn'].setValue("");
      this.documentForm.controls['payload'].setValue("");
      this.documentForm.controls['properties'].setValue([]);
      this.documentForm.controls['documentVersionStatus'].setValue("");
      this.documentForm.controls['documentVersionEvents'].setValue([]);
      this.documentForm.controls['documentVersions'].setValue([]);
    }
    this.documentForm.markAsPristine();
  }

  get document(): DocumentRo {
    let doc: DocumentRo = {...this._document};
    if (this.documentForm.controls['payload'].dirty) {
      doc.payload = this.documentForm.controls['payload'].value;
      doc.payloadStatus = EntityStatus.UPDATED;
    }
    doc.properties = this.documentForm.controls['properties'].value;
    return doc;
  }

  onBackButtonClicked(): void {
    this.navigationService.navigateUp();
  }

  async onDocumentResetButtonClicked() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("resource.document.panel.cancel.confirmation.dialog.title")),
        description: await lastValueFrom(this.translateService.get("resource.document.panel.cancel.confirmation.dialog.description"))
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.resetChanges()
      }
    });
  }

  resetChanges() {

    let currentVersion = this._document?.payloadVersion;
    if (!currentVersion) {
      this.documentForm.controls['payload'].setValue("");
      this.documentForm.markAsPristine();
    } else {
      this.loadDocumentForVersion(currentVersion);
    }
  }


  onSaveButtonClicked(): void {

    this.editResourceService.saveDocumentObservable(this._resource, this.document)
      .subscribe({
        next: async (doc: DocumentRo) =>{
          if (!doc) {
            this.document = null;
          } else {
            this.alertService.success(await lastValueFrom(this.translateService.get("resource.document.panel.success.save", {currentResourceVersion: doc.currentResourceVersion})));
            this.document = doc;
          }
        },
        error: this.handleServerError
      });
  }

  onReviewButtonClicked(): void {
    // request review
    // create lightweight document object
    let docRequest: DocumentRo = {
      documentId: this._document.documentId,
      payloadVersion: this._document.payloadVersion,
    } as DocumentRo;
    this.editResourceService.reviewRequestReviewObservable(this._resource, docRequest).subscribe({
      next: (doc: DocumentRo) =>{
        if (!doc) {
          this.document = null;
        } else {
          this.document = doc;
        }
      },
      error: this.handleServerError
    });
  }

  onApproveButtonClicked(): void {
    // create lightweight document object
    let docRequest: DocumentRo = {
      documentId: this._document.documentId,
      payloadVersion: this._document.payloadVersion,
    } as DocumentRo;
    this.editResourceService.reviewApproveObservable(this._resource, docRequest).subscribe({
      next: (doc: DocumentRo) =>{
        if (!doc) {
          this.document = null;
        } else {
          this.document = doc;
        }
      },
      error: this.handleServerError
    });
  }

  onRejectButtonClicked(): void {
    // create lightweight document object
    let docRequest: DocumentRo = {
      documentId: this._document.documentId,
      payloadVersion: this._document.payloadVersion,
    } as DocumentRo;
    this.editResourceService.reviewRejectObservable(this._resource, docRequest).subscribe({
      next: (doc: DocumentRo) =>{
        if (!doc) {
          this.document = null;
        } else {
          this.document = doc;
        }
      },
      error: this.handleServerError
    });
  }
  /**
   * Publish the current document version
   *
   */
  onPublishButtonClicked(): void {
    // create lightweight document object
    let docRequest: DocumentRo = {
      documentId: this._document.documentId,
      payloadVersion: this._document.payloadVersion,
    } as DocumentRo;
    this.editResourceService.publishDocumentObservable(this._resource, docRequest).subscribe({
      next: (doc: DocumentRo) =>{
        if (!doc) {
          this.document = null;
        } else {
          this.document = doc;
        }
      },
      error: this.handleServerError
    });
  }

  onGenerateButtonClicked(): void {
    this.editResourceService.generateDocumentObservable(this._resource)
      .subscribe(async (value: DocumentRo) => {
      if (value) {
        this.alertService.success(await lastValueFrom(this.translateService.get("resource.document.panel.success.generate")))
        this.documentForm.controls['payload'].setValue(value.payload);
        this.documentForm.controls['payload'].markAsDirty();
      } else {
        this.document = null;
      }
    }, (error: any) => {
        this.handleServerError(error);
    })
  }

  async onShowDocumentWizardDialog() {

    const formRef: MatDialogRef<any> = this.dialog.open(DocumentWizardDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("resource.document.panel.document.wizard.dialog.title")),
        resource: this._resource,

      }
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        let val = formRef.componentInstance.getExtensionXML();
        this.documentForm.controls['payload'].setValue(val);
        this.documentForm.controls['payload'].markAsDirty();
      }
    });
  }

  loadDocumentForVersion(version: number = null): void {
    this.editResourceService.getDocumentObservable(this._resource, version).subscribe((value: DocumentRo) => {
      if (value) {
        this.document = value;
      } else {
        this.document = null;
      }
    }, (error: any) => {
      this.handleServerError(error);
    });
  }

  validateCurrentDocument(): void {
    this.editResourceService.validateDocumentObservable(this._resource, this.document).subscribe(async (value: DocumentRo) => {
      this.alertService.success(await lastValueFrom(this.translateService.get("resource.document.panel.success.valid")))
    }, (error: any) => {
      this.handleServerError(error);
    });
  }

  onDocumentValidateButtonClicked(): void {
    this.validateCurrentDocument();
  }

  onNewDocumentVersionButtonClicked(): void {
    // create a new version of the document
    let docRequest: DocumentRo = {
      documentVersionStatus: DocumentVersionsStatus.DRAFT,
      payloadStatus: EntityStatus.NEW,
      status: EntityStatus.UPDATED,
      payload: this.documentForm.controls['payload'].value,
      allVersions: this.getDocumentVersions,
      // set from current document
      documentVersions: this.documentForm.controls['documentVersions'].value,
      properties: this.documentForm.controls['properties'].value,
    } as DocumentRo;
    // set as current
    this.document = docRequest;
    this.documentForm.markAsDirty();
  }

  onSelectionDocumentVersionChanged(): void {
    this.loadDocumentForVersion(this.documentForm.controls['payloadVersion'].value)
  }

  public onEditPanelClick() {

    if (this.documentEditor.hasFocus) {
      return;
    }
    this.documentEditor.focusAndCursorToEnd();
  }

  get getDocumentVersions(): number[] {
    return !this._document?.allVersions ? [] : this._document?.allVersions;
  }

  get emptyDocument(): boolean {
    return !this.documentForm.controls['payload']?.value
  }

  get documentVersionsExists(): boolean {
    return this.getDocumentVersions.length > 0
  }

  get cancelButtonDisabled(): boolean {
    return !this.documentForm.dirty;
  }

  get saveButtonDisabled(): boolean {
    return !this.documentForm.dirty || !this.documentForm.controls['payload']?.value;
  }

  get reviewButtonDisabled(): boolean {
    return !this.reviewEnabled || !this.documentSubmitReviewAllowed;
  }

  get reviewActionButtonDisabled(): boolean {
    return !this.reviewEnabled || this.documentForm.controls['documentVersionStatus']?.value!==DocumentVersionsStatus.UNDER_REVIEW;
  }

  get publishButtonDisabled(): boolean {
    // can not publish changed document
    if (this.isDirty()){
      return true;
    }
    let status =  this.documentForm.controls['documentVersionStatus']?.value

    return this.reviewEnabled?
      status !== DocumentVersionsStatus.APPROVED :
      !this.publishableDocStatusList.find(i => i === status)
  }

  get documentEditable(): boolean {
    let status =  this.documentForm.controls['documentVersionStatus']?.value
    return !!this.editableDocStatusList.find(i => i === status)
  }

  get documentSubmitReviewAllowed(): boolean {
    let status =  this.documentForm.controls['documentVersionStatus']?.value
    return !!this.reviewAllowedStatusList.find(i => i === status)
  }

  get reviewEnabled(): boolean {
    return this.resource?.reviewEnabled;
  }

  isDirty(): boolean {
    return this.documentForm.dirty
  }

  get showWizardDialog(): boolean {
    // in version DomiSMP 5.0 CR show only the wizard for edelivery-oasis-smp-1.0-servicegroup
    return this._resource?.resourceTypeIdentifier === 'edelivery-oasis-smp-1.0-servicegroup';
  }

  /**
   * Handle server error: logout on invalid session error or show error message
   * @param err error object
   */
  private handleServerError(err: any) {
    if (this.httpErrorHandlerService.logoutOnInvalidSessionError(err)) {
      return;
    }
    this.alertService.error(err.error?.errorDescription)
  }

}
