import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Inject,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder} from "@angular/forms";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {GlobalLookups} from "../../common/global-lookups";
import {HttpClient} from "@angular/common/http";
import {SecurityService} from "../../security/security.service";
import {TruststoreService} from "../truststore.service";
import {CertificateDialogComponent} from "../../common/dialogs/certificate-dialog/certificate-dialog.component";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {TruststoreResult} from "../truststore-result.model";
import {CertificateRo} from "../certificate-ro.model";


@Component({
  selector: 'truststore-edit-dialog',
  templateUrl: './truststore-edit-dialog.component.html',
  styleUrls: ['truststore-edit-dialog.component.css']
})
export class TruststoreEditDialogComponent implements AfterViewInit, AfterViewChecked {
  @ViewChild('certificateRowActions') certificateRowActions: TemplateRef<any>;
  @ViewChild('rowIndex') rowIndex: TemplateRef<any>;

  formTitle: string;
  trustedCertificateList: Array<any> = [];

  tableColumns = [];


  constructor(private truststoreService: TruststoreService,
              private securityService: SecurityService,
              private http: HttpClient,
              public lookups: GlobalLookups,
              public dialog: MatDialog,
              private dialogRef: MatDialogRef<TruststoreEditDialogComponent>,
              private alertService: AlertMessageService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: UntypedFormBuilder,
              private changeDetector: ChangeDetectorRef) {
    this.formTitle = "Truststore edit dialog";
    // bind to trusted certificate list events
    this.lookups.onTrustedCertificateListRefreshEvent().subscribe((data) => {
        this.refreshData();
      }
    )
  }

  ngAfterViewChecked(): void {
    // fix bug updating the columns
    //https://github.com/swimlane/ngx-datatable/issues/1266
    window.dispatchEvent(new Event('resize'));
    this.changeDetector.detectChanges();

  }

  ngAfterViewInit(): void {
    this.initColumns();
    this.refreshData();
  }

  initColumns(): void {
    this.tableColumns = [
      {
        cellTemplate: this.rowIndex,
        name: 'Index',
        width: 30,
        maxWidth: 80,
        sortable: false
      },
      {
        name: 'Alias',
        prop: 'alias',
        sortable: false,
      },
      {
        name: 'Certificate',
        prop: 'certificateId',
        sortable: false,
      },
      {
        name: 'Actions',
        sortable: false,
        cellTemplate: this.certificateRowActions,
      },
    ];
  }


  refreshData() {
    this.trustedCertificateList = [...this.lookups.cachedTrustedCertificateList];
  }

  onDeleteCertificateRowActionClicked(row) {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete certificate " + row.alias + " from truststore",
        description: "Action will permanently delete certificate from truststore! Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.deleteCertificateFromTruststore(row.alias);
      }
    });
  }

  deleteCertificateFromTruststore(alias: string) {
    this.truststoreService.deleteCertificateFromKeystore$(alias).subscribe((res: TruststoreResult) => {
        if (res) {
          if (res.errorMessage) {
            this.alertService.exception("Error occurred while deleting certificate:" + alias, res.errorMessage, false);
          } else {
            this.alertService.success("Certificate with alias [" + alias + "] is deleted!");
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
          this.alertService.success("Certificate: [" + res.certificateId + "] with alias [" + res.alias + "] is imported!");
          this.lookups.refreshTrustedCertificateLookup();
        } else {
          this.alertService.exception("Error occurred while uploading certificate.", "Check if uploaded file has valid certificate type.", false);
        }
      },
      err => {
        this.alertService.exception('Error uploading certificate file ' + file.name, err.error?.errorDescription);
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
