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
import {DocumentWizardDialogComponent} from "../document-wizard-dialog/document-wizard-dialog.component";
import {ConfirmationDialogComponent} from "../../../common/dialogs/confirmation-dialog/confirmation-dialog.component";

@Component({
  moduleId: module.id,
  templateUrl: './resource-document-panel.component.html',
  styleUrls: ['./resource-document-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ResourceDocumentPanelComponent implements AfterViewInit, BeforeLeaveGuard {
  title: string = "Resources";
  private _resource: ResourceRo;

  private _document: DocumentRo;
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
    this.documentForm = formBuilder.group({
      'mimeType': new FormControl({value: null}),
      'name': new FormControl({value: null}),
      'currentResourceVersion': new FormControl({value: null}),
      'payloadVersion': new FormControl({value: null}),
      'payload': new FormControl({value: null}),
    });
    this.resource = editResourceService.selectedResource

    this.documentForm.controls['payload'].setValue("")
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
    if (!!value) {
      this.documentForm.controls['mimeType'].setValue(value.mimeType);
      this.documentForm.controls['name'].setValue(value.name);
      this.documentForm.controls['currentResourceVersion'].setValue(value.currentResourceVersion);
      this.documentForm.controls['payloadVersion'].setValue(value.payloadVersion);
      this.documentForm.controls['payload'].setValue(!value.payload?"":value.payload);
      this.documentForm.controls['payload'].enable();
      // the method documentVersionsExists already uses the current value to check if versions exists
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


  onSaveButtonClicked(): void {
    this.editResourceService.saveDocumentObservable(this._resource, this.document).subscribe((value: DocumentRo) => {
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
    this.editResourceService.generateDocumentObservable(this._resource).subscribe((value: DocumentRo) => {
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

    const formRef: MatDialogRef<any> = this.dialog.open(DocumentWizardDialogComponent, {
      data: {
        title: "Service group wizard",
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
      this.alertService.error(error.error?.errorDescription)
    });
  }

  validateCurrentDocument(): void {
    this.editResourceService.validateDocumentObservable(this._resource, this.document).subscribe((value: DocumentRo) => {
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

  get emptyDocument(): boolean{
    return !this.documentForm.controls['payload']?.value
  }

  get documentVersionsExists(): boolean{
    return this.getDocumentVersions.length > 0
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

  get showWizardDialog(): boolean {
    // in version DomiSMP 5.0 CR show only the wizard for edelivery-oasis-smp-1.0-servicegroup
    return this._resource?.resourceTypeIdentifier === 'edelivery-oasis-smp-1.0-servicegroup';
  }
}






