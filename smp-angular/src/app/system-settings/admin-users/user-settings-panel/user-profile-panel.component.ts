import {Component, ElementRef, EventEmitter, Input, Output, ViewChild,} from '@angular/core';
import {SmpConstants} from "../../../smp.constants";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {CredentialRo} from "../../../security/credential.model";
import {UserController} from "../../user/user-controller";
import {SecurityService} from "../../../security/security.service";
import {ThemeService} from "../../../common/theme-service/theme.service";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {UserService} from "../../user/user.service";
import {MatDialog} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import {GlobalLookups} from "../../../common/global-lookups";
import {DateAdapter} from "@angular/material/core";
import {NgxMatDateAdapter} from "@angular-material-components/datetime-picker";
import {UserRo} from "../../user/user-ro.model";
import {ApplicationRoleEnum} from "../../../common/enums/application-role.enum";


@Component({
  selector: 'user-profile-panel',
  templateUrl: './user-profile-panel.component.html',
  styleUrls: ['./user-profile-panel.component.scss']
})
export class UserProfilePanelComponent {

  @Output() onSaveUserEvent: EventEmitter<UserRo> = new EventEmitter();
  @Output() onDiscardNew: EventEmitter<any> = new EventEmitter();
  @Output() onChangeUserPasswordEvent: EventEmitter<UserRo> = new EventEmitter();


  readonly emailPattern = '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}';
  readonly dateFormat: string = 'yyyy-MM-dd HH:mm:ssZ';
  readonly dateTimeFormat: string = SmpConstants.DATE_TIME_FORMAT;
  readonly nullValue: string = SmpConstants.NULL_VALUE;

  readonly applicationRoles = Object.keys(ApplicationRoleEnum).map(el => {
    return {key: el, value: ApplicationRoleEnum[el]}
  });


  userForm: FormGroup;
  userCredentialForm: FormGroup;
  _managedUserData: UserRo;

  currentDate: Date = new Date();

  currentPwdCredential: CredentialRo;
  userController: UserController;

  @Input() showDataPanelTitles: boolean = true

  @ViewChild('username', {static: false}) usernameField: ElementRef;


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
      'active': new FormControl({value: '', disabled: true}),
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
  }


  get managedUserData(): UserRo {
    let userRo = {...this._managedUserData};
    userRo.active = this.userForm.get('active').value;
    userRo.username = this.userForm.get('username').value;
    userRo.role = this.userForm.get('role').value;
    userRo.emailAddress = this.userForm.get('emailAddress').value;
    userRo.fullName = this.userForm.get('fullName').value;
    userRo.smpTheme = this.userForm.get('smpTheme').value;
    userRo.smpLocale = this.userForm.get('smpLocale').value;
    return userRo;
  }

  @Input() set managedUserData(value: UserRo) {
    this._managedUserData = value;

    if (!!this._managedUserData) {
      this.userForm.controls['username'].setValue(this._managedUserData.username);
      this.userForm.controls['active'].setValue(this._managedUserData.active);
      this.userForm.controls['role'].setValue(this._managedUserData.role);
      this.userForm.controls['emailAddress'].setValue(this._managedUserData.emailAddress);
      this.userForm.controls['fullName'].setValue(this._managedUserData.fullName);
      this.userForm.controls['smpTheme'].setValue(!this._managedUserData.smpTheme ? 'default_theme' : this._managedUserData.smpTheme);
      this.userForm.controls['smpLocale'].setValue(!this._managedUserData.smpLocale ? 'fr' : this._managedUserData.smpLocale);
      // mark form as pristine
      this.userForm.enable();
      // disable fields
      if (!this.isNewUser) {
        this.userForm.controls['username'].disable();
      } else {
        this.setFocus();
      }
      if (this.isUserDataLoggedInUserData) {
        this.userForm.controls['role'].disable();
        this.userForm.controls['active'].disable();
      }
    } else {
      this.userForm.controls['username'].setValue("");
      this.userForm.controls['role'].setValue("");
      this.userForm.controls['active'].setValue("false");
      this.userForm.controls['emailAddress'].setValue("");
      this.userForm.controls['fullName'].setValue("");
      this.userForm.controls['smpTheme'].setValue('default_theme');
      this.userForm.controls['smpLocale'].setValue('fr');
      this.userForm.disable();
    }
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
    this.onSaveUserEvent.emit(this.managedUserData);
  }

  onResetButtonClicked() {
    if (this.isNewUser) {
      this.onDiscardNew.emit();
    }
    this.userForm.reset(this._managedUserData);
    if (this.isUserDataLoggedInUserData) {
      this.themeService.persistTheme(this._managedUserData.smpTheme);
      this.dateAdapter.setLocale(this._managedUserData.smpLocale);
      this.ngxMatDateAdapter.setLocale(this._managedUserData.smpLocale);
    }
  }

  changeCurrentUserPassword() {
    this.onChangeUserPasswordEvent.emit(this._managedUserData)
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
    // save theme only for logged in user
    if (this.isUserDataLoggedInUserData) {
      this.themeService.persistTheme(target);
    }

  }

  get themeItems() {
    return this.themeService.themes;
  }

  onLocaleSelect(target: string) {
    // save locale only for logged-in user
    if (this.isUserDataLoggedInUserData) {
      this.dateAdapter.setLocale(target);
      this.ngxMatDateAdapter.setLocale(target);
    }
  }

  isDirty(): boolean {
    return this.userForm.dirty;
  }

  get isNewUser(): boolean {
    return !this._managedUserData?.userId;
  }

  get canChangeRole ():boolean {
    return !this.isUserDataLoggedInUserData
  }

  get isUserDataLoggedInUserData(){
    return this.securityService.getCurrentUser()?.userId == this._managedUserData?.userId
  }

  public setFocus() {
    setTimeout(() => this.usernameField.nativeElement.focus());
  }

}
