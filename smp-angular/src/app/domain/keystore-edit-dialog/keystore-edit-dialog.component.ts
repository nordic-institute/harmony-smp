import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material';
import {FormBuilder} from "@angular/forms";
import {AlertService} from "../../alert/alert.service";
import {GlobalLookups} from "../../common/global-lookups";
import {HttpClient} from "@angular/common/http";
import {SecurityService} from "../../security/security.service";
import {CertificateDialogComponent} from "../../common/certificate-dialog/certificate-dialog.component";
import {ConfirmationDialogComponent} from "../../common/confirmation-dialog/confirmation-dialog.component";
import {KeystoreImportDialogComponent} from "../keystore-import-dialog/keystore-import-dialog.component";
import {InformationDialogComponent} from "../../common/information-dialog/information-dialog.component";
import {KeystoreService} from "../keystore.service";
import {KeystoreResult} from "../keystore-result.model";

@Component({
  selector: 'keystore-edit-dialog',
  templateUrl: './keystore-edit-dialog.component.html'
})
export class KeystoreEditDialogComponent {
  formTitle: string;

  displayedColumns = ['alias', 'certificateId'];


  constructor(private keystoreService: KeystoreService,
              private securityService: SecurityService,
              private http: HttpClient,
              public lookups: GlobalLookups,
              public dialog: MatDialog,
              private dialogRef: MatDialogRef<KeystoreEditDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    this.formTitle = "Keystore edit dialog";
  }


  onDeleteCertificateRowActionClicked(row) {

    let listDomains = this.lookups.cachedDomainList.filter(domain => domain.smlClientKeyAlias === row.alias || domain.signatureKeyAlias === row.alias);
    let listSignatureKeys = listDomains.map(domain => domain.domainCode);

    if (listSignatureKeys.length > 0) {
      this.dialog.open(InformationDialogComponent, {
        data: {
          title: "Delete key/certificate " + row.alias + " from keystore!",
          description: "Key/certificate is in use by domains: " + listSignatureKeys + ". First replace/remove certificate from domains!"
        }
      }).afterClosed().subscribe(result => {
        if (result) {
          //
        }
      })
    } else {
      this.dialog.open(ConfirmationDialogComponent, {
        data: {
          title: "Delete key/certificate " + row.alias + " from keystore!",
          description: "Action will permanently delete key/certificate from keystore! Do you wish to continue?"
        }
      }).afterClosed().subscribe(result => {
        if (result) {
          this.deleteCertificateFromKeystore(row.alias);
        }
      })
    }
  }

  deleteCertificateFromKeystore(alias: string) {
    this.keystoreService.deleteCertificateFromKeystore$(alias).subscribe((res: KeystoreResult) => {
        if (res) {
          if (res.errorMessage) {
            this.alertService.exception("Error occurred while deleting certificate:" + alias, res.errorMessage, false);
          } else {
            this.alertService.success("Certificate " + alias + " deleted!");
            this.lookups.refreshCertificateLookup();

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

  openImportKeystoreDialog() {
    const formRef: MatDialogRef<any> = this.dialog.open(KeystoreImportDialogComponent);
    formRef.afterClosed().subscribe(result => {
      if (result) {
        // import
      }
    });
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
