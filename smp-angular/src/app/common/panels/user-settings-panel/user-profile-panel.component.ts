import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SecurityService} from "../../../security/security.service";
import {ThemeService} from "../../theme-service/theme.service";
import {MatDialog} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import {GlobalLookups} from "../../global-lookups";
import {ApplicationRoleEnum} from "../../enums/application-role.enum";
import {UserRo} from "../../model/user-ro.model";
import {UserController} from "../../services/user-controller";
import {DateTimeService} from "../../services/date-time.service";
import DateUtils from "../../utils/date-utils";

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

  readonly applicationRoles = Object.keys(ApplicationRoleEnum).map(el => {
    return {key: el, value: ApplicationRoleEnum[el]}
  });

  userForm: FormGroup;
  userCredentialForm: FormGroup;
  _managedUserData: UserRo;

  protected currentDate: Date = new Date();
  userController: UserController;

  @Input() showDataPanelTitles: boolean = true

  @ViewChild('username', {static: false}) usernameField: ElementRef;

  constructor(
    private securityService: SecurityService,
    private themeService: ThemeService,
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private http: HttpClient,
    private lookups: GlobalLookups,
    private dateTimeService: DateTimeService) {

    this.userController = new UserController(this.http, this.lookups, this.dialog);

    // set empty form ! do not bind it to current object !
    this.userForm = this.formBuilder.group({
      // common values
      'username': new FormControl({value: '', disabled: true}),
      'role': new FormControl({value: '', disabled: true}),
      'active': new FormControl({value: '', disabled: true}),
      'emailAddress': new FormControl({
        value: '',
        disabled: false
      }, [Validators.pattern(this.emailPattern),
        Validators.maxLength(255)]),
      'fullName': new FormControl({value: '', disabled: false}),
      'smpTheme': new FormControl({value: 'default_theme', disabled: false}),
      'smpLocale': new FormControl({value: this.lookups.getCurrentLocale(), disabled: false}),

    });

    this.userCredentialForm = formBuilder.group({
      'passwordUpdatedOn': new FormControl({value: '', disabled: true}),
      'passwordExpireOn': new FormControl({value: '', disabled: true}),
      'sequentialLoginFailureCount': new FormControl({
        value: '0',
        disabled: true
      }),
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

    this.updatePwdCredential(value);

    if (!!this._managedUserData) {
      this.userForm.controls['username'].setValue(this._managedUserData.username);
      this.userForm.controls['active'].setValue(this._managedUserData.active);
      this.userForm.controls['role'].setValue(this._managedUserData.role);
      this.userForm.controls['emailAddress'].setValue(this._managedUserData.emailAddress);
      this.userForm.controls['fullName'].setValue(this._managedUserData.fullName);
      this.userForm.controls['smpTheme'].setValue(!this._managedUserData.smpTheme ? 'default_theme' : this._managedUserData.smpTheme);
      this.userForm.controls['smpLocale'].setValue(!this._managedUserData.smpLocale ? this.lookups.getCurrentLocale(): this._managedUserData.smpLocale);
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
      this.userForm.controls['smpLocale'].setValue(this.lookups.getCurrentLocale());
      this.userForm.disable();
    }
    this.userForm.markAsPristine();
  }


  private updatePwdCredential(value: UserRo) {
    // form is always disabled
    this.userCredentialForm.disable()
    if (!value) {
      this.userCredentialForm.controls['passwordUpdatedOn'].setValue(null);
      this.userCredentialForm.controls['passwordExpireOn'].setValue(null);
      this.userCredentialForm.controls['sequentialLoginFailureCount'].setValue(0);
      this.userCredentialForm.controls['lastFailedLoginAttempt'].setValue(null);
      this.userCredentialForm.controls['suspendedUtil'].setValue(null);
    } else {
      this.userCredentialForm.controls['passwordUpdatedOn'].setValue(
        this.formatDateTimeToString(value.passwordUpdatedOn));
      this.userCredentialForm.controls['passwordExpireOn'].setValue(
        this.formatDateTimeToString(value.passwordExpireOn));
      this.userCredentialForm.controls['sequentialLoginFailureCount'].setValue(
        !(value.sequentialLoginFailureCount) ? "---" : value.sequentialLoginFailureCount);
      this.userCredentialForm.controls['lastFailedLoginAttempt'].setValue(
        this.formatDateTimeToString(value.lastFailedLoginAttempt));
      this.userCredentialForm.controls['suspendedUtil'].setValue(
        this.formatDateTimeToString(value.suspendedUtil));
    }
    // mark form as pristine
    this.userCredentialForm.markAsPristine();
  }


  onSaveButtonClicked() {
    this.onSaveUserEvent.emit(this.managedUserData);
  }

  onResetButtonClicked() {
    /*if (this.isNewUser) {
      this.onDiscardNew.emit();
    }*/
    this.userForm.reset(this._managedUserData);
    if (this.isUserDataLoggedInUserData) {
      this.themeService.persistTheme(this._managedUserData.smpTheme);
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
    }
  }

  isDirty(): boolean {
    return this.userForm.dirty;
  }

  get isNewUser(): boolean {
    return !this._managedUserData?.userId;
  }

  get canChangeRole(): boolean {
    return !this.isUserDataLoggedInUserData
  }

  get isUserDataLoggedInUserData() {
    return this.securityService.getCurrentUser()?.userId == this._managedUserData?.userId
  }

  public setFocus() {
    setTimeout(() => this.usernameField.nativeElement.focus());
  }

  get selectedDateTimeFormat(): string {;
    // get currently selected locale
    let locale = this.userForm.controls['smpLocale'].value;
    locale = !locale ? this.lookups.getCurrentLocale() : locale;
    return DateUtils.getDateTimeFormatForLocal(locale, true);
  }

  private formatDateTimeToString(date:Date): string {
    return this.dateTimeService.formatDateTimeForUserLocal(date)
  }

  /**
   * Method returns a formatted example date time string for the current date
   */
  get formattedDateTimeExample(): string {
    let locale = this.userForm.controls['smpLocale'].value;
    locale = !locale ? this.lookups.getCurrentLocale() : locale;
    return this.dateTimeService.formatDateTimeForLocal(this.currentDate, locale, true);
  }
}
