import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormControl, FormGroup, UntypedFormBuilder, Validators} from "@angular/forms";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {AdminKeystoreService} from "../admin-keystore.service";
import {KeystoreResult} from "../../../common/model/keystore-result.model";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";

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
              private fb: UntypedFormBuilder,
              private translateService: TranslateService) {

    this.translateService.get("keystore.import.dialog.title").subscribe(value => this.formTitle = value);

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
      this.dialogForm.controls['password'].value).subscribe({next: async (res: KeystoreResult) => {
        if (res) {
          if (res.errorMessage) {
            this.alertService.exception(await lastValueFrom(this.translateService.get("keystore.import.dialog.error.import", {fileName: this.selectedFile.name})), res.errorMessage, false);
          } else {
            if (res.ignoredAliases) {
              this.alertService.warning(await lastValueFrom(this.translateService.get("keystore.import.dialog.warning.ignored.aliases", {ignoredAliases: res.ignoredAliases.join(",")})), false);
            }
            this.keystoreService.notifyKeystoreEntriesUpdated(res.addedCertificates);
            this.dialogRef.close();
          }
        } else {
          this.alertService.exception(await lastValueFrom(this.translateService.get("keystore.import.dialog.error.generic")),
            await lastValueFrom(this.translateService.get("keystore.import.dialog.error.generic.message")), false);
        }
      },
      error: async (err) => {
        this.alertService.exception(await lastValueFrom(this.translateService.get("keystore.import.dialog.error.upload", {fileName: this.selectedFile.name})), err.error?.errorDescription);
      }
    })
  }
}
