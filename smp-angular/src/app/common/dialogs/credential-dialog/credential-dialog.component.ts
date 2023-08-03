import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {SmpConstants} from "../../../smp.constants";
import {AccessTokenRo} from "../../model/access-token-ro.model";
import {UserService} from "../../../system-settings/user/user.service";
import {CredentialRo} from "../../../security/credential.model";
import {CertificateRo} from "../../../system-settings/user/certificate-ro.model";
import {CertificateService} from "../../../system-settings/user/certificate.service";
import {HttpErrorHandlerService} from "../../error/http-error-handler.service";


@Component({
  templateUrl: './credential-dialog.component.html',
  styleUrls: ['./credential-dialog.component.css']
})
export class CredentialDialogComponent {
  public static CERTIFICATE_TYPE: string = "CERTIFICATE";
  public static ACCESS_TOKEN_TYPE: string = "ACCESS_TOKEN";

  dateTimeFormat: string = SmpConstants.DATE_TIME_FORMAT;
  formTitle = "Access token generation dialog";
  credentialForm: FormGroup;
  certificateForm: FormGroup;

  message: string;
  messageType: string = "alert-error";
  credentialType: string;

  isReadOnly: boolean = false;
  // certificate specific data
  newCertFile: File = null;
  enableCertificateImport: boolean = true;


  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private userService: UserService,
              private httpErrorHandlerService: HttpErrorHandlerService,
              private certificateService: CertificateService,
              public dialogRef: MatDialogRef<CredentialDialogComponent>,
              private formBuilder: FormBuilder
  ) {
    dialogRef.disableClose = true;//disable default close operation
    this.formTitle = data.formTitle;
    this.credentialType = data.credentialType;
    this.credentialForm = formBuilder.group({
      // common values
      'active': new FormControl({value: 'true', readonly: this.isReadOnly}),
      'description': new FormControl({value: '', readonly: this.isReadOnly}),
      'activeFrom': new FormControl({value: '', readonly: this.isReadOnly}),
      'expireOn': new FormControl({value: '', readonly: this.isReadOnly})
    });
    // create certificate form
    this.certificateForm = formBuilder.group({
      'subject': new FormControl({value: null, readonly: true}),
      'validFrom': new FormControl({value: null, readonly: true}),
      'validTo': new FormControl({value: null, readonly: true}),
      'issuer': new FormControl({value: null, readonly: true}),
      'serialNumber': new FormControl({value: null, readonly: true}),
      'crlUrl': new FormControl({value: null, readonly: true}),
      'certificateId': new FormControl({value: null, readonly: true}),
      'encodedValue': new FormControl({value: null, readonly: true})
    });

    this.credentialForm.controls['active'].setValue(true);
    this.credentialForm.controls['description'].setValue('');
    this.credentialForm.controls['activeFrom'].setValue('');
    this.credentialForm.controls['expireOn'].setValue('');

    this.clearCertificateData()

    this.setDisabled(false);
  }

  clearCertificateData() {
    this.certificateForm.controls['subject'].setValue('');
    this.certificateForm.controls['validFrom'].setValue('');
    this.certificateForm.controls['validTo'].setValue('');
    this.certificateForm.controls['issuer'].setValue('');
    this.certificateForm.controls['serialNumber'].setValue('');
    this.certificateForm.controls['crlUrl'].setValue('');
    this.certificateForm.controls['certificateId'].setValue('');
    this.certificateForm.controls['encodedValue'].setValue('');
    this.enableCertificateImport = false;
  }

  get isAccessTokenType(): boolean {
    return this.credentialType === CredentialDialogComponent.ACCESS_TOKEN_TYPE;
  }

  get isCertificateType(): boolean {
    return this.credentialType === CredentialDialogComponent.CERTIFICATE_TYPE;
  }

  setDisabled(disabled: boolean) {
    if (disabled) {
      this.credentialForm.controls['active'].disable();
      this.credentialForm.controls['description'].disable();
      this.credentialForm.controls['activeFrom'].disable();
      this.credentialForm.controls['expireOn'].disable();
    } else {
      this.credentialForm.controls['active'].enable();
      this.credentialForm.controls['description'].enable();
      this.credentialForm.controls['activeFrom'].enable();
      this.credentialForm.controls['expireOn'].enable();
      if (this.isCertificateType) {
        this.credentialForm.controls['activeFrom'].disable();
        this.credentialForm.controls['expireOn'].disable();
      }

    }
    this.isReadOnly = disabled

  }

  submitForm() {
    if (this.isAccessTokenType) {
      this.generatedAccessToken();
    } else if (this.isCertificateType) {
      this.storeCertificateCredentials();
    }
  }


  uploadCertificate(event) {
    this.newCertFile = null;
    const file = event.target.files[0];
    this.certificateService.validateCertificate(file).subscribe((res: CertificateRo) => {
        if (res && res.certificateId) {
          this.certificateForm.patchValue({
            'subject': res.subject,
            'validFrom': res.validFrom,
            'validTo': res.validTo,
            'issuer': res.issuer,
            'serialNumber': res.serialNumber,
            'certificateId': res.certificateId,
            'crlUrl': res.crlUrl,
            'encodedValue': res.encodedValue,
            'isCertificateValid': !res.invalid
          });
          this.enableCertificateImport = !res.error;
          if (res.invalid) {
            this.showErrorMessage(res.invalidReason, res.error);

          } else {
            this.clearAlert()
          }

          this.credentialForm.controls['activeFrom'].setValue(res.validFrom);
          this.credentialForm.controls['expireOn'].setValue(res.validTo);

          this.newCertFile = file;
        } else {
          this.clearCertificateData()
          this.showErrorMessage("Error occurred while reading certificate. Check if uploaded file has valid certificate type", true)
        }
      },
      err => {
        this.clearCertificateData()
        if (this.httpErrorHandlerService.logoutOnInvalidSessionError(err)){
          this.closeDialog();
          return;
        }
        this.showErrorMessage("Error uploading certificate file [" + file.name + "]." + err.error?.errorDescription, true)
      }
    );
  }

  storeCertificateCredentials() {
    this.clearAlert();
    this.userService.storeUserCertificateCredential(this.initCredential);
    this.closeDialog();
  }


  generatedAccessToken() {

    this.clearAlert();
    this.userService.generateUserAccessTokenCredential(this.initCredential).subscribe((response: AccessTokenRo) => {
      this.showSuccessMessage("Token with ID: \"" + response.identifier + "\" and value: \"" + response.value + "\" was generated!" +
        "<br \><br \>Copy the access token's value and save it in a safe space. <br \><b>You won't be able to see your token's value once you click Close.</b>")
      this.userService.notifyAccessTokenUpdated(response.credential);
      this.setDisabled(true);
    }, (err) => {
      if (this.httpErrorHandlerService.logoutOnInvalidSessionError(err)){
        this.closeDialog();
        return;
      }
      this.showErrorMessage(err.error.errorDescription, true);
    });
  }

  get initCredential(): CredentialRo {
    let credential: CredentialRo = {
      name: "",
      active: this.credentialForm.controls['active'].value,
      description: this.credentialForm.controls['description'].value,
      activeFrom: this.credentialForm.controls['activeFrom'].value,
      expireOn: this.credentialForm.controls['expireOn'].value,
    }
    if (this.isCertificateType) {
      credential.certificate = this.certificateData;
    }
    return credential;
  }

  get certificateData(): CertificateRo {
    if (this.isCertificateType) {
      return {
        certificateId: this.certificateForm.controls['certificateId'].value,
        subject: this.certificateForm.controls['subject'].value,
        issuer: this.certificateForm.controls['issuer'].value,
        serialNumber: this.certificateForm.controls['serialNumber'].value,
        validFrom: this.certificateForm.controls['validFrom'].value,
        validTo: this.certificateForm.controls['validTo'].value,
        crlUrl: this.certificateForm.controls['crlUrl'].value,
        encodedValue: this.certificateForm.controls['encodedValue'].value
      } as CertificateRo
    }
    return null;
  }

  get minSelectableDate(): Date {
    return this.credentialType == CredentialDialogComponent.ACCESS_TOKEN_TYPE ? new Date() : null;
  }

  showSuccessMessage(value: string) {
    this.message = value;
    this.messageType = "success";
  }

  showErrorMessage(value: string, errorLevel:boolean) {
    this.message = value;
    this.messageType =errorLevel?"error":"warning";
  }

  clearAlert() {
    this.message = null;
    this.messageType = null;
  }


  closeDialog() {
    this.dialogRef.close()
  }
}
