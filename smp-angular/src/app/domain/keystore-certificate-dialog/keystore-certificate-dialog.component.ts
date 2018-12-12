import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AlertService} from "../../alert/alert.service";
import {GlobalLookups} from "../../common/global-lookups";
import {CertificateService} from "../../user/certificate.service";
import {CertificateRo} from "../../user/certificate-ro.model";
import {SmpConstants} from "../../smp.constants";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {User} from "../../security/user.model";
import {SecurityService} from "../../security/security.service";
import {UserDetailsDialogMode} from "../../user/user-details-dialog/user-details-dialog.component";

@Component({
  selector: 'keystore-certificate-dialog',
  templateUrl: './keystore-certificate-dialog.component.html'
})
export class KeystoreCertificateDialogComponent {
  formTitle: string;
  certificateForm: FormGroup;

  current:CertificateRo;

  constructor(
              private securityService: SecurityService,
              private dialogRef: MatDialogRef<KeystoreCertificateDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {

    this.formTitle = "Certificate details";
    this.current  = {...data.row}

// set empty form ! do not bind it to current object !
    this.certificateForm = fb.group({
      'alias': new FormControl({ value: '', readonly: true }, null),
      'subject': new FormControl({ value: '', readonly: true }, null),
      'validFrom': new FormControl({ value: '', readonly: true }, null),
      'validTo': new FormControl({ value: '', readonly: true }, null),
      'issuer': new FormControl({ value: '', readonly: true }, null),
      'serialNumber': new FormControl({ value: '', readonly: true }, null),
      'certificateId': new FormControl({ value: '', readonly: true }, null),
      'encodedValue': new FormControl({ value: '', readonly: true }, null)

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
