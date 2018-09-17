import {Component, EventEmitter, Inject, ViewChild} from "@angular/core";
import {MD_DIALOG_DATA, MdDialogRef} from "@angular/material";
import {TrustStoreService} from "../trustore.service";
import {AlertService} from "../../alert/alert.service";
import {isNullOrUndefined, isString} from "util";
import {isEmpty} from "rxjs/operator/isEmpty";

@Component({
  selector: 'app-trustore-upload',
  templateUrl: './truststore-upload.component.html',
})
export class TrustStoreUploadComponent {

  @ViewChild('fileInput')
  private fileInput;

  password: any;
  onTruststoreUploaded = new EventEmitter();
  enableSubmit = false;

  constructor(public dialogRef: MdDialogRef<TrustStoreUploadComponent>,
              private trustStoreService: TrustStoreService, private alertService: AlertService,
              @Inject(MD_DIALOG_DATA) public data: any) {
  }

  public checkFile() {
    this.enableSubmit = this.fileInput.nativeElement.files.length != 0;
  }

  public submit() {
    let fi = this.fileInput.nativeElement;
    this.trustStoreService.saveTrustStore(fi.files[0], this.password).subscribe(res => {
        this.alertService.success(res.text(), false);
        this.onTruststoreUploaded.emit();
      },
      err => {
        if(!err.ok && err.statusText.length == 0) {
          this.alertService.error("Error updating truststore file (" + fi.files[0].name + ")", false);
        } else {
          this.alertService.error(err.text() + " (" + fi.files[0].name + ")", false);
        }
      }
    );
    this.dialogRef.close();
  }
}
