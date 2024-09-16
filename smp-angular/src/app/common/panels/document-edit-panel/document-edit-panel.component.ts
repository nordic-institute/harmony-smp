import {
  Component,
  Input,
  OnInit,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
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
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {DocumentRo} from "../../../common/model/document-ro.model";
import {
  NavigationService
} from "../../../window/sidenav/navigation-model.service";

import {
  ConfirmationDialogComponent
} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {
  SmpEditorComponent
} from "../../../common/components/smp-editor/smp-editor.component";
import {EntityStatus} from "../../../common/enums/entity-status.enum";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom, Observer} from "rxjs";
import {
  DocumentVersionsStatus
} from "../../../common/enums/document-versions-status.enum";
import {
  HttpErrorHandlerService
} from "../../../common/error/http-error-handler.service";
import {
  DocumentWizardDialogComponent
} from "../../../edit/edit-resources/document-wizard-dialog/document-wizard-dialog.component";
import {
  EditResourceService
} from "../../../edit/edit-resources/edit-resource.service";
import {SubresourceRo} from "../../model/subresource-ro.model";
import {
  SubresourceWizardRo
} from "../../../edit/edit-resources/subresource-document-wizard-dialog/subresource-wizard-edit-ro.model";
import {
  SubresourceDocumentWizardComponent
} from "../../../edit/edit-resources/subresource-document-wizard-dialog/subresource-document-wizard.component";
import {
  ReviewDocumentVersionRo
} from "../../model/review-document-version-ro.model";

export enum SmpDocumentEditorType {
  RESOURCE_EDITOR = "RESOURCE_EDITOR",
  SUBRESOURCE_EDITOR = "SUBRESOURCE_EDITOR",
  REVIEW_EDITOR = "REVIEW_EDITOR"
}

export enum SmpReviewDocumentTarget {
  RESOURCE = "RESOURCE",
  SUBRESOURCE = "SUBRESOURCE",
}

/**
 * Component edit panel for document and document version management.
 * Please not the document version management can change the document (version) entities only
 * and does not edit resource or subresource entities.
 *
 * The document (versions) actions can be
 * <ul>
 *   <li>new version created: a new version of the document can be created for the document</li>
 *   <li>edited: a document version payload, or document version can be loaded </li>
 *   <li>saved: a document changed can be persisted</li>
 *   <li>published: the document can be set as current version</li>
 *   <li>validated: the selected document can be validated </li>
 *   <li>generated: the button allows option to generate new document from the plugin template</li>
 *   <li>reviewed process: the component allows action needed for the review process
 *    <ul>
 *      <li>request review</li>
 *      <li>approve</li>
 *      <li>reject</li>
 *   </ul>
 *   </li>
 * </ul>
 */
