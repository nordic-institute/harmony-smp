import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
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
import {ErrorStateMatcher} from "@angular/material/core";
import {UserDetailsService} from "../../user/user-details-dialog/user-details.service";
import {CertificateRo} from "../../user/certificate-ro.model";
import {AlertMessageService} from "../alert-message/alert-message.service";
import {ErrorResponseRO} from "../error/error-model";
import {SecurityService} from "../../security/security.service";
import {InformationDialogComponent} from "../information-dialog/information-dialog.component";

@Component({
  selector: 'smp-password-change-dialog',
  templateUrl: './password-change-dialog.component.html',
  styleUrls: ['./password-change-dialog.component.css']
})
export class PasswordChangeDialogComponent {

  formTitle = "Change password dialog!";
  dialogForm: FormGroup;
  hideCurrPwdFiled: boolean = true;
  hideNewPwdFiled: boolean = true;
  hideConfPwdFiled: boolean = true;
  current: User;
  message: string;
  messageType: string = "alert-error";
  forceChange:boolean=false;

  constructor(
    public dialogRef: MatDialogRef<PasswordChangeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: User,
    private lookups: GlobalLookups,
    private userDetailsService: UserDetailsService,
    private alertService: AlertMessageService,
    private securityService: SecurityService,
    public dialog: MatDialog,
    private fb: FormBuilder
  ) {
    // disable close of focus lost
    dialogRef.disableClose = true;

    this.current = {...data}

    this.forceChange = this.current.forceChangeExpiredPassword;

    let currentPasswdFormControl: FormControl = new FormControl({value: null, readonly: false}, [Validators.required]);
    let newPasswdFormControl: FormControl = new FormControl({value: null, readonly: false},
      [Validators.required, Validators.pattern(this.passwordValidationRegExp), equal(currentPasswdFormControl, false)]);
    let confirmNewPasswdFormControl: FormControl = new FormControl({value: null, readonly: false},
      [Validators.required, equal(newPasswdFormControl, true)]);

    this.dialogForm = fb.group({
      'email': new FormControl({value: null, readonly: true}, null),
      'username': new FormControl({value: null, readonly: true}, null),
      'current-password': currentPasswdFormControl,
      'new-password': newPasswdFormControl,
      'confirm-new-password': confirmNewPasswdFormControl
    });

    this.dialogForm.controls['email'].setValue(this.current.emailAddress);
    this.dialogForm.controls['username'].setValue(this.current.username);
    this.dialogForm.controls['current-password'].setValue('');
    this.dialogForm.controls['new-password'].setValue('');
    this.dialogForm.controls['confirm-new-password'].setValue('');
  }

  public passwordError = (controlName: string, errorName: string) => {
    return this.dialogForm.controls[controlName].hasError(errorName);
  }

  get passwordValidationMessage() {
    return this.lookups.cachedApplicationConfig?.passwordValidationRegExpMessage;
  }

  get passwordValidationRegExp() {
    return this.lookups.cachedApplicationConfig?.passwordValidationRegExp;
  }

  changeCurrentUserPassword() {
    this.clearAlert();

    // update password
    this.userDetailsService.changePassword(this.current.userId,
      this.dialogForm.controls['new-password'].value,
      this.dialogForm.controls['current-password'].value).subscribe((res: boolean) => {
        this.showPassChangeDialog();
        close()
      },
      (err) => {
        this.showErrorMessage(err.error.errorDescription);
      }
    );
  }
  showPassChangeDialog(){
    this.dialog.open(InformationDialogComponent, {
      data: {
        title: "Password changed!",
        description: "Password has been successfully changed. Login again to the application with the new password!"
      }
    }).afterClosed().subscribe(result => {
      // no need to logout because service itself logouts
      this.securityService.finalizeLogout(result);
      close();
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

export function equal(currentPasswdFormControl: FormControl, matchEqual: boolean): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null =>
    (matchEqual ? control.value === currentPasswdFormControl.value : control.value !== currentPasswdFormControl.value)
      ? null : {error: control.value};
}
