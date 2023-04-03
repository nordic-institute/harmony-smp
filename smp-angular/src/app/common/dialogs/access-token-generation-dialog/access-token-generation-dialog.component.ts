import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from "@angular/forms";
import {User} from "../../../security/user.model";
import {GlobalLookups} from "../../global-lookups";
import {UserDetailsService} from "../../../user/user-details-dialog/user-details.service";
import {AccessTokenRo} from "./access-token-ro.model";
import {SecurityService} from "../../../security/security.service";
import {SmpConstants} from "../../../smp.constants";
import {EntityStatus} from "../../model/entity-status.model";

@Component({
  selector: 'smp-access-token-generation-dialog',
  templateUrl: './access-token-generation-dialog.component.html',
  styleUrls: ['./access-token-generation-dialog.component.css']
})
export class AccessTokenGenerationDialogComponent {

  dateTimeFormat: string = SmpConstants.DATE_TIME_FORMAT;
  formTitle = "Access token generation dialog";
  dialogForm: UntypedFormGroup;
  hideCurrPwdFiled: boolean = true;
  hideNewPwdFiled: boolean = true;
  hideConfPwdFiled: boolean = true;
  tokenChanged: boolean = false;
  adminUser: boolean = false;
  current: User;
  message: string;
  messageType: string = "alert-error";


  constructor(
    public dialogRef: MatDialogRef<AccessTokenGenerationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private lookups: GlobalLookups,
    private userDetailsService: UserDetailsService,
    public securityService: SecurityService,
    private fb: UntypedFormBuilder
  ) {
    dialogRef.disableClose = true;//disable default close operation

    this.current = {...data.user}
    this.adminUser = data.adminUser


    this.dialogForm = fb.group({
      'email': new UntypedFormControl({value: null, readonly: true}, null),
      'username': new UntypedFormControl({value: null, readonly: true}, null),
      'accessTokenId': new UntypedFormControl({value: null, readonly: true}, null),
      'accessTokenExpireOn': new UntypedFormControl({value: null, readonly: true}, null),
      'current-password': new UntypedFormControl({value: null, readonly: false}, this.securityService.getCurrentUser().casAuthenticated?null:[Validators.required]),
    });

    this.dialogForm.controls['email'].setValue(this.isEmptyEmailAddress ? "Empty email address!" : this.current.emailAddress);
    this.dialogForm.controls['username'].setValue(this.current.username);
    this.dialogForm.controls['accessTokenId'].setValue(this.current.accessTokenId);
    this.dialogForm.controls['accessTokenExpireOn'].setValue(this.current.accessTokenExpireOn);
    this.dialogForm.controls['current-password'].setValue('');
    this.tokenChanged = false;
  }

  public passwordError = (controlName: string, errorName: string) => {
    return this.dialogForm.controls[controlName].hasError(errorName);
  }

  get isEmptyEmailAddress() {
    return !this.current.emailAddress;
  }

  get getPasswordTitle(): string{
    return this.adminUser?"Admin password for user ["+this.securityService.getCurrentUser().username+"]":"Current password";
  }

  regenerateAccessToken() {
    this.clearAlert();

    if (this.adminUser) {
// update password
      this.userDetailsService.regenerateAccessTokenAdmin(this.securityService.getCurrentUser().userId,
        this.dialogForm.controls['current-password'].value,
      this.current.userId
      ).subscribe((response: AccessTokenRo) => {
          this.showSuccessMessage("Token with id: " + response.identifier + " and value: " + response.value + " was generated!")
          this.current.accessTokenId = response.identifier;
          this.current.accessTokenExpireOn = response.expireOn;
          // set to current form
          this.dialogForm.controls['accessTokenId'].setValue(this.current.accessTokenId);
          this.dialogForm.controls['accessTokenExpireOn'].setValue(this.current.accessTokenExpireOn);
          this.tokenChanged = true;
        },
        (err) => {
          this.showErrorMessage(err.error.errorDescription);
        }
      );
    } else {
      // update access token for currently logged-in user
      this.userDetailsService.regenerateAccessToken(this.current.userId,
        this.dialogForm.controls['current-password'].value).subscribe((response: AccessTokenRo) => {
          this.showSuccessMessage("Token with id: " + response.identifier + " and value: " + response.value + " was generated!")
          this.current.accessTokenId = response.identifier;
          this.current.accessTokenExpireOn = response.expireOn;
          // set to current form
          this.dialogForm.controls['accessTokenId'].setValue(this.current.accessTokenId);
          this.dialogForm.controls['accessTokenExpireOn'].setValue(this.current.accessTokenExpireOn);
          // save new values
          const user = {...this.current, status: EntityStatus.UPDATED};
          //this.securityService.updateUserDetails(user);
          this.tokenChanged = true;
        },
        (err) => {
          this.showErrorMessage(err.error.errorDescription);
        }
      );
    }
  }

  showSuccessMessage(value: string) {
    this.message = value;
    this.messageType = "success";
  }

  showErrorMessage(value: string) {
    this.message = value;
    this.messageType = "error";
  }

  clearAlert() {
    this.message = null;
    this.messageType = null;
  }

  public getCurrent() {
    if (this.tokenChanged) {
      return this.current;
    }
    return null;
  }

  closeDialog() {
    this.dialogRef.close(this.getCurrent())
  }
}
