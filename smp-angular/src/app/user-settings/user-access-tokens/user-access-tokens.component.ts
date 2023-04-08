import {Component, QueryList, ViewChildren,} from '@angular/core';
import {SecurityService} from "../../security/security.service";
import {UserService} from "../../system-settings/user/user.service";
import {CredentialRo} from "../../security/credential.model";
import {ConfirmationDialogComponent} from "../../common/dialogs/confirmation-dialog/confirmation-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {EntityStatus} from "../../common/model/entity-status.model";
import {CredentialDialogComponent} from "../../common/dialogs/credential-dialog/credential-dialog.component";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";
import {AccessTokenPanelComponent} from "./access-token-panel/access-token-panel.component";


@Component({
  templateUrl: './user-access-tokens.component.html',
  styleUrls: ['./user-access-tokens.component.scss']
})
export class UserAccessTokensComponent implements BeforeLeaveGuard {
  accessTokens: CredentialRo[] = [];

  @ViewChildren(AccessTokenPanelComponent)
  userTokenCredentialComponents: QueryList<AccessTokenPanelComponent>;

  constructor(private securityService: SecurityService,
              private userService: UserService,
              public dialog: MatDialog) {


    this.userService.onAccessTokenCredentialsUpdateSubject().subscribe((credentials: CredentialRo[]) => {
      this.updateAccessTokenCredentials(credentials);
    });

    this.userService.onAccessTokenCredentialUpdateSubject().subscribe((credential: CredentialRo) => {
      this.updateAccessTokenCredential(credential);
    });

    this.userService.getUserAccessTokenCredentials();
  }

  public updateAccessTokenCredentials(userAccessTokens: CredentialRo[]) {
    this.accessTokens = userAccessTokens;
  }

  public updateAccessTokenCredential(userAccessToken: CredentialRo) {
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
        this.deleteAccessToken(credential);
      }
    })
  }

  public createNewAccessToken() {
    this.dialog.open(CredentialDialogComponent, {
      data: {
        credentialType: CredentialDialogComponent.ACCESS_TOKEN_TYPE,
        formTitle: "Access token generation dialog"
      }
    }).afterClosed();
  }

  public onSaveItemClicked(credential: CredentialRo) {

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

  private deleteAccessToken(credential: CredentialRo) {
    this.userService.deleteUserAccessTokenCredential(credential);
  }

  private updateAccessToken(credential: CredentialRo) {
    this.userService.updateUserAccessTokenCredential(credential);
  }


  isDirty(): boolean {
    let dirtyComp = !this.userTokenCredentialComponents ? null : this.userTokenCredentialComponents.find(cmp => cmp.isDirty())
    return !!dirtyComp;
  }
}

