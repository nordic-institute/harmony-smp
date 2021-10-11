import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {CertificateRo} from "../../user/certificate-ro.model";
import {SecurityService} from "../../security/security.service";

@Component({
  selector: 'keystore-certificate-dialog',
  templateUrl: './certificate-dialog.component.html'
})
export class CertificateDialogComponent {
  formTitle: string;
  certificateForm: FormGroup;

  current: CertificateRo;

  constructor(
    private securityService: SecurityService,
    private dialogRef: MatDialogRef<CertificateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder) {

    this.formTitle = "Certificate details";
    this.current = {...data.row}

// set empty form ! do not bind it to current object !
    this.certificateForm = fb.group({
      'alias': new FormControl({value: '', readonly: true}, null),
      'subject': new FormControl({value: '', readonly: true}, null),
      'validFrom': new FormControl({value: '', readonly: true}, null),
      'validTo': new FormControl({value: '', readonly: true}, null),
      'issuer': new FormControl({value: '', readonly: true}, null),
      'serialNumber': new FormControl({value: '', readonly: true}, null),
      'certificateId': new FormControl({value: '', readonly: true}, null),
      'encodedValue': new FormControl({value: '', readonly: true}, null)

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
