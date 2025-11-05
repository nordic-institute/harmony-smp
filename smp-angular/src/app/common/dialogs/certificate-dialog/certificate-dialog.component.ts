import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder} from "@angular/forms";
import {CertificateRo} from "../../model/certificate-ro.model";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import {CertificateService} from "../../services/certificate.service";
import {HttpErrorHandlerService} from "../../error/http-error-handler.service";
import {CertificatePanelComponent} from "../../panels/certificate-panel/certificate-panel.component";
import {EntityStatus} from "../../enums/entity-status.enum";

@Component({
  selector: 'keystore-certificate-dialog',
  templateUrl: './certificate-dialog.component.html',
  styleUrls: ['./certificate-dialog.component.css'],
  standalone: false
})
export class CertificateDialogComponent {

  formTitle: string;
  current: CertificateRo;

  // certificate specific data
  newCertFile: File = null;
  enableCertificateImport: boolean = true;
  // alert message
  message: string;
  messageType: string = "alert-error";


  @ViewChild("certificatePanel")
  certificatePanel: CertificatePanelComponent;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder,
    private certificateService: CertificateService,
    public dialogRef: MatDialogRef<CertificateDialogComponent>,
    private httpErrorHandlerService: HttpErrorHandlerService,
    private translateService: TranslateService) {

    this.translateService.get("certificate.dialog.title").subscribe(title => this.formTitle = title);
    this.current = !data?.row ? {status: EntityStatus.NEW, certificateId: ''} as CertificateRo : {...data.row}
    this.enableCertificateImport = data?.enableImport;
  }

  uploadCertificate(event: any) {
    this.newCertFile = null;
    const file = event.target.files[0];
    this.certificateService.validateCertificate(file).subscribe(async (res: CertificateRo) => {
        if (res && res.certificateId) {
          this.current = res;
          this.enableCertificateImport = !res.error;
          if (res.invalid) {
            this.showErrorMessage(res.invalidReason, res.error);

          } else {
            this.clearAlert()
          }
          this.newCertFile = file;
        } else {
          this.clearCertificateData()
          this.showErrorMessage(await lastValueFrom(this.translateService.get("credentials.dialog.error.read.certificate")), true)
        }
      },
      async err => {
        this.clearCertificateData()
        if (this.httpErrorHandlerService.logoutOnInvalidSessionError(err)) {
          this.closeDialog();
          return;
        }
        this.showErrorMessage(await lastValueFrom(this.translateService.get("credentials.dialog.error.upload.certificate", {
          fileName: file.name,
          errorDescription: err.error?.errorDescription
        })), true);
      }
    );
  }

  storeCertificateCredentials() {
    this.clearAlert();
    this.closeDialog(this.current)
  }

  clearCertificateData() {
    this.certificatePanel.clearData();
  }


  showSuccessMessage(value: string) {
    this.message = value;
    this.messageType = "success";
  }

  showErrorMessage(value: string, errorLevel: boolean) {
    this.message = value;
    this.messageType = errorLevel ? "error" : "warning";
  }

  clearAlert() {
    this.message = null;
    this.messageType = null;
  }

  closeDialog(certificateRo?: CertificateRo) {
    if (certificateRo) {
      this.dialogRef.close(certificateRo)
      return;
    }
    this.dialogRef.close()
  }
}
