import {AfterViewInit, Component, Input, ViewChild, ViewEncapsulation,} from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {GroupRo} from "../../../common/model/group-ro.model";
import {ResourceRo} from "../../../common/model/resource-ro.model";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {DomainRo} from "../../../common/model/domain-ro.model";
import {ResourceDefinitionRo} from "../../../system-settings/admin-extension/resource-definition-ro.model";
import {EditResourceService} from "../edit-resource.service";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import {DocumentRo} from "../../../common/model/document-ro.model";
import {NavigationService} from "../../../window/sidenav/navigation-model.service";
import {SubresourceRo} from "../../../common/model/subresource-ro.model";
import {
  SubresourceDocumentWizardComponent
} from "../subresource-document-wizard-dialog/subresource-document-wizard.component";
import {
  ServiceMetadataWizardRo
} from "../../../service-group-edit/service-metadata-wizard-dialog/service-metadata-wizard-edit-ro.model";
import {ConfirmationDialogComponent} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";

@Component({
  moduleId: module.id,
  templateUrl: './subresource-document-panel.component.html',
  styleUrls: ['./subresource-document-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SubresourceDocumentPanelComponent implements AfterViewInit, BeforeLeaveGuard {

  title: string = "Subresources";
  private _resource: ResourceRo;
  private _subresource: SubresourceRo;

  _document: DocumentRo;
  @Input() private group: GroupRo;
  @Input() domain: DomainRo;
  @Input() domainResourceDefs: ResourceDefinitionRo[];

  @ViewChild("codemirror") codemirror: CodemirrorComponent;
// code mirror configuration
  codemirrorOptions = {
    lineNumbers: true,
    lineWrapping: true,
    viewportMargin: Infinity,
    mode: 'xml'
  };

  resourceForm: FormGroup;
  subresourceForm: FormGroup;
  documentForm: FormGroup;

  constructor(private editResourceService: EditResourceService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private navigationService: NavigationService,
              private formBuilder: FormBuilder) {
    this.resourceForm = formBuilder.group({
      'identifierValue': new FormControl({value: null}),
      'identifierScheme': new FormControl({value: null}),
      'visibility': new FormControl({value: null}),
      'resourceTypeIdentifier': new FormControl({value: null}),
    });
    this.subresourceForm = formBuilder.group({
      'identifierValue': new FormControl({value: null}),
      'identifierScheme': new FormControl({value: null}),
      'subresourceTypeIdentifier': new FormControl({value: null}),
    });

    this.documentForm = formBuilder.group({
      'mimeType': new FormControl({value: null}),
      'name': new FormControl({value: null}),
      'currentResourceVersion': new FormControl({value: null}),
      'payloadVersion': new FormControl({value: null}),
      'payload': new FormControl({value: null}),
    });
    this.documentForm.controls['payload'].setValue("")

    this.resource = editResourceService.selectedResource
    this.subresource = editResourceService.selectedSubresource


  }

  ngAfterViewInit(): void {
    // this.codemirror.codeMirror.setSize('100%', '100%');
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
      this.navigationService.reset();
      return;
    }

    this.resourceForm.disable();
    this.resourceForm.controls['identifierValue'].setValue(value.identifierValue);
    this.resourceForm.controls['identifierScheme'].setValue(value.identifierScheme);
    this.resourceForm.controls['resourceTypeIdentifier'].setValue(value.resourceTypeIdentifier);
    this.resourceForm.controls['visibility'].setValue(value.visibility);
    this.resourceForm.markAsPristine();
  }

  get subresource(): SubresourceRo {
    let subresource = {...this._subresource};
    subresource.identifierScheme = this.subresourceForm.get('identifierScheme').value;
    subresource.identifierValue = this.subresourceForm.get('identifierValue').value;
    subresource.subresourceTypeIdentifier = this.subresourceForm.get('subresourceTypeIdentifier').value;
    return subresource;
  }

  @Input() set subresource(value: SubresourceRo) {
    this._subresource = value;

    if (!this._subresource) {
      this.navigationService.reset();
      return;
    }


    this.subresourceForm.disable();
    this.subresourceForm.controls['identifierValue'].setValue(value.identifierValue);
    this.subresourceForm.controls['identifierScheme'].setValue(value.identifierScheme);
    this.subresourceForm.controls['subresourceTypeIdentifier'].setValue(value.subresourceTypeIdentifier);
    this.resourceForm.markAsPristine();
    // load current document for the resource
    this.loadDocumentForVersion();
  }

  @Input() set document(value: DocumentRo) {
    this._document = value;
    this.documentForm.disable();
    if (!!value) {
      this.documentForm.controls['mimeType'].setValue(value.mimeType);
      this.documentForm.controls['name'].setValue(value.name);
      this.documentForm.controls['currentResourceVersion'].setValue(value.currentResourceVersion);
      this.documentForm.controls['payloadVersion'].setValue(value.payloadVersion);
      this.documentForm.controls['payload'].setValue(!value.payload?"":value.payload);
      this.documentForm.controls['payload'].enable();

      if (!this.documentVersionsExists) {
        this.documentForm.controls['payloadVersion'].disable();
      } else {
        this.documentForm.controls['payloadVersion'].enable();
      }

    } else {
      this.documentForm.controls['name'].setValue("");
      this.documentForm.controls['payload'].setValue("");
      this.documentForm.controls['currentResourceVersion'].setValue("");
      this.documentForm.controls['payloadVersion'].setValue("");
      this.documentForm.controls['payload'].setValue("");
    }
    this.documentForm.markAsPristine();
  }

  get document(): DocumentRo {
    let doc: DocumentRo = {...this._document};
    doc.payload = this.documentForm.controls['payload'].value;
    return doc;
  }

  onSaveButtonClicked(): void {
    this.editResourceService.saveSubresourceDocumentObservable(this.subresource, this._resource, this.document).subscribe((value: DocumentRo) => {
      if (value) {
        this.alertService.success("Document is saved with current version [" + value.currentResourceVersion + "].")
        this.document = value;
      } else {
        this.document = null;
      }
    }, (error: any) => {
      this.alertService.error(error.error?.errorDescription)
    })
  }

  onGenerateButtonClicked(): void {
    this.editResourceService.generateSubresourceDocumentObservable(this.subresource, this._resource).subscribe((value: DocumentRo) => {
      if (value) {
        this.alertService.success("Document is generated.")
        this.documentForm.controls['payload'].setValue(value.payload);
        this.documentForm.controls['payload'].markAsDirty();
      } else {
        this.document = null;
      }
    }, (error: any) => {
      this.alertService.error(error.error?.errorDescription)
    })
  }

  onShowDocumentWizardDialog() {

    let serviceMetadataWizard: ServiceMetadataWizardRo = {
      isNewServiceMetadata: false,
      participantIdentifier: this._resource.identifierValue,
      participantScheme: this._resource.identifierScheme,
      documentIdentifier: this._subresource.identifierValue,
      documentIdentifierScheme:  this._subresource.identifierScheme,
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
        let smw: ServiceMetadataWizardRo = formRef.componentInstance.getCurrent();
        this.documentForm.controls['payload'].setValue(smw.contentXML);
        this.documentForm.controls['payload'].markAsDirty();
      }
    });
  }

  loadDocumentForVersion(version: number = null): void {
    this.editResourceService.getSubresourceDocumentObservable(this._subresource, this._resource, version).subscribe((value: DocumentRo) => {
      if (value) {
        this.document = value;
      } else {
        this.document = null;
      }
    }, (error: any) => {
      this.alertService.error(error.error?.errorDescription)
    });
  }

  validateCurrentDocument(): void {
    this.editResourceService.validateSubresourceDocumentObservable(this.subresource, this._resource, this.document).subscribe((value: DocumentRo) => {
      this.alertService.success("Document is Valid.")
    }, (error: any) => {
      this.alertService.error(error.error?.errorDescription)
    });
  }

  onDocumentValidateButtonClicked(): void {
    this.validateCurrentDocument();
  }

  onSelectionDocumentVersionChanged(): void {
    this.loadDocumentForVersion(this.documentForm.controls['payloadVersion'].value)
  }

  public onEditPanelClick() {
    if (this.codemirror.codeMirror.hasFocus()) {
      return;
    }
    let endPosition: number = this._document?.payload?.length;
    if (endPosition) {
      // forward focus to "codeMirror"
      this.codemirror.codeMirror.setCursor(endPosition)
    }
    this.codemirror.codeMirror.focus()

  }

  get getDocumentVersions(): number[] {
    return !this._document?.allVersions ? [] : this._document?.allVersions;
  }

  get documentVersionsExists(): boolean{
    return this.getDocumentVersions.length > 0
  }
  get emptyDocument(): boolean{
    return !this.documentForm.controls['payload']?.value
  }

  get cancelButtonDisabled(): boolean {
    return !this.documentForm.dirty;
  }

  get saveButtonDisabled(): boolean {
    return !this.documentForm.dirty || !this.documentForm.controls['payload']?.value;
  }

  isDirty(): boolean {
    return this.documentForm.dirty
  }

  onDocumentResetButtonClicked(): void {

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Cancel changes",
        description: "Do you want to cancel all changes on the document?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.resetChanges()
      }
    });
  }
  resetChanges(){
    let currentVersion = this._document?.payloadVersion;
    if (!currentVersion) {
      this.documentForm.controls['payload'].setValue("");
      this.documentForm.markAsPristine();
    } else {
      this.loadDocumentForVersion(currentVersion);
    }
  }

  get showWizardDialog(): boolean {
    // in version DomiSMP 5.0 CR show only the wizard for edelivery-oasis-smp-1.0-servicemetadata
    return this._subresource?.subresourceTypeIdentifier === 'edelivery-oasis-smp-1.0-servicemetadata';
  }
}