@Component({
  selector: 'document-edit-panel',
  templateUrl: './document-edit-panel.component.html',
  styleUrls: ['./document-edit-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DocumentEditPanelComponent implements BeforeLeaveGuard, OnInit {
  readonly reviewAllowedStatusList: DocumentVersionsStatus[] = [DocumentVersionsStatus.DRAFT, DocumentVersionsStatus.REJECTED, DocumentVersionsStatus.RETIRED];
  readonly editableDocStatusList: DocumentVersionsStatus[] = [DocumentVersionsStatus.DRAFT, DocumentVersionsStatus.REJECTED, DocumentVersionsStatus.RETIRED];
  readonly publishableDocStatusList: DocumentVersionsStatus[] = [DocumentVersionsStatus.DRAFT, DocumentVersionsStatus.APPROVED, DocumentVersionsStatus.RETIRED];
  private resource: ResourceRo;
  private subresource: SubresourceRo;
  private reviewDocument: ReviewDocumentVersionRo;
  private isResourceDocument:boolean = true;

  _document: DocumentRo;
  @Input() private group: GroupRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];
  private _editorMode: SmpDocumentEditorType = SmpDocumentEditorType.RESOURCE_EDITOR;

  @Input() set editorMode(edType: string) {
    this._editorMode = SmpDocumentEditorType[edType];
  }

  get editorMode(): SmpDocumentEditorType {
    return !this._editorMode ? SmpDocumentEditorType.RESOURCE_EDITOR : this._editorMode;
  }

  @ViewChild("smpDocumentEditor") documentEditor: SmpEditorComponent;
  // ----
  // defined observers
  loadDocumentObserver: Partial<Observer<DocumentRo>> = {
    next: async (doc: DocumentRo) => {
      if (!doc) {
        this.document = null;
      } else {
        this.document = doc;
      }
    },
    error: (err: any) => {
      this.httpErrorHandlerService.handleHttpError(err)
    }
  };

  reviewActionDocumentObserver: Partial<Observer<DocumentRo>> = {
    next: async (doc: DocumentRo) => {
      if (!doc) {
        this.document = null;
      } else {
        this.document = doc;
      }
    },
    error: (err: any) => {
      this.httpErrorHandlerService.handleHttpError(err)
    }
  };

  // save event observer
  saveDocumentObserver: Partial<Observer<DocumentRo>> = {
    next: async (doc: DocumentRo) => {
      if (!doc) {
        this.document = null;
      } else {
        this.alertService.success(await lastValueFrom(this.translateService.get("document.edit.panel.success.save", {currentResourceVersion: doc.payloadVersion})));
        this.document = doc;
      }
    },
    error: (err: any) => {
      this.httpErrorHandlerService.handleHttpError(err)
    }
  };

  // save event observer
  validateDocumentObserver: Partial<Observer<DocumentRo>> = {
    next: async (doc: DocumentRo) => {
      this.alertService.success(await lastValueFrom(this.translateService.get("document.edit.panel.success.valid")))
    },
    error: (err: any) => {
      this.httpErrorHandlerService.handleHttpError(err)
    }
  };

  // save event observer
  generateDocumentObserver: Partial<Observer<DocumentRo>> = {
    next: async (doc: DocumentRo) => {
      if (!doc) {
        this.document = null;
      } else {
        this.alertService.success(await lastValueFrom(this.translateService.get("document.edit.panel.success.generate")))
        this.documentForm.controls['payload'].setValue(doc.payload);
        this.documentForm.controls['payload'].markAsDirty();
      }
    },
    error: (err: any) => {
      this.httpErrorHandlerService.handleHttpError(err)
    }
  };

  documentForm: FormGroup;

  constructor(private editResourceService: EditResourceService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private navigationService: NavigationService,
              private formBuilder: FormBuilder,
              private translateService: TranslateService,
              private httpErrorHandlerService: HttpErrorHandlerService) {


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
      'documentMetadata': new FormControl({value: null}),
    });

    this.resource = editResourceService.selectedResource;
    this.subresource = editResourceService.selectedSubresource;
    this.reviewDocument = editResourceService.selectedReviewDocument;
    this.documentForm.controls['payload'].setValue("")
  }

  /**
   * Methods created resource and subresource from documentReview object
   */
  initFromDocumentReview() {
    if (!this.reviewDocument) {
      return
    }
    this.resource = {
      resourceId: this.reviewDocument.resourceId,
      identifierScheme: this.reviewDocument.resourceIdentifierScheme,
      identifierValue: this.reviewDocument.resourceIdentifierValue,
      reviewEnabled: true,
      visibility: null,
      resourceTypeIdentifier: null,
    } as ResourceRo;

    if (this.reviewDocument.subresourceId) {
      this.subresource = {
        subresourceId: this.reviewDocument.subresourceId,
        identifierScheme: this.reviewDocument.subresourceIdentifierScheme,
        identifierValue: this.reviewDocument.subresourceIdentifierValue,
        subresourceTypeIdentifier: null,
      } as SubresourceRo;
    }
    if (this.reviewDocument.target === "RESOURCE") {
      this.isResourceDocument = true;
    } else {
      this.isResourceDocument = false;
    }
  }

  ngOnInit(): void {
    if (this.editorMode === SmpDocumentEditorType.REVIEW_EDITOR) {
      this.initFromDocumentReview();
    } else {
      this.isResourceDocument = this.editorMode === SmpDocumentEditorType.RESOURCE_EDITOR;
    }

    if (this.editorMode === SmpDocumentEditorType.REVIEW_EDITOR && !this.reviewDocument
      || this.editorMode !== SmpDocumentEditorType.REVIEW_EDITOR && !this.resource) {
      this.alertService.errorForTranslation("document.edit.panel.error.document.null");
      this.navigationService.navigateUp();
      return;
    }
    if (this.editorMode === SmpDocumentEditorType.REVIEW_EDITOR) {
      this.loadDocumentForVersion(this.reviewDocument.version);
    } else {
      this.loadDocumentForVersion();
    }
  }


  @Input() set document(value: DocumentRo) {
    this._document = value;
    this.documentForm.disable();
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
      this.documentForm.controls['documentMetadata'].setValue(value.metadata);
      // the method documentVersionsExists already uses the current value to check if versions exists

      if (this.documentVersionsExists && this.isNotReviewMode) {
        this.documentForm.controls['payloadVersion'].enable();
      }
      if (this.documentEditable) {
        this.documentForm.controls['payload'].enable();
      }
    } else {
      this.documentForm.controls['name'].setValue("");
      this.documentForm.controls['currentResourceVersion'].setValue("");
      this.documentForm.controls['payloadVersion'].setValue("");
      this.documentForm.controls['payloadCreatedOn'].setValue("");
      this.documentForm.controls['payload'].setValue("");
      this.documentForm.controls['properties'].setValue([]);
      this.documentForm.controls['documentVersionStatus'].setValue("");
      this.documentForm.controls['documentVersionEvents'].setValue([]);
      this.documentForm.controls['documentVersions'].setValue([]);
      this.documentForm.controls['documentMetadata'].setValue(null);
    }
    this.documentForm.markAsPristine();
  }

  get document(): DocumentRo {
    let doc: DocumentRo = {...this._document};
    if (this.documentForm.controls['payload'].dirty) {
      doc.payload = this.documentForm.controls['payload'].value;
      doc.payloadStatus = EntityStatus.UPDATED;
    } else {
      // no need to send payload if not changed
      doc.payload = null;
    }
    // set new properties
    doc.properties = this.documentForm.controls['properties'].value;
    doc.metadata = this.documentForm.controls['documentMetadata'].value;
    return doc;
  }

  onBackButtonClicked(): void {
    this.navigationService.navigateUp();
  }

  async onDocumentResetButtonClicked() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("document.edit.panel.cancel.confirmation.dialog.title")),
        description: await lastValueFrom(this.translateService.get("document.edit.panel.cancel.confirmation.dialog.description"))
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

  get currentDocumentVersion(): number {
    return this.isNotReviewMode? this.documentForm.controls['payloadVersion']?.value:
      this.reviewDocument?.version;
  }

  onSaveButtonClicked(): void {
    let onSaveObservable = this.isResourceDocument ?
      this.editResourceService.saveResourceDocumentObservable(this.resource, this.document) :
      this.editResourceService.saveSubresourceDocumentObservable(this.subresource, this.resource, this.document);
    onSaveObservable.subscribe(this.saveDocumentObserver);
  }

  onReviewRequestButtonClicked(): void {
    // create lightweight document object
    let docRequest: DocumentRo = {
      documentId: this._document.documentId,
      payloadVersion: this._document.payloadVersion,
    } as DocumentRo;

    let onReviewRequestObservable = this.isResourceDocument ?
      this.editResourceService.reviewRequestForResourceDocumentObservable(this.resource, docRequest) :
      this.editResourceService.reviewRequestForSubresourceDocumentObservable(this.subresource, this.resource, docRequest);
    // request review
    onReviewRequestObservable.subscribe(this.loadDocumentObserver);
  }

  onApproveButtonClicked(): void {
    // create lightweight document object
    let docRequest: DocumentRo = {
      documentId: this._document.documentId,
      payloadVersion: this._document.payloadVersion,
    } as DocumentRo;

    let onReviewRequestObservable = this.isResourceDocument ?
      this.editResourceService.reviewApproveForResourceDocumentObservable(this.resource, docRequest) :
      this.editResourceService.reviewApproveForSubresourceDocumentObservable(this.subresource, this.resource, docRequest);
    // request review
    onReviewRequestObservable.subscribe(this.reviewActionDocumentObserver);
  }

  onRejectButtonClicked(): void {
    // create lightweight document object
    let docRequest: DocumentRo = {
      documentId: this._document.documentId,
      payloadVersion: this._document.payloadVersion,
    } as DocumentRo;

    let onReviewRequestObservable = this.isResourceDocument ?
      this.editResourceService.reviewRejectResourceDocumentObservable(this.resource, docRequest) :
      this.editResourceService.reviewRejectSubresourceDocumentObservable(this.subresource, this.resource, docRequest);
    // request review
    onReviewRequestObservable.subscribe(this.reviewActionDocumentObserver);
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

    let onReviewRequestObservable = this.isResourceDocument ?
      this.editResourceService.publishResourceDocumentObservable(this.resource, docRequest) :
      this.editResourceService.publishSubresourceDocumentObservable(this.subresource, this.resource, docRequest);
    // request review
    onReviewRequestObservable.subscribe(this.loadDocumentObserver);
  }

  onGenerateButtonClicked(): void {
    let generateObservable = this.isResourceDocument ?
      this.editResourceService.generateResourceDocumentObservable(this.resource) :
      this.editResourceService.generateSubresourceDocumentObservable(this.subresource, this.resource);
    generateObservable.subscribe(this.generateDocumentObserver);
  }

  async onShowDocumentWizardDialog() {
    if (this.isResourceDocument) {
      await this.onShowResourceDocumentWizardDialog();
    } else {
      await this.onShowSubresourceDocumentWizardDialog();
    }
  }

  async onShowResourceDocumentWizardDialog() {
    const formRef: MatDialogRef<any> = this.dialog.open(DocumentWizardDialogComponent, {
      data: {
        title: await lastValueFrom(this.translateService.get("document.edit.panel.document.wizard.dialog.title")),
        resource: this.resource,

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

  async onShowSubresourceDocumentWizardDialog() {

    let serviceMetadataWizard: SubresourceWizardRo = {
      isNewSubresource: false,
      participantIdentifier: this.resource.identifierValue,
      participantScheme: this.resource.identifierScheme,
      documentIdentifier: this.subresource.identifierValue,
      documentIdentifierScheme: this.subresource.identifierScheme,
      processIdentifier: '',
      processScheme: '',
      transportProfile: 'bdxr-transport-ebms3-as4-v1p0', // default value for oasis AS4

      endpointUrl: '',
      endpointCertificate: '',

      serviceDescription: '',
      technicalContactUrl: '',

    }

    const formRef: MatDialogRef<any> = this.dialog.open(SubresourceDocumentWizardComponent, {
      data: serviceMetadataWizard
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        let smw: SubresourceWizardRo = formRef.componentInstance.getCurrent();
        this.documentForm.controls['payload'].setValue(smw.contentXML);
        this.documentForm.controls['payload'].markAsDirty();
      }
    });
  }

  /**
   * 'loadDocumentForVersion' load the document for the given version
   * @param version
   */
  loadDocumentForVersion(version: number = null): void {

    let loadObservable = this.isResourceDocument ?
      this.editResourceService.getResourceDocumentObservable(this.resource, version) :
      this.editResourceService.getSubresourceDocumentObservable(this.subresource, this.resource, version);
    loadObservable.subscribe(this.loadDocumentObserver);
  }

  /**
   * Submit the current document for validation to the server
   */
  validateCurrentDocument(): void {
    let validateObservable = this.isResourceDocument ?
      this.editResourceService.validateResourceDocumentObservable(this.resource, this.document) :
      this.editResourceService.validateSubresourceDocumentObservable(this.subresource, this.resource, this.document);
    validateObservable.subscribe(this.validateDocumentObserver);
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
      metadata: this.documentForm.controls['documentMetadata'].value,
    } as DocumentRo;
    // set as current
    this.document = docRequest;
    this.documentForm.markAsDirty();
    this.documentForm.controls['payload'].markAsDirty();
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
    return !this.documentForm.dirty
      || !this.documentForm.controls['payload']?.value;
  }

  /**
   * Review button is disabled if review is not enabled,
   * if changes are not persisted, or not in  status, rejected, retired or draft
   */
  get reviewButtonDisabled(): boolean {
    return !this.reviewEnabled
      || !this.documentSubmitReviewAllowed
      || this.isDirty();
  }

  get reviewActionButtonDisabled(): boolean {
    return !this.reviewEnabled
      || this.documentForm.controls['documentVersionStatus']?.value !== DocumentVersionsStatus.UNDER_REVIEW
  }

  get publishButtonDisabled(): boolean {
    // can not publish changed document
    if (this.isDirty()) {
      return true;
    }
    let status = this.documentForm.controls['documentVersionStatus']?.value

    return this.reviewEnabled ?
      status !== DocumentVersionsStatus.APPROVED :
      !this.publishableDocStatusList.find(i => i === status)
  }

  get documentEditable(): boolean {
    let status = this.documentForm.controls['documentVersionStatus']?.value
    return !!this.editableDocStatusList.find(i => i === status)
  }

  get documentSubmitReviewAllowed(): boolean {
    let status = this.documentForm.controls['documentVersionStatus']?.value
    return !!this.reviewAllowedStatusList.find(i => i === status)
  }

  get reviewEnabled(): boolean {
    return this.resource?.reviewEnabled;
  }

  get isNotReviewMode(): boolean {
    return this.editorMode !== SmpDocumentEditorType.REVIEW_EDITOR;
  }

  isDirty(): boolean {
    return this.documentForm.dirty
  }

  get showWizardDialog(): boolean {
    if (this.isResourceDocument) {
      // in version DomiSMP 5.0 CR show only the wizard for edelivery-oasis-smp-1.0-servicegroup
      return this.resource?.resourceTypeIdentifier === 'edelivery-oasis-smp-1.0-servicegroup';
    } else {
      return this.subresource?.subresourceTypeIdentifier === 'edelivery-oasis-smp-1.0-servicemetadata';
    }
  }
}
