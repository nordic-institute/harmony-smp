import {Component, QueryList, ViewChildren,} from '@angular/core';
import {SecurityService} from "../../security/security.service";
import {UserService} from "../../system-settings/user/user.service";
import {CredentialRo} from "../../security/credential.model";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EntityStatus} from "../../common/enums/entity-status.enum";
import {CertificateDialogComponent} from "../../common/dialogs/certificate-dialog/certificate-dialog.component";
import {CredentialDialogComponent} from "../../common/dialogs/credential-dialog/credential-dialog.component";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {UserCertificatePanelComponent} from "./user-certificate-panel/user-certificate-panel.component";
import {HttpErrorHandlerService} from "../../common/error/http-error-handler.service";


@Component({
  templateUrl: './user-certificates.component.html',
  styleUrls: ['./user-certificates.component.scss']
})
export class UserCertificatesComponent implements BeforeLeaveGuard {
  certificates: CredentialRo[] = [];


  @ViewChildren(UserCertificatePanelComponent)
  userCertificateCredentialComponents: QueryList<UserCertificatePanelComponent>;

  constructor(private securityService: SecurityService,
              private httpErrorHandlerService: HttpErrorHandlerService,
              private userService: UserService,
              public dialog: MatDialog) {


    this.userService.onCertificateCredentiasUpdateSubject().subscribe((credentials: CredentialRo[]) => {
      this.updateCredentials(credentials);
    });

    this.userService.onCertificateCredentialUpdateSubject().subscribe((credential: CredentialRo) => {
      this.updateCredential(credential);
    });
    this.userService.getUserCertificateCredentials();
  }

  public updateCredentials(certificates: CredentialRo[]) {
    this.certificates = certificates;
  }

  public updateCredential(certificate: CredentialRo) {
    // remove the access token
    if (certificate.status == EntityStatus.REMOVED) {
      this.certificates = this.certificates.filter(item => item.credentialId !== certificate.credentialId)
    }
    if (certificate.status == EntityStatus.UPDATED) {
      // update value in the array
      let itemIndex = this.certificates.findIndex(item => item.credentialId == certificate.credentialId);
      this.certificates[itemIndex] = certificate;
    }
    if (certificate.status == EntityStatus.NEW) {
      // update value in the array

      this.certificates = [
        ...this.certificates,
        certificate];
    }
  }

  public trackListItem(index: number, credential: CredentialRo) {
    return credential.credentialId;
  }

  public onDeleteItemClicked(credential: CredentialRo) {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Delete Access token",
        description: "Action will delete access token: " + credential.name + "!<br /><br />Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.delete(credential);
      }
    })
  }

  public createNew() {
    this.dialog.open(CredentialDialogComponent, {
      data: {
        credentialType: CredentialDialogComponent.CERTIFICATE_TYPE,
        formTitle: "Import certificate dialog"
      }
    }).afterClosed();

  }

  public onShowItemClicked(credential: CredentialRo) {
    this.userService.getUserCertificateCredentialObservable(credential)
      .subscribe((response: CredentialRo) => {
        this.dialog.open(CertificateDialogComponent, {
          data: {row: response.certificate}
        });

      }, error => {
        if (this.httpErrorHandlerService.logoutOnInvalidSessionError(error)) {
          return;
        }
      });
  }


  public onSaveItemClicked(credential: CredentialRo) {

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Update Certificate data",
        description: "Action will update Certificate settings: " + credential.name + " data!<br /><br />Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.update(credential);
      }
    })
  }

  private delete(credential: CredentialRo) {
    this.userService.deleteUserCertificateCredential(credential);
  }

  private update(credential: CredentialRo) {
    this.userService.updateUserCertificateCredential(credential);
  }

  isDirty(): boolean {
    let dirtyComp = !this.userCertificateCredentialComponents ? null : this.userCertificateCredentialComponents.find(cmp => cmp.isDirty())
    return !!dirtyComp;
  }
}

