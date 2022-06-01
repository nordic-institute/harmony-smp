import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {SearchTableEntityStatus} from "../../common/search-table/search-table-entity-status.model";
import {ServiceMetadataEditRo} from "../service-metadata-edit-ro.model";
import {GlobalLookups} from "../../common/global-lookups";
import {ServiceMetadataWizardDialogComponent} from "../service-metadata-wizard-dialog/service-metadata-wizard-dialog.component";
import {ServiceGroupEditRo} from "../service-group-edit-ro.model";
import {SmpConstants} from "../../smp.constants";
import {Observable} from "rxjs/internal/Observable";
import {HttpClient} from "@angular/common/http";
import {ServiceGroupDomainEditRo} from "../service-group-domain-edit-ro.model";
import {ServiceMetadataValidationEditRo} from "./service-metadata-validation-edit-ro.model";
import {ServiceMetadataWizardRo} from "../service-metadata-wizard-dialog/service-metadata-wizard-edit-ro.model";

@Component({
  selector: 'service-group-metadata-dialog',
  templateUrl: './service-group-metadata-dialog.component.html',
  styleUrls: ['./service-group-metadata-dialog.component.css']
})
export class ServiceGroupMetadataDialogComponent implements OnInit {

  static readonly NEW_MODE = 'New ServiceMetadata';
  static readonly EDIT_MODE = 'Edit ServiceMetadata';

  @ViewChild('domainList') domainList: any;


  editMode: boolean;
  formTitle: string;
  current: ServiceMetadataEditRo & { confirmation?: string };
  currentServiceGroup: ServiceGroupEditRo;
  dialogForm: FormGroup;
  metadataValidationMessage: string;
  xmlServiceMetadataObserver: Observable<ServiceMetadataEditRo>;
  isMetadataValid: boolean = true;


