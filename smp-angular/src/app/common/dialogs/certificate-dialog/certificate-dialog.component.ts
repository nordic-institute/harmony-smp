import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {UntypedFormBuilder} from "@angular/forms";
import {SmpConstants} from "../../../smp.constants";
import {CertificateRo} from "../../model/certificate-ro.model";
import {TranslateService} from "@ngx-translate/core";

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
    private fb: UntypedFormBuilder,
    private translateService: TranslateService) {

    this.translateService.get("certificate.dialog.title").subscribe(title => this.formTitle = title);
    this.current = {...data.row}

  }
}
