import {Component,} from '@angular/core';
import {SecurityService} from "../../security/security.service";
import {UserService} from "../../system-settings/user/user.service";
import {Credential} from "../../security/credential.model";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EntityStatus} from "../../common/model/entity-status.model";
import {CredentialDialogComponent} from "../../common/dialogs/credential-dialog/credential-dialog.component";


@Component({
  templateUrl: './user-access-tokens.component.html',
  styleUrls: ['./user-access-tokens.component.scss']
})
export class UserAccessTokensComponent {
  accessTokens: Credential[] = [];

  constructor(private securityService: SecurityService,
              private userService: UserService,
              public dialog: MatDialog) {


    this.userService.onAccessTokenCredentialsUpdateSubject().subscribe((credentials: Credential[]) => {
      this.updateAccessTokenCredentials(credentials);
    });

    this.userService.onAccessTokenCredentialUpdateSubject().subscribe((credential: Credential) => {
      this.updateAccessTokenCredential(credential);
    });

    this.userService.getUserAccessTokenCredentials();
  }

  public updateAccessTokenCredentials(userAccessTokens: Credential[]) {
    this.accessTokens = userAccessTokens;
  }

  public updateAccessTokenCredential(userAccessToken: Credential) {
    // remove the access token
    if (userAccessToken.status == EntityStatus.REMOVED) {
      this.accessTokens = this.accessTokens.filter(item => item.credentialId !== userAccessToken.credentialId)
    }
    if (userAccessToken.status == EntityStatus.UPDATED) {
      // update value in the array
      let itemIndex = this.accessTokens.findIndex(item => item.credentialId == userAccessToken.credentialId);
      this.accessTokens[itemIndex] = userAccessToken;
    }
    if (userAccessToken.status == EntityStatus.NEW) {
      // update value in the array

      this.accessTokens = [
        ...this.accessTokens,
        userAccessToken];
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
        this.deleteAccessToken(credential);
      }
    })
  }

  public createNewAccessToken() {
    this.dialog.open(CredentialDialogComponent, {
      data:{
        credentialType: CredentialDialogComponent.ACCESS_TOKEN_TYPE,
        formTitle: "Access token generation dialog"
      }
    } ).afterClosed();
  }

  public onSaveItemClicked(credential: Credential) {

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Update Access token",
        description: "Action will update access token: " + credential.name + " data!<br /><br />Do you wish to continue?"
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.updateAccessToken(credential);
      }
    })
  }

  private deleteAccessToken(credential: Credential) {
    this.userService.deleteUserAccessTokenCredential(credential);
  }

  private updateAccessToken(credential: Credential) {
    this.userService.updateUserAccessTokenCredential(credential);
  }


}

