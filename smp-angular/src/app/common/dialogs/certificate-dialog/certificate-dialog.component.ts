import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup} from "@angular/forms";
import {CertificateRo} from "../../../user/certificate-ro.model";
import {SecurityService} from "../../../security/security.service";
import {SmpConstants} from "../../../smp.constants";

@Component({
  selector: 'keystore-certificate-dialog',
  templateUrl: './certificate-dialog.component.html'
})
export class CertificateDialogComponent {
  readonly dateTimeFormat: string = SmpConstants.DATE_TIME_FORMAT;
  formTitle: string;
  certificateForm: UntypedFormGroup;
  current: CertificateRo;

  constructor(
    private securityService: SecurityService,
    private dialogRef: MatDialogRef<CertificateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder) {

    this.formTitle = "Certificate details";
    this.current = { ...data.row}

// set empty form ! do not bind it to current object !
    this.certificateForm = fb.group({
      'alias': new UntypedFormControl({value: '', readonly: true}, null),
      'subject': new UntypedFormControl({value: '', readonly: true}, null),
      'validFrom': new UntypedFormControl({value: '', readonly: true}, null),
      'validTo': new UntypedFormControl({value: '', readonly: true}, null),
      'issuer': new UntypedFormControl({value: '', readonly: true}, null),
      'serialNumber': new UntypedFormControl({value: '', readonly: true}, null),
      'certificateId': new UntypedFormControl({value: '', readonly: true}, null),
      'encodedValue': new UntypedFormControl({value: '', readonly: true}, null)

    });

    // certificate authentication
    this.certificateForm.controls['alias'].setValue(this.current.alias);
    this.certificateForm.controls['subject'].setValue(this.current.subject);
    this.certificateForm.controls['validFrom'].setValue(this.current.validFrom);
    this.certificateForm.controls['validTo'].setValue(this.current.validTo);
    this.certificateForm.controls['issuer'].setValue(this.current.issuer);
    this.certificateForm.controls['serialNumber'].setValue(this.current.serialNumber);
    this.certificateForm.controls['certificateId'].setValue(this.current.certificateId);
    this.certificateForm.controls['encodedValue'].setValue(this.current.encodedValue);

  }
}
