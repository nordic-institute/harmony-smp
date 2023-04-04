import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {ColumnPicker} from '../../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AlertMessageService} from '../../common/alert-message/alert-message.service';
import {UserController} from './user-controller';
import {HttpClient} from '@angular/common/http';
import {SearchTableComponent} from "../../common/search-table/search-table.component";
import {SecurityService} from "../../security/security.service";
import {GlobalLookups} from "../../common/global-lookups";
import {TruststoreEditDialogComponent} from "./truststore-edit-dialog/truststore-edit-dialog.component";
import {EntityStatus} from "../../common/model/entity-status.model";
import {SmpConstants} from "../../smp.constants";

@Component({
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, AfterViewInit, AfterViewChecked {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>;
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>;
  @ViewChild('rowActions') rowActions: TemplateRef<any>;
  @ViewChild('searchTable') searchTable: SearchTableComponent;
  @ViewChild('certificateTemplate') certificateTemplate: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  userController: UserController;
  filter: any = {};
  baseUrl: string = SmpConstants.REST_INTERNAL_USER_MANAGE;

  constructor(private lookups: GlobalLookups,
              public securityService: SecurityService,
              protected http: HttpClient,
              protected alertService: AlertMessageService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.userController = new UserController(this.http, this.lookups, this.dialog);
  }

  initColumns() {
    this.columnPicker.allColumns = [
      {
        name: 'Username',
        prop: 'username',
        showInitially: true,
        canAutoResize: true
      },
      {
        name: 'Certificate',
        prop: 'certificate',
        showInitially: true,
        cellTemplate: this.certificateTemplate,
        canAutoResize: true
      },
      {
        name: 'Role',
        prop: 'role',
        showInitially: true,
        canAutoResize: true
      },
    ];
    this.searchTable.tableColumnInit();
  }

  ngAfterViewInit() {
    this.initColumns();
    // if system admin refresh trust certificate list!
    if (this.securityService.isCurrentUserSystemAdmin()) {
      this.lookups.refreshTrustedCertificateLookup();
    }
  }

  ngAfterViewChecked() {
    this.changeDetector.detectChanges();
  }

  certCssClass(row) {

    if (row.certificate?.invalid) {
      return 'invalidCertificate';
    } else if (!row.certificate?.subject || !row.certificate?.issuer) {
      return 'certificateWarning';
    } else if (row.status === EntityStatus.NEW) {
      return 'table-row-new';
    } else if (row.status === EntityStatus.UPDATED) {
      return 'table-row-updated';
    } else if (row.status === EntityStatus.REMOVED) {
      return 'deleted';
    } else {
      return 'table-row';
    }
  }

  getCertToolTip(certificate) {
    if (!certificate) {
      return;
    }

    if (certificate.invalid) {
      return certificate.invalidReason;
    }

    if (!certificate.subject || !certificate.issuer) {
      return 'Legacy certificate definition. Please register certificate for the user!';
    }

    return '';
  }

  details(row: any) {
    this.userController.showDetails(row);
  }

  // for dirty guard...
  isDirty(): boolean {
    return this.searchTable.isDirty();
  }

  openEditTruststoreDialog() {
    const formRef: MatDialogRef<any> = this.dialog.open(TruststoreEditDialogComponent);
    formRef.afterClosed().subscribe(result => {
    });
  }
}
