import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from "@angular/forms";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {GlobalLookups} from "../../common/global-lookups";
import {CertificateService} from "../../user/certificate.service";
import {CertificateRo} from "../../user/certificate-ro.model";
import {SmpConstants} from "../../smp.constants";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {User} from "../../security/user.model";
import {SecurityService} from "../../security/security.service";
import {KeystoreResult} from "../keystore-result.model";
import {KeystoreService} from "../keystore.service";

@Component({
  selector: 'keystore-import-dialog',
  templateUrl: './keystore-import-dialog.component.html'
})
export class KeystoreImportDialogComponent {
  formTitle: string;
  dialogForm: UntypedFormGroup;

  selectedFile: File;

  constructor(private keystoreService: KeystoreService,
              private securityService: SecurityService,
              private http: HttpClient,
              public lookups: GlobalLookups,
              private dialogRef: MatDialogRef<KeystoreImportDialogComponent>,
              private alertService: AlertMessageService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: UntypedFormBuilder) {

    this.formTitle = "Keystore import dialog";

    this.dialogForm = fb.group({
      'file': new UntypedFormControl({value: ''}, [Validators.required]),
      'keystoreType': new UntypedFormControl({value: ''}, [Validators.required]),
      'password': new UntypedFormControl({value: ''}, [Validators.required]),
    });
    this.dialogForm.controls['keystoreType'].setValue("JKS");
    this.dialogForm.controls['password'].setValue("");
    this.dialogForm.controls['file'].setValue("");
  }

  keystoreFileSelected(event) {
    this.selectedFile = event.target.files[0];
    this.dialogForm.controls['file'].setValue(this.selectedFile ? this.selectedFile.name : "");
  }

  importKeystore() {
    this.keystoreService.uploadKeystore$(this.selectedFile,this.dialogForm.controls['keystoreType'].value,
      this.dialogForm.controls['password'].value ).subscribe((res: KeystoreResult) => {
        if (res) {
          if (res.errorMessage){
            this.alertService.exception("Error occurred while importing keystore:" + this.selectedFile.name , res.errorMessage, false);
          } else {
            this.alertService.success("Keystore " + this.selectedFile.name + " imported!");
            this.lookups.refreshCertificateLookup();
            this.dialogRef.close();
          }
        } else {
          this.alertService.exception("Error occurred while reading keystore.", "Check if uploaded file has valid keystore type.", false);
        }
      },
      err => {
        this.alertService.exception('Error uploading keystore file ' + this.selectedFile.name,  err.error?.errorDescription);
      }
    )
  }
}
