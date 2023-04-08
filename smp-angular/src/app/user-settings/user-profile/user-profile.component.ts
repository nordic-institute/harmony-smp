import {Component, Input,} from '@angular/core';
import {SecurityService} from "../../security/security.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {FormBuilder, FormControl, FormGroup, Validators,} from "@angular/forms";
import {ThemeService} from "../../common/theme-service/theme.service";
import {User} from "../../security/user.model";
import {UserService} from "../../system-settings/user/user.service";
import {SmpConstants} from "../../smp.constants";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {UserController} from "../../system-settings/user/user-controller";
import {HttpClient} from "@angular/common/http";
import {GlobalLookups} from "../../common/global-lookups";
import {CredentialRo} from "../../security/credential.model";
import {DateAdapter} from "@angular/material/core";
import {NgxMatDateAdapter} from "@angular-material-components/datetime-picker";


@Component({
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent {

  readonly emailPattern = '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}';
  readonly dateFormat: string = 'yyyy-MM-dd HH:mm:ssZ';
  readonly dateTimeFormat: string = SmpConstants.DATE_TIME_FORMAT;
  readonly nullValue: string = SmpConstants.NULL_VALUE;
  userForm: FormGroup;
  userCredentialForm: FormGroup;
  currentUserData: User;

  currentDate: Date = new Date();

  currentPwdCredential: CredentialRo;
  userController: UserController;
  @Input() showActionButtons: boolean = true;

  constructor(
    private securityService: SecurityService,
    private themeService: ThemeService,
    private alertService: AlertMessageService,
    private formBuilder: FormBuilder,
    private userService: UserService,
    private dialog: MatDialog,
    private http: HttpClient,
    private lookups: GlobalLookups,
    private dateAdapter: DateAdapter<Date>,
    private ngxMatDateAdapter: NgxMatDateAdapter<Date>) {

    this.userController = new UserController(this.http, this.lookups, this.dialog);

    // set empty form ! do not bind it to current object !
    this.userForm = formBuilder.group({
      // common values
      'username': new FormControl({value: '', disabled: true}),
      'role': new FormControl({value: '', disabled: true}),
      'emailAddress': new FormControl({value: '', disabled: false}, [Validators.pattern(this.emailPattern),
        Validators.maxLength(255)]),
      'fullName': new FormControl({value: '', disabled: false}),
      'smpTheme': new FormControl({value: 'default_theme', disabled: false}),
      'smpLocale': new FormControl({value: 'fr', disabled: false}),

    });

    this.userCredentialForm = formBuilder.group({
      'passwordUpdatedOn': new FormControl({value: '', disabled: true}),
      'passwordExpireOn': new FormControl({value: '', disabled: true}),
      'sequentialLoginFailureCount': new FormControl({value: '0', disabled: true}),
      'lastFailedLoginAttempt': new FormControl({value: '', disabled: true}),
      'suspendedUtil': new FormControl({value: '', disabled: true}),
    });
    userService.onProfileDataChangedEvent().subscribe(updatedUser => {
        this.updateUserData(updatedUser);
      }
    );

    userService.onPwdCredentialsUpdateEvent().subscribe(pwdCredential => {
        this.updatePwdCredential(pwdCredential);
      }
    );

    userService.getUserPwdCredentialStatus();

    this.updateUserData(securityService.getCurrentUser())
  }

  private updateUserData(currentUser: User) {
    this.currentUserData = {
      ...currentUser
    }

    this.userForm.controls['username'].setValue(this.currentUserData.username);
    this.userForm.controls['role'].setValue(this.currentUserData.role);
    this.userForm.controls['emailAddress'].setValue(this.currentUserData.emailAddress);
    this.userForm.controls['fullName'].setValue(this.currentUserData.fullName);
    this.userForm.controls['smpTheme'].setValue(!this.currentUserData.smpTheme ? 'default_theme' : this.currentUserData.smpTheme);
    this.userForm.controls['smpLocale'].setValue(!this.currentUserData.smpLocale ? 'fr' : this.currentUserData.smpLocale);

    // set current user theme as persisted for the application
    this.themeService.persistTheme(this.currentUserData.smpTheme);
    // mark form as pristine
    this.userForm.markAsPristine();
  }

  private updatePwdCredential(currentPwdCredential: CredentialRo) {
    this.currentPwdCredential = {
      ...currentPwdCredential
    }
    this.userCredentialForm.controls['passwordUpdatedOn'].setValue(this.currentPwdCredential.updatedOn);
    this.userCredentialForm.controls['passwordExpireOn'].setValue(this.currentPwdCredential.expireOn);
    this.userCredentialForm.controls['sequentialLoginFailureCount'].setValue(this.currentPwdCredential.sequentialLoginFailureCount);
    this.userCredentialForm.controls['lastFailedLoginAttempt'].setValue(this.currentPwdCredential.lastFailedLoginAttempt);
    this.userCredentialForm.controls['suspendedUtil'].setValue(this.currentPwdCredential.suspendedUtil);
    // mark form as pristine
    this.userCredentialForm.markAsPristine();
  }


  onSaveButtonClicked() {
    let userData = {...this.currentUserData};
    userData.emailAddress = this.userForm.get('emailAddress').value;
    userData.fullName = this.userForm.get('fullName').value;
    userData.smpTheme = this.userForm.get('smpTheme').value;
    userData.smpLocale = this.userForm.get('smpLocale').value;

    this.userService.updateUser(userData);
  }

  onResetButtonClicked() {
    this.userForm.reset(this.currentUserData);
  }

  changeCurrentUserPassword() {
    const formRef: MatDialogRef<any> = this.userController.changePasswordDialog({
      data: {
        user: this.currentUserData,
        adminUser: false
      },
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        this.currentUserData.passwordExpireOn = result.passwordExpireOn;
        this.userForm.controls['passwordExpireOn'].setValue(this.currentUserData.passwordExpireOn);
      }
    });

  }


  get submitButtonEnabled(): boolean {
    return this.userForm.valid && this.userForm.dirty;
  }

  get resetButtonEnabled(): boolean {
    return this.userForm.dirty;
  }

  get safeRefresh(): boolean {
    return true;
  }


  onThemeSelect(target: string) {
    this.themeService.persistTheme(target);
  }

  get themeItems() {
    return this.themeService.themes;
  }

  onLocaleSelect(target: string) {
    console.log("set locale" + target)
    this.dateAdapter.setLocale(target);
    this.ngxMatDateAdapter.setLocale(target);
  }


}
