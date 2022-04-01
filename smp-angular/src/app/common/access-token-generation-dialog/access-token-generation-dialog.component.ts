import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective, NgForm,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {User} from "../../security/user.model";
import {GlobalLookups} from "../global-lookups";
import {UserDetailsService} from "../../user/user-details-dialog/user-details.service";
import {AccessTokenRo} from "./access-token-ro.model";
import {SearchTableEntityStatus} from "../search-table/search-table-entity-status.model";
import {SecurityService} from "../../security/security.service";

@Component({
  selector: 'smp-access-token-generation-dialog',
  templateUrl: './access-token-generation-dialog.component.html',
  styleUrls: ['./access-token-generation-dialog.component.css']
})
export class AccessTokenGenerationDialogComponent {

  formTitle = "Access token generation dialog!";
  dialogForm: FormGroup;
  hideCurrPwdFiled: boolean = true;
  hideNewPwdFiled: boolean = true;
  hideConfPwdFiled: boolean = true;
  current: User;
  message: string;
  messageType: string = "alert-error";

  constructor(
    public dialogRef: MatDialogRef<AccessTokenGenerationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: User,
    private lookups: GlobalLookups,
    private userDetailsService: UserDetailsService,
    private securityService: SecurityService,
    private fb: FormBuilder
  ) {
    this.current = {...data}


    this.dialogForm = fb.group({
      'email': new FormControl({value: null, readonly: true}, null),
      'username': new FormControl({value: null, readonly: true}, null),
      'accessTokenId': new FormControl({value: null, readonly: true}, null),
      'accessTokenExpireOn': new FormControl({value: null, readonly: true}, null),
      'current-password': new FormControl({value: null, readonly: false}, [Validators.required]),
    });

    this.dialogForm.controls['email'].setValue(this.current.emailAddress);
    this.dialogForm.controls['username'].setValue(this.current.username);
    this.dialogForm.controls['accessTokenId'].setValue(this.current.accessTokenId);
    this.dialogForm.controls['accessTokenExpireOn'].setValue(this.current.accessTokenExpireOn);
    this.dialogForm.controls['current-password'].setValue('');
  }

  public passwordError = (controlName: string, errorName: string) => {
    return this.dialogForm.controls[controlName].hasError(errorName);
  }

  regenerateAccessToken() {
    this.clearAlert();

    // update password
    this.userDetailsService.regenerateAccessToken(this.current.userId,
      this.dialogForm.controls['current-password'].value).subscribe((response: AccessTokenRo) => {
        this.showSuccessMessage("Token with id: " + response.identifier + " and value: " + response.value + " was generated!")
        this.current.accessTokenId = response.identifier;
        this.current.accessTokenExpireOn = response.expireOn;
        // set to current form
        this.dialogForm.controls['accessTokenId'].setValue(this.current.accessTokenId);
        this.dialogForm.controls['accessTokenExpireOn'].setValue(this.current.accessTokenExpireOn);
        // save new values
        const user = {...this.current, status: SearchTableEntityStatus.UPDATED};
        this.securityService.updateUserDetails(user);
      },
      (err) => {
        this.showErrorMessage(err.error.errorDescription);
      }
    );
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
}
