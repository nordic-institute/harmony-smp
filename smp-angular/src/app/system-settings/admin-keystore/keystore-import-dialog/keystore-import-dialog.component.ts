import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormControl, FormGroup, UntypedFormBuilder, Validators} from "@angular/forms";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {AdminKeystoreService} from "../admin-keystore.service";
import {KeystoreResult} from "../../../common/model/keystore-result.model";

@Component({
  selector: 'keystore-import-dialog',
  templateUrl: './keystore-import-dialog.component.html'
})
export class KeystoreImportDialogComponent {
  formTitle: string;
  dialogForm: FormGroup;

  selectedFile: File;

  constructor(private keystoreService: AdminKeystoreService,
              private dialogRef: MatDialogRef<KeystoreImportDialogComponent>,
              private alertService: AlertMessageService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: UntypedFormBuilder) {

    this.formTitle = "Keystore import dialog";

    this.dialogForm = this.fb.group({
      'file': new FormControl({value: '', readonly: false}, [Validators.required]),
      'keystoreType': new FormControl({value: '', readonly: false}, [Validators.required]),
      'password': new FormControl({value: '', readonly: false}, [Validators.required]),
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
    this.keystoreService.uploadKeystore(this.selectedFile, this.dialogForm.controls['keystoreType'].value,
      this.dialogForm.controls['password'].value).subscribe({next: (res: KeystoreResult) => {
        if (res) {
          if (res.errorMessage) {
            this.alertService.exception("Error occurred while importing keystore:" + this.selectedFile.name, res.errorMessage, false);
          } else {
            if (res.ignoredAliases) {
              this.alertService.warning("The following aliases have been ignored because they were already present in the current keystore: " + res.ignoredAliases.join(","), false);
            }
            this.keystoreService.notifyKeystoreEntriesUpdated(res.addedCertificates);
            this.dialogRef.close();
          }
        } else {
          this.alertService.exception("Error occurred while reading keystore.", "Check if uploaded file has valid keystore type.", false);
        }
      },
      error: (err) => {
        this.alertService.exception('Error uploading keystore file ' + this.selectedFile.name, err.error?.errorDescription);
      }
    })
  }
}
