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

@Component({
  selector: 'keystore-import-dialog',
  templateUrl: './keystore-import-dialog.component.html'
})
export class KeystoreImportDialogComponent {
  formTitle: string;
  dialogForm: FormGroup;

  selectedFile: File;

  constructor(private certificateService: CertificateService,
              private securityService: SecurityService,
              private http: HttpClient,
              public lookups: GlobalLookups,
              private dialogRef: MatDialogRef<KeystoreImportDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {

    this.formTitle = "Keystore import dialog";

    this.dialogForm = fb.group({
      'file': new FormControl({value: ''}, [Validators.required]),
      'keystoreType': new FormControl({value: ''}, [Validators.required]),
      'password': new FormControl({value: ''}, [Validators.required]),
    });
    this.dialogForm.controls['keystoreType'].setValue("JKS");
    this.dialogForm.controls['password'].setValue("");
    this.dialogForm.controls['file'].setValue("");


  }


  keystoreFileSelected(event) {
    this.selectedFile = event.target.files[0];
    this.dialogForm.controls['file'].setValue(this.selectedFile ? this.selectedFile.name : "");
  }


  importKeystoreMultipart() {
    const headers = new HttpHeaders()
      .set("Content-Type", "multipart/form-data");

    const params: HttpParams = new HttpParams();
    params.set('keystoreType', this.dialogForm.controls['keystoreType'].value);
    params.set('password', this.dialogForm.controls['password'].value);
    let keystoreType = this.dialogForm.controls['keystoreType'].value;
    let password = encodeURI(this.dialogForm.controls['password'].value);

    let input = new FormData();
// Add your values in here
    input.append('keystoreType',keystoreType);
    input.append('password', password);
    input.append('file', this.selectedFile);

    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<CertificateRo>(`${SmpConstants.REST_KEYSTORE}/${currentUser.id}/upload/`, input).subscribe((res: CertificateRo) => {
        if (res ) {
          this.alertService.success("Keystore "+this.selectedFile.name+ " imported!");
        } else {
          this.alertService.exception("Error occurred while reading certificate.", "Check if uploaded file has valid certificate type.", false);
        }
      },
      err => {
        this.alertService.exception('Error uploading certificate file ' + this.selectedFile.name, err);
      }
    )

  }

  importKeystore() {
    const headers = new HttpHeaders()
      .set("Content-Type", "application/octet-stream");

    const params: HttpParams = new HttpParams();
    params.set('keystoreType', this.dialogForm.controls['keystoreType'].value);
    params.set('password', this.dialogForm.controls['password'].value);
    let keystoreType = this.dialogForm.controls['keystoreType'].value;
    let password = encodeURIComponent(this.dialogForm.controls['password'].value);


    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<CertificateRo>(`${SmpConstants.REST_KEYSTORE}/${currentUser.id}/upload/${keystoreType}/${password}`, this.selectedFile, {headers,params }).subscribe((res: CertificateRo) => {
        if (res ) {
          this.alertService.success("Keystore "+this.selectedFile.name+ " imported!");
          this.lookups.refreshCertificateLookup();
        } else {
          this.alertService.exception("Error occurred while reading certificate.", "Check if uploaded file has valid certificate type.", false);
        }
      },
      err => {
        this.alertService.exception('Error uploading certificate file ' + this.selectedFile.name, err);
      }
    )

  }


}
