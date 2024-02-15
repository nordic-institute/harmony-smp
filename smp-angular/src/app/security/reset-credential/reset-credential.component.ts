import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormGroup, UntypedFormControl, Validators} from "@angular/forms";
import {GlobalLookups} from "../../common/global-lookups";
import {equal} from "../../common/dialogs/password-change-dialog/password-change-dialog.component";
import {SecurityService} from "../security.service";

@Component({
  templateUrl: './reset-credential.component.html',
  styleUrls: ['./reset-credential.component.css']
})
export class ResetCredentialComponent implements OnInit {

  resetForm: FormGroup;
  resetToken: string

  hideNewPwdFiled: boolean = true;
  hideConfPwdFiled: boolean = true;

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private lookups: GlobalLookups,
              private securityService: SecurityService) {
  }


  ngOnInit() {
    this.initForm();
    this.resetToken = this.activatedRoute.snapshot.params['resetToken'];
  }

  private initForm() {

    let newPasswdFormControl: UntypedFormControl = new UntypedFormControl({value: null, readonly: false},
      [Validators.required, Validators.pattern(this.passwordValidationRegExp)]);
    let confirmNewPasswdFormControl: UntypedFormControl = new UntypedFormControl({value: null, readonly: false},
      [Validators.required, equal(newPasswdFormControl, true)]);

    this.resetForm = new FormGroup({
      'resetUsername': new UntypedFormControl({value: null, readonly: true}, null),
      'new-password': newPasswdFormControl,
      'confirm-new-password': confirmNewPasswdFormControl
    });

    this.resetForm.controls['resetUsername'].setValue("");
    this.resetForm.controls['new-password'].setValue("");
    this.resetForm.controls['confirm-new-password'].setValue("");
  }

  public passwordError = (controlName: string, errorName: string) => {
    return this.resetForm.controls[controlName].hasError(errorName);
  }

  get passwordValidationMessage() {
    return this.lookups.cachedApplicationInfo?.passwordValidationRegExpMessage;
  }


  get passwordValidationRegExp() {
    return this.lookups.cachedApplicationInfo?.passwordValidationRegExp;
  }

  public resetPassword() {
    this.securityService.credentialReset(this.resetForm.controls['resetUsername'].value,
      this.resetToken,
      this.resetForm.controls['new-password'].value,);
  }

  public cancel() {
    this.router.navigate(['/search']);
  }
}
