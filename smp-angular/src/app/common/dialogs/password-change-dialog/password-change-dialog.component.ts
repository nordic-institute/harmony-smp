import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AbstractControl, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, ValidatorFn, Validators} from "@angular/forms";
import {User} from "../../../security/user.model";
import {GlobalLookups} from "../../global-lookups";
import {UserDetailsService} from "../../../system-settings/user/user-details.service";
import {AlertMessageService} from "../../alert-message/alert-message.service";
import {SecurityService} from "../../../security/security.service";
import {InformationDialogComponent} from "../information-dialog/information-dialog.component";
import {UserRo} from "../../../system-settings/user/user-ro.model";

@Component({
  selector: 'smp-password-change-dialog',
  templateUrl: './password-change-dialog.component.html',
  styleUrls: ['./password-change-dialog.component.css']
})
export class PasswordChangeDialogComponent {

  formTitle = "Set/Change password dialog";
  dialogForm: UntypedFormGroup;
  hideCurrPwdFiled: boolean = true;
  hideNewPwdFiled: boolean = true;
  hideConfPwdFiled: boolean = true;
  current: User;
  adminUser: boolean = false;
  message: string;
  messageType: string = "alert-error";
  forceChange: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<PasswordChangeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private lookups: GlobalLookups,
    private userDetailsService: UserDetailsService,
    private alertService: AlertMessageService,
    private securityService: SecurityService,
    public dialog: MatDialog,
    private fb: UntypedFormBuilder
  ) {
    // disable close of focus lost
    dialogRef.disableClose = true;

    this.current = {...data.user}
    this.adminUser = data.adminUser

    this.forceChange = this.current.forceChangeExpiredPassword;

    let currentPasswdFormControl: UntypedFormControl = new UntypedFormControl({value: null, readonly: false},
      this.securityService.getCurrentUser().casAuthenticated && this.adminUser ? null : [Validators.required]);
    let newPasswdFormControl: UntypedFormControl = new UntypedFormControl({value: null, readonly: false},
      [Validators.required, Validators.pattern(this.passwordValidationRegExp), equal(currentPasswdFormControl, false)]);
    let confirmNewPasswdFormControl: UntypedFormControl = new UntypedFormControl({value: null, readonly: false},
      [Validators.required, equal(newPasswdFormControl, true)]);

    this.dialogForm = fb.group({
      'email': new UntypedFormControl({value: null, readonly: true}, null),
      'username': new UntypedFormControl({value: null, readonly: true}, null),
      'current-password': currentPasswdFormControl,
      'new-password': newPasswdFormControl,
      'confirm-new-password': confirmNewPasswdFormControl
    });

    this.dialogForm.controls['email'].setValue(this.isEmptyEmailAddress ? "Empty email address!" : this.current.emailAddress);
    this.dialogForm.controls['username'].setValue(this.current.username);
    this.dialogForm.controls['current-password'].setValue('');
    this.dialogForm.controls['new-password'].setValue('');
    this.dialogForm.controls['confirm-new-password'].setValue('');

    this.dialogForm.controls['new-password'].valueChanges.subscribe({
      next: (value) => {
        this.dialogForm.controls['confirm-new-password'].updateValueAndValidity();
      }
    });
  }

  get showCurrentPasswordField():boolean {
    return !this.securityService.getCurrentUser()?.casAuthenticated || !this.adminUser
  }

  public passwordError = (controlName: string, errorName: string) => {
    return this.dialogForm.controls[controlName].hasError(errorName);
  }

  get isEmptyEmailAddress() {
    return !this.current.emailAddress;
  }

  get passwordValidationMessage() {
    return this.lookups.cachedApplicationConfig?.passwordValidationRegExpMessage;
  }

  get passwordValidationRegExp() {
    return this.lookups.cachedApplicationConfig?.passwordValidationRegExp;
  }

  get getPasswordTitle(): string {
    return this.adminUser ? "Admin password for user [" + this.securityService.getCurrentUser().username + "]" : "Current password";
  }

  changeCurrentUserPassword() {
    this.clearAlert();
    if (this.adminUser) {
      // update password
      this.userDetailsService.changePasswordAdmin(
        this.securityService.getCurrentUser().userId,
        this.current.userId,
        this.dialogForm.controls['new-password'].value,
        this.dialogForm.controls['current-password'].value).subscribe((result: UserRo) => {
          this.showPassChangeDialog();
          this.current.passwordExpireOn = result.passwordExpireOn;
          this.dialogRef.close(result)
        },
        (err) => {
          this.showErrorMessage(err.error.errorDescription);
        }
      );
    } else {
      // update password
      this.userDetailsService.changePassword(this.current.userId,
        this.dialogForm.controls['new-password'].value,
        this.dialogForm.controls['current-password'].value).subscribe((res: boolean) => {
          this.showPassChangeDialog();
        },
        (err) => {
          this.showErrorMessage(err.error.errorDescription);
        }
      );
    }
  }

  showPassChangeDialog() {
    this.dialog.open(InformationDialogComponent, {
      data: {
        title: "Password set/changed",
        description: "Password has been successfully set/changed." +
          (!this.adminUser ? " Login again to the application with the new password!" : "")
      }
    }).afterClosed().subscribe(result => {
      if (!this.adminUser) {
        // logout if changed for itself
        this.securityService.finalizeLogout(result);
      }
    })
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

export function equal(currentPasswdFormControl: UntypedFormControl, matchEqual: boolean): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null =>
    (matchEqual ? control.value === currentPasswdFormControl.value : control.value !== currentPasswdFormControl.value)
      ? null : {error: control.value};
}
