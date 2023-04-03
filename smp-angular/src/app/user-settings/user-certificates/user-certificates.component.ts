import {Component,} from '@angular/core';
import {SecurityService} from "../../security/security.service";
import {UserService} from "../../user/user.service";
import {Credential} from "../../security/credential.model";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EntityStatus} from "../../common/model/entity-status.model";
import {CertificateDialogComponent} from "../../common/dialogs/certificate-dialog/certificate-dialog.component";
import {CredentialDialogComponent} from "../../common/dialogs/credential-dialog/credential-dialog.component";


@Component({
  templateUrl: './user-certificates.component.html',
  styleUrls: ['./user-certificates.component.scss']
})
export class UserCertificatesComponent {
  certificates: Credential[] = [];

  constructor(private securityService: SecurityService,
              private userService: UserService,
              public dialog: MatDialog) {


    this.userService.onCertificateCredentiasUpdateSubject().subscribe((credentials: Credential[]) => {
      this.updateCredentials(credentials);
    });

    this.userService.onCertificateCredentialUpdateSubject().subscribe((credential: Credential) => {
      this.updateCredential(credential);
    });
    this.userService.getUserCertificateCredentials();
  }

  public updateCredentials(certificates: Credential[]) {
    this.certificates = certificates;
  }

  public updateCredential(certificate: Credential) {
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

  public trackListItem(index: number, credential: Credential) {
    return credential.credentialId;
  }

  public onDeleteItemClicked(credential: Credential) {
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
    this.dialog.open(CredentialDialogComponent,{
      data:{
        credentialType: CredentialDialogComponent.CERTIFICATE_TYPE,
        formTitle: "Import certificate dialog"
      }
    } ).afterClosed();

  }
  public onShowItemClicked(credential: Credential) {
    this.userService.getUserCertificateCredentialObservable(credential).subscribe((response: Credential) => {
      this.dialog.open(CertificateDialogComponent, {
        data: {row: response.certificate}
      });

    });
  }


  public onSaveItemClicked(credential: Credential) {

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

  private delete(credential: Credential) {
    this.userService.deleteUserCertificateCredential(credential);
  }

  private update(credential: Credential) {
    this.userService.updateUserCertificateCredential(credential);
  }
}

