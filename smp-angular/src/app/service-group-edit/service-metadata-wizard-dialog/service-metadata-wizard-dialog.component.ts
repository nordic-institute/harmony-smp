import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {CertificateService} from "../../system-settings/user/certificate.service";
import {CertificateRo} from "../../system-settings/user/certificate-ro.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {ServiceMetadataWizardRo} from "./service-metadata-wizard-edit-ro.model";
import {GlobalLookups} from "../../common/global-lookups";

@Component({
  selector: 'service-metadata-wizard-dialog',
  templateUrl: './service-metadata-wizard-dialog.component.html',
  styleUrls: ['./service-metadata-wizard-dialog.component.css']
})
export class ServiceMetadataWizardDialogComponent {

  static readonly NEW_MODE = 'New ServiceMetadata XML';
  static readonly EDIT_MODE = 'Edit ServiceMetadata XML';
  static readonly EBCORE_IDENTIFIER_PREFIX = "urn:oasis:names:tc:ebcore:partyid-type:";

  isNewServiceMetadata: boolean;
  current: ServiceMetadataWizardRo
    & { confirmation?: string };
  dialogForm: UntypedFormGroup;
  certificateValidationMessage: string;
  isCertificateValid: string;
  selectedFile: File;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private http: HttpClient,
    private dialogRef: MatDialogRef<ServiceMetadataWizardDialogComponent>,
    private alertService: AlertMessageService,
    private dialogFormBuilder: UntypedFormBuilder,
    private certificateService: CertificateService,
    private lookups: GlobalLookups,
  ) {
    this.isNewServiceMetadata = this.data.isNewServiceMetadata;

    this.current = {...this.data}

    this.dialogForm = dialogFormBuilder.group({
      'participantIdentifier': new UntypedFormControl({value: '', disabled: true}, null),
      'participantScheme': new UntypedFormControl({value: '', disabled: true}, null),

      'documentIdentifier': new UntypedFormControl({value: '', disabled: !this.isNewServiceMetadata}, [Validators.required]),
      'documentIdentifierScheme': new UntypedFormControl({value: '', disabled: !this.isNewServiceMetadata}, null),
      'processScheme': new UntypedFormControl({value: ''}, null),
      'processIdentifier': new UntypedFormControl({value: ''}, [Validators.required]),

      'transportProfile': new UntypedFormControl({value: ''}, [Validators.required]),
      'endpointUrl': new UntypedFormControl({value: ''}, [Validators.required]),
      'endpointCertificate': new UntypedFormControl({value: ''}, [Validators.required]),

      'serviceDescription': new UntypedFormControl({value: ''}, null),
      'technicalContactUrl': new UntypedFormControl({value: ''}, null),
    });

    this.dialogForm.controls['participantIdentifier'].setValue(this.current.participantIdentifier);
    this.dialogForm.controls['participantScheme'].setValue(this.current.participantScheme);

    this.dialogForm.controls['documentIdentifier'].setValue(this.current.documentIdentifier);
    this.dialogForm.controls['documentIdentifierScheme'].setValue(this.current.documentIdentifierScheme);
    this.dialogForm.controls['transportProfile'].setValue(this.current.transportProfile);

    this.dialogForm.controls['processScheme'].setValue(this.current.processScheme);
    this.dialogForm.controls['processIdentifier'].setValue(this.current.processIdentifier);
    this.dialogForm.controls['endpointUrl'].setValue(this.current.endpointUrl);
    this.dialogForm.controls['endpointCertificate'].setValue(this.current.endpointCertificate);
    this.dialogForm.controls['serviceDescription'].setValue(this.current.serviceDescription);
    this.dialogForm.controls['technicalContactUrl'].setValue(this.current.technicalContactUrl);

  }


  uploadCertificate(event) {
    const file = event.target.files[0];
    this.certificateValidationMessage = null;
    this.certificateService.validateCertificate(file).subscribe((res: CertificateRo) => {
        if (res && res.certificateId) {

          this.dialogForm.patchValue({
            'endpointCertificate': res.encodedValue
          });
        } else {
          this.certificateValidationMessage = 'Error occurred while reading certificate. Check if uploaded file has valid certificate type';
        }
      },
      err => {
        this.certificateValidationMessage = 'Error uploading certificate file [' + file.name + '] ' + err.error?.errorDescription;
      }
    );
  }

  clearAlert() {
    this.certificateValidationMessage = null;
  }


  onFileChanged(event) {
    this.selectedFile = event.target.files[0]
  }

  onUpload() {
    // this.http is the injected HttpClient
    this.certificateService.validateCertificate(this.selectedFile)
      .subscribe(event => {
        console.log(event); // handle event here
      });
  }

  public getCurrent(): ServiceMetadataWizardRo {

    this.current.participantIdentifier = this.dialogForm.controls['participantIdentifier'].value;
    this.current.participantScheme = this.dialogForm.controls['participantScheme'].value;
    this.current.documentIdentifier = this.dialogForm.controls['documentIdentifier'].value;
    this.current.documentIdentifierScheme = this.dialogForm.controls['documentIdentifierScheme'].value;
    this.current.transportProfile = this.dialogForm.controls['transportProfile'].value;

    this.current.endpointUrl = this.dialogForm.controls['endpointUrl'].value;
    this.current.endpointCertificate = this.dialogForm.controls['endpointCertificate'].value;
    this.current.serviceDescription = this.dialogForm.controls['serviceDescription'].value;
    this.current.technicalContactUrl = this.dialogForm.controls['technicalContactUrl'].value;
    this.current.contentXML = this.getServiceMetadataXML();


    return this.current;
  }

  getParticipantElementXML(): string {
    let schema = this.dialogForm.controls['participantScheme'].value;
    let value = this.dialogForm.controls['participantIdentifier'].value;
    if (!!schema && this.lookups.cachedApplicationConfig.concatEBCorePartyId &&
      schema.startsWith(ServiceMetadataWizardDialogComponent.EBCORE_IDENTIFIER_PREFIX)) {
      value = schema + ":" + value;
      schema = null;
    }

    return '<ParticipantIdentifier ' +
      (!schema ? '' : 'scheme="' + this.xmlSpecialChars(schema) + '"') + '>'
      + this.xmlSpecialChars(value) + '</ParticipantIdentifier>';
  }

  getDocumentElementXML(): string {
    return ' <DocumentIdentifier ' +
      (!this.dialogForm.controls['documentIdentifierScheme'].value ? '' : 'scheme="'
        + this.xmlSpecialChars(this.dialogForm.controls['documentIdentifierScheme'].value) + '"') +
      '>' + this.xmlSpecialChars(this.dialogForm.controls['documentIdentifier'].value) + '</DocumentIdentifier>';
  }

  getServiceMetadataXML() {

    let exampleXML = '<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">' +
      '\n    <ServiceInformation>' +
      '\n        ' + this.getParticipantElementXML() +
      '\n        ' + this.getDocumentElementXML() +
      '\n        <ProcessList>' +
      '\n            <Process>' +
      '\n                <ProcessIdentifier ' +
      (!this.dialogForm.controls['processScheme'].value ? '' : 'scheme="' + this.xmlSpecialChars(this.dialogForm.controls['processScheme'].value) + '"') +
      '>' + this.xmlSpecialChars(this.dialogForm.controls['processIdentifier'].value) + '</ProcessIdentifier>' +
      '\n                <ServiceEndpointList>' +
      '\n                   <Endpoint transportProfile="' + this.xmlSpecialChars(this.dialogForm.controls['transportProfile'].value) + '">' +
      '\n                        <EndpointURI>' + this.xmlSpecialChars(this.dialogForm.controls['endpointUrl'].value) + '</EndpointURI>' +
      '\n                        <Certificate>' + this.xmlSpecialChars(this.dialogForm.controls['endpointCertificate'].value) + '</Certificate>' +
      '\n                        <ServiceDescription>' + this.xmlSpecialChars(this.dialogForm.controls['serviceDescription'].value) + '</ServiceDescription>' +
      '\n                        <TechnicalContactUrl>' + this.xmlSpecialChars(this.dialogForm.controls['technicalContactUrl'].value) + '</TechnicalContactUrl>' +
      '\n                    </Endpoint>' +
      '\n                </ServiceEndpointList>' +
      '\n            </Process>' +
      '\n        </ProcessList>' +
      '\n    </ServiceInformation>' +
      '\n</ServiceMetadata>';

    return exampleXML;
  }

  xmlSpecialChars(unsafe) {
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }
}
