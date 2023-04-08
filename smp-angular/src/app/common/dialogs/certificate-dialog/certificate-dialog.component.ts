import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {UntypedFormBuilder} from "@angular/forms";
import {CertificateRo} from "../../../system-settings/user/certificate-ro.model";
import {SmpConstants} from "../../../smp.constants";

@Component({
  selector: 'keystore-certificate-dialog',
  templateUrl: './certificate-dialog.component.html'
})
export class CertificateDialogComponent {
  readonly dateTimeFormat: string = SmpConstants.DATE_TIME_FORMAT;
  formTitle: string;
  current: CertificateRo;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder) {

    this.formTitle = "Certificate details";
    this.current = {...data.row}

  }
}
