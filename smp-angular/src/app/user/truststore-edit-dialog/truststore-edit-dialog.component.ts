import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material';
import {FormBuilder} from "@angular/forms";
import {AlertService} from "../../alert/alert.service";
import {GlobalLookups} from "../../common/global-lookups";
import {HttpClient} from "@angular/common/http";
import {SecurityService} from "../../security/security.service";
import {TruststoreService} from "../truststore.service";
import {CertificateDialogComponent} from "../../common/certificate-dialog/certificate-dialog.component";
import {ConfirmationDialogComponent} from "../../common/confirmation-dialog/confirmation-dialog.component";
import {InformationDialogComponent} from "../../common/information-dialog/information-dialog.component";
import {TruststoreResult} from "../truststore-result.model";
import {CertificateRo} from "../certificate-ro.model";


@Component({
  selector: 'truststore-edit-dialog',
  templateUrl: './truststore-edit-dialog.component.html',
  styleUrls: ['truststore-edit-dialog.component.css']
})
export class TruststoreEditDialogComponent {
  formTitle: string;

  displayedColumns = ['alias', 'certificateId'];


  constructor(private truststoreService: TruststoreService,
              private securityService: SecurityService,
              private http: HttpClient,
              public lookups: GlobalLookups,
              public dialog: MatDialog,
              private dialogRef: MatDialogRef<TruststoreEditDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    this.formTitle = "Truststore edit dialog";
  }


  onDeleteCertificateRowActionClicked(row) {

      this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: "Delete certificate " + row.alias + " from truststore!",
          description: "Action will permanently delete certificate from truststore! Do you wish to continue?"
        }
      }).afterClosed().subscribe(result => {
        if (result) {
          this.deleteCertificateFromTruststore(row.alias);
        }
      })

  }

  deleteCertificateFromTruststore(alias: string) {
    this.truststoreService.deleteCertificateFromKeystore$(alias).subscribe((res: TruststoreResult) => {
        if (res) {
          if (res.errorMessage) {
            this.alertService.exception("Error occurred while deleting certificate:" + alias, res.errorMessage, false);
          } else {
            this.alertService.success("Certificate " + alias + " deleted!");
            this.lookups.refreshTrustedCertificateLookup();

          }
        } else {
          this.alertService.exception("Error occurred while deleting certificate:" + alias, "Unknown Error", false);
        }
      },
      err => {
        this.alertService.exception('Error occurred while deleting certificate:' + alias, err);
      }
    )

  }

  uploadCertificate(event) {
    const file = event.target.files[0];
    this.truststoreService.uploadCertificate$(file).subscribe((res: CertificateRo) => {
        if (res && res.certificateId) {
          this.lookups.refreshTrustedCertificateLookup();
        } else {
          this.alertService.exception("Error occurred while uploading certificate.", "Check if uploaded file has valid certificate type.", false);
        }
      },
      err => {
        this.alertService.exception('Error uploading certificate file ' + file.name, err);
      }
    );
  }

  onActivate(event) {
    if ("dblclick" === event.type) {
      this.onShowCertificateDataRow(event.row);
    }
  }

  onShowCertificateDataRow(row) {
    const formRef: MatDialogRef<any> = this.dialog.open(CertificateDialogComponent, {
      data: {row}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        // import
      }
    });
  }


}