  constructor(public dialog: MatDialog,
              protected http: HttpClient,
              public lookups: GlobalLookups,
              private dialogRef: MatDialogRef<ServiceGroupMetadataDialogComponent>,
              private alertService: AlertMessageService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {

    this.editMode = data.edit;
    this.formTitle = this.editMode ? ServiceGroupMetadataDialogComponent.EDIT_MODE : ServiceGroupMetadataDialogComponent.NEW_MODE;
    this.currentServiceGroup = data.serviceGroup;
    this.current = this.editMode
      ? {
        ...data.metadata,
      }
      : {
        documentIdentifier: '',
        documentIdentifierScheme: '',
        smlSubdomain: this.currentServiceGroup.serviceGroupDomains[0].smlSubdomain,
        domainCode: this.currentServiceGroup.serviceGroupDomains[0].domainCode,
        domainId: null,
        status: SearchTableEntityStatus.NEW,
        xmlContentStatus: SearchTableEntityStatus.NEW,
      };

    this.dialogForm = fb.group({
      'participantIdentifier': new FormControl({value: this.currentServiceGroup.participantIdentifier, disabled: true}),
      'participantScheme': new FormControl({value: this.currentServiceGroup.participantScheme, disabled: true}),
      'domainCode': new FormControl({}, [Validators.required]),

      'documentIdentifier': new FormControl({value: this.current.documentIdentifier, disabled: this.editMode},
        [Validators.required]),
      'documentIdentifierScheme': new FormControl({
          value: this.current.documentIdentifierScheme,
          disabled: this.editMode
        },
        []),
      'xmlContent': new FormControl({value: ''}, [Validators.required]),
    });

    // update values
    this.dialogForm.controls['domainCode'].setValue(this.current.domainCode);
    this.dialogForm.controls['xmlContent'].setValue(this.current.xmlContent);
  }

  ngOnInit() {

    // retrieve xml extension for this service group
    if (this.current.status !== SearchTableEntityStatus.NEW && !this.current.xmlContent) {
      // init domains
      this.xmlServiceMetadataObserver = this.http.get<ServiceMetadataEditRo>(SmpConstants.REST_METADATA + '/' + this.current.id);
      this.xmlServiceMetadataObserver.subscribe((res: ServiceMetadataEditRo) => {
        this.dialogForm.get('xmlContent').setValue(res.xmlContent);
        // store init xml to current value for change validation
        this.current.xmlContent=res.xmlContent;
      });
    }

  }

  checkValidity(g: FormGroup) {
    Object.keys(g.controls).forEach(key => {
      g.get(key).markAsDirty();
    });
    Object.keys(g.controls).forEach(key => {
      g.get(key).markAsTouched();
    });
    //!!! updateValueAndValidity - else some filed did no update current / on blur never happened
    Object.keys(g.controls).forEach(key => {
      g.get(key).updateValueAndValidity();
    });
  }

  submitForm() {
    this.checkValidity(this.dialogForm);

    // before closing check the schema
    let request: ServiceMetadataValidationEditRo = {
      participantScheme: this.dialogForm.controls['participantScheme'].value,
      participantIdentifier: this.dialogForm.controls['participantIdentifier'].value,
      documentIdentifierScheme: !this.dialogForm.controls['documentIdentifierScheme'].value?null:
      this.dialogForm.controls['documentIdentifierScheme'].value,
      documentIdentifier: this.dialogForm.controls['documentIdentifier'].value,
      xmlContent: this.dialogForm.controls['xmlContent'].value,
      statusAction: this.editMode?SearchTableEntityStatus.UPDATED:SearchTableEntityStatus.NEW,
    }
    //
    let validationObservable = this.http.post<ServiceMetadataValidationEditRo>(SmpConstants.REST_METADATA_VALIDATE, request);
    validationObservable.subscribe((res: ServiceMetadataValidationEditRo) => {
      if (res.errorMessage) {
        this.metadataValidationMessage = res.errorMessage;
        this.isMetadataValid = false;
      } else {
        this.metadataValidationMessage = "ServiceMetada is valid!";
        this.isMetadataValid = true;
        // now we can close the dialog
        this.dialogRef.close(true);
      }

    });
  }

  onClearServiceMetadata() {
    this.dialogForm.controls['xmlContent'].setValue("");
  }

  onStartWizardDialog() {

    let serviceMetadataWizard: ServiceMetadataWizardRo = {
      isNewServiceMetadata: !this.editMode,
      participantIdentifier: this.dialogForm.controls['participantIdentifier'].value,
      participantScheme: this.dialogForm.controls['participantScheme'].value,
      documentIdentifier: this.dialogForm.controls['documentIdentifier'].value,
      documentIdentifierScheme: this.dialogForm.controls['documentIdentifierScheme'].value,
      processIdentifier: '',
      processScheme: '',
      transportProfile: 'bdxr-transport-ebms3-as4-v1p0', // default value for oasis AS4

      endpointUrl: '',
      endpointCertificate: '',

      serviceDescription: '',
      technicalContactUrl: '',

    }

    let wizardInit: MatDialogConfig = {
      data: serviceMetadataWizard
    }



    const formRef: MatDialogRef<any> = this.dialog.open(ServiceMetadataWizardDialogComponent,wizardInit);
    formRef.afterClosed().subscribe(result => {
      if (result) {

        let smw: ServiceMetadataWizardRo =  formRef.componentInstance.getCurrent();
        this.dialogForm.controls['xmlContent'].setValue(smw.contentXML);
        if(!this.editMode){
          this.dialogForm.controls['documentIdentifierScheme'].setValue(smw.documentIdentifierScheme);
          this.dialogForm.controls['documentIdentifier'].setValue(smw.documentIdentifier);
        }
      }
    });
  }

  getParticipantElementXML(): string {
    let schema = this.dialogForm.controls['participantScheme'].value;
    let value= this.dialogForm.controls['participantIdentifier'].value;
    if (!!schema && this.lookups.cachedApplicationConfig.concatEBCorePartyId &&
      schema.startsWith(ServiceMetadataWizardDialogComponent.EBCORE_IDENTIFIER_PREFIX) ) {
      value = schema + ":" +  value;
      schema =null;
    }

    return  '<ParticipantIdentifier ' +
      (!schema?'': 'scheme="' + this.xmlSpecialChars(schema) + '"')+ '>'
      + this.xmlSpecialChars(value)+ '</ParticipantIdentifier>';
  }

  getDocumentElementXML(): string {
    return  ' <DocumentIdentifier ' +
      (!this.dialogForm.controls['documentIdentifierScheme'].value?'': 'scheme="'
        + this.xmlSpecialChars(this.dialogForm.controls['documentIdentifierScheme'].value) + '"') +
      '>' + this.xmlSpecialChars(this.dialogForm.controls['documentIdentifier'].value) + '</DocumentIdentifier>';
  }

  onGenerateSimpleXML() {
    let exampleXML = '<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">' +
      '\n    <ServiceInformation>' +
      '\n        ' + this.getParticipantElementXML() +
      '\n        ' + this.getDocumentElementXML() +
      '\n        <ProcessList>' +
      '\n            <Process>' +
      '\n                <ProcessIdentifier scheme="[enterProcessType]">[enterProcessName]</ProcessIdentifier>' +
      '\n                <ServiceEndpointList>' +
      '\n                   <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">' +
      '\n                        <EndpointURI>https://mypage.eu</EndpointURI>' +
      '\n                        <Certificate>UGFzdGUgYmFzZTY0IGVuY29kZWQgY2VydGlmaWNhdGUgb2YgQVA=</Certificate>' +
      '\n                        <ServiceDescription>Service description for partners</ServiceDescription>' +
      '\n                        <TechnicalContactUrl>www.best-page.eu</TechnicalContactUrl>' +
      '\n                    </Endpoint>' +
      '\n                </ServiceEndpointList>' +
      '\n            </Process>' +
      '\n        </ProcessList>' +
      '\n    </ServiceInformation>' +
      '\n</ServiceMetadata>';
    this.dialogForm.controls['xmlContent'].setValue(exampleXML);
  }

  xmlSpecialChars(unsafe) {
    return !unsafe?'':unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  onServiceMetadataValidate() {

    let request: ServiceMetadataValidationEditRo = {

      participantScheme: this.dialogForm.controls['participantScheme'].value,
      participantIdentifier: this.dialogForm.controls['participantIdentifier'].value,
      documentIdentifierScheme: !this.dialogForm.controls['documentIdentifierScheme'].value?null:
        this.dialogForm.controls['documentIdentifierScheme'].value,
      documentIdentifier: this.dialogForm.controls['documentIdentifier'].value,
      xmlContent: this.dialogForm.controls['xmlContent'].value,
      statusAction: this.editMode?SearchTableEntityStatus.UPDATED:SearchTableEntityStatus.NEW,
    }
    //
    let validationObservable = this.http.post<ServiceMetadataValidationEditRo>(SmpConstants.REST_METADATA_VALIDATE, request);
    validationObservable.subscribe((res: ServiceMetadataValidationEditRo) => {
      if (res.errorMessage) {
        this.metadataValidationMessage = res.errorMessage;
        this.isMetadataValid = false;
      } else {
        this.metadataValidationMessage = "Servicemetadata is valid!";
        this.isMetadataValid = true;
      }

    });
  }

  public getCurrent(): ServiceMetadataEditRo {

    this.current.domainCode = this.domainList.selected.value.domainCode;
    this.current.smlSubdomain = this.domainList.selected.value.smlSubdomain;
    this.current.domainId = this.domainList.selected.value.domainId;

    this.current.xmlContent = this.dialogForm.value['xmlContent'];
    // change this two properties only on new
    if (this.current.status === SearchTableEntityStatus.NEW) {
      this.current.documentIdentifier = this.dialogForm.value['documentIdentifier'];
      this.current.documentIdentifierScheme = this.dialogForm.value['documentIdentifierScheme'];
    } else if (this.current.status === SearchTableEntityStatus.PERSISTED) {
      this.current.status = SearchTableEntityStatus.UPDATED;
      this.current.xmlContentStatus = SearchTableEntityStatus.UPDATED;
    }
    return this.current;
  }

  //!! this two method must be called before getCurrent
  public isMetaDataXMLChanged():boolean{
     return  this.dialogForm.value['xmlContent'] !== this.current.xmlContent;
  }
  public isServiceMetaDataChanged():boolean{
    return  this.isMetaDataXMLChanged() || !this.isEqual(this.current.domainCode, this.domainList.selected.value.domainCode) ;
  }

  compareDomainCode(sgDomain: ServiceGroupDomainEditRo, domainCode: String): boolean {
    return sgDomain.domainCode === domainCode;
  }

  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }
}
