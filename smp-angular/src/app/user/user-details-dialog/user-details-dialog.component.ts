import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatSlideToggleChange} from '@angular/material';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {Role} from '../../security/role.model';
import {UserRo} from '../user-ro.model';
import {SearchTableEntityStatus} from '../../common/search-table/search-table-entity-status.model';
import {AlertService} from '../../alert/alert.service';
import {CertificateService} from '../certificate.service';
import {CertificateRo} from "../certificate-ro.model";
import {DatePipe} from "../../custom-date/date.pipe";
import {GlobalLookups} from "../../common/global-lookups";
import {Observable, of} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {UserDetailsService} from "./user-details.service";

@Component({
  selector: 'user-details-dialog',
  templateUrl: './user-details-dialog.component.html',
  styleUrls: ['user-details-dialog.component.css']
})
export class UserDetailsDialogComponent {

  @ViewChild('fileInput') private fileInput;

  readonly emailPattern = '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}';
  readonly passwordPattern = '^(?=.*[A-Z])(?=.*[ !#$%&\'()*+,-./:;<=>?@\\[^_`{|}~\\\]"])(?=.*[0-9])(?=.*[a-z]).{8,32}$';
  readonly dateFormat: string = 'yyyy-MM-dd HH:mm:ssZ';
  readonly usernamePattern = '^[a-zA-Z0-9]{4,32}$';

  mode: UserDetailsDialogMode;
  editMode: boolean;
  userId: number;
  userRoles = [];
  certificateValidationMessage: string = null;
  isCertificateInvalid: boolean = true;
  existingRoles = [];
  userForm: FormGroup;
  current: UserRo;
  tempStoreForCertificate: CertificateRo = this.newCertificateRo();
  tempStoreForUser: UserRo = this.newUserRo();

  private passwordConfirmationValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const userToggle = control.get('userToggle');
    const password = control.get('password');
    const confirmation = control.get('confirmation');
    return userToggle && password && confirmation && userToggle.value && password.value !== confirmation.value ? {confirmationMatch: true} : null;
  };

  private atLeastOneToggleCheckedValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const userToggle = control.get('userToggle');
    const certificateToggle = control.get('certificateToggle');
    return userToggle && certificateToggle && !userToggle.value && !certificateToggle.value ? {userDetailsOrCertificateRequired: true} : null;
  };

  private certificateValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const certificateToggle = control.get('certificateToggle');
    const subject = control.get('subject');
    const validFrom = control.get('validFrom');
    const validTo = control.get('validTo');
    const issuer = control.get('issuer');
    const serialNumber = control.get('serialNumber');
    const isValid = control.get('isCertificateValid');
    return certificateToggle && subject && validFrom && validTo && issuer && serialNumber
    && certificateToggle.value
    && isValid
    && !(subject.value && validFrom.value && validTo.value && issuer.value && serialNumber.value) ? {certificateDetailsRequired: true} : null;
  };

  private certificateExistValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const certificateToggle = control.get('certificateToggle');
    const certificateId = control.get('certificateId');
    // get all persisted
    const listIds = this.lookups.cachedServiceGroupOwnerList.map(a => a.certificate ? a.certificate.certificateId : "NoId");

    return certificateToggle && certificateId && certificateId.value
    && listIds.includes(certificateId.value) && this.current.certificate && certificateId.value !== this.current.certificate.certificateId ? {certificateIdExists: true} : null;
  };

  private asyncPasswordValidator: AsyncValidatorFn = (control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
    if (this.isPreferencesMode()) {
      const userToggle = control.get('userToggle');
      const passwordToggle = control.get('passwordToggle');
      const password = control.get('password');
      const confirmation = control.get('confirmation');

      if (userToggle && passwordToggle && password
        && this.userId && userToggle.value && passwordToggle.value && password.value) {
        return this.userDetailsService.isSamePreviousPasswordUsed$(this.userId, password.value).pipe(
          map(previousPasswordUsed => previousPasswordUsed ? {previousPasswordUsed: true} : null),
          catchError(() => {
            this.alertService.error("Error occurred while validating the password against the previously chosen one!");
            return of(null);
          }));
      }
    }
    return of(null);
  };

  notInList(list: string[]) {
    return (c: AbstractControl): { [key: string]: any } => {
      if (c.value && list.includes(c.value.toString().toLowerCase())) {
        return {'notInList': {valid: false}};
      }
      return null;
    }
  }

  constructor(private dialogRef: MatDialogRef<UserDetailsDialogComponent>,
              private lookups: GlobalLookups,
              private certificateService: CertificateService,
              private userDetailsService: UserDetailsService,
              private alertService: AlertService,
              private datePipe: DatePipe,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    this.mode = data.mode;
    this.userId = data.row && data.row.id;
    this.editMode = this.mode !== UserDetailsDialogMode.NEW_MODE;

    this.current = this.editMode
      ? {
        ...data.row,
        password: '', // ensures the user password is cleared before editing
        confirmation: '',
        certificate: data.row.certificate || this.newCertificateRo()
      } : {
        active: true,
        username: '',
        emailAddress: '',
        password: '',
        confirmation: '',
        role: '',
        status: SearchTableEntityStatus.NEW,
        statusPassword: SearchTableEntityStatus.NEW,
        certificate: this.newCertificateRo(),

      };

    // The password authentication is if username exists
    // if it's off on clear then clear the username!
    const bUserPasswordAuthentication: boolean = !!this.current.username;
    const bSetPassword: boolean = false;

    // calculate allowed roles
    this.existingRoles = this.getAllowedRoles(this.current.role);

    // set empty form ! do not bind it to current object !
    this.userForm = fb.group({
      // common values
      'active': new FormControl({value: ''}, []),
      'emailAddress': new FormControl({value: ''}, [Validators.pattern(this.emailPattern), Validators.maxLength(255)]),
      'role': new FormControl({
        value: '',
        disabled: this.mode === UserDetailsDialogMode.PREFERENCES_MODE
      }, Validators.required),
      // username/password authentication
      'userToggle': new FormControl(bUserPasswordAuthentication),
      'passwordToggle': new FormControl({value: bSetPassword, disabled: !bUserPasswordAuthentication}),
      'username': new FormControl({value: '', disabled: this.editMode || !bUserPasswordAuthentication},
        !this.editMode || !this.current.username
          ? [Validators.nullValidator, Validators.pattern(this.usernamePattern), this.notInList(this.lookups.cachedServiceGroupOwnerList.map(a => a.username ? a.username.toLowerCase() : null))]
          : null),
      // improve notInList validator
      'password': new FormControl({value: '', disabled: !bUserPasswordAuthentication || !bSetPassword},
        [Validators.required, Validators.pattern(this.passwordPattern)]),
      'confirmation': new FormControl({value: '', disabled: !bUserPasswordAuthentication || !bSetPassword},
        Validators.pattern(this.passwordPattern)),
      // certificate authentication
      'certificateToggle': new FormControl(this.current && this.current.certificate && !!this.current.certificate.certificateId),
      'subject': new FormControl({value: '', disabled: true}, Validators.required),
      'validFrom': new FormControl({value: '', disabled: true}, Validators.required),
      'validTo': new FormControl({value: '', disabled: true}, Validators.required),
      'issuer': new FormControl({value: '', disabled: true}, Validators.required),
      'serialNumber': new FormControl({value: '', disabled: true}, Validators.required),
      'certificateId': new FormControl({value: '', disabled: true,}, [Validators.required]),
      'isCertificateValid': new FormControl({value: 'true', disabled: true,}, [Validators.requiredTrue]
      ),
    }, {
      validator: [this.passwordConfirmationValidator,
        this.atLeastOneToggleCheckedValidator,
        this.certificateValidator,
        this.certificateExistValidator,
      ],
      asyncValidator: this.asyncPasswordValidator,
    });
    // bind values to form! not property
    this.userForm.controls['active'].setValue(this.current.active);
    this.userForm.controls['emailAddress'].setValue(this.current.emailAddress);
    this.userForm.controls['role'].setValue(this.current.role);
    // username/password authentication
    this.userForm.controls['username'].setValue(this.current.username);
    this.userForm.controls['password'].setValue(this.current.password);
    // certificate authentication
    this.userForm.controls['subject'].setValue(this.current.certificate.subject);
    this.userForm.controls['validFrom'].setValue(this.current.certificate.validFrom);
    this.userForm.controls['validTo'].setValue(this.current.certificate.validTo);
    this.userForm.controls['issuer'].setValue(this.current.certificate.issuer);
    this.userForm.controls['serialNumber'].setValue(this.current.certificate.serialNumber);
    this.userForm.controls['certificateId'].setValue(this.current.certificate.certificateId);
    this.userForm.controls['isCertificateValid'].setValue(!this.current.certificate.invalid);

    // if edit mode and user is given - toggle is disabled
    // username should not be changed.!
    if (this.editMode && bUserPasswordAuthentication) {
      this.userForm.controls['userToggle'].disable();
    }
  }

  submitForm() {
    this.dialogRef.close(true);
  }

  uploadCertificate(event) {
    const file = event.target.files[0];
    this.certificateService.uploadCertificate$(file).subscribe((res: CertificateRo) => {
        if (res && res.certificateId) {
          this.userForm.patchValue({
            'subject': res.subject,
            'validFrom': res.validFrom,
            'validTo': res.validTo,
            'issuer': res.issuer,
            'serialNumber': res.serialNumber,
            'certificateId': res.certificateId
          });
          this.certificateValidationMessage = res.invalidReason;
          this.isCertificateInvalid = res.invalid;

        } else {
          this.alertService.exception("Error occurred while reading certificate.", "Check if uploaded file has valid certificate type.", false);
        }
      },
      err => {
        this.alertService.exception('Error uploading certificate file ' + file.name, err);
      }
    );

  }

  onCertificateToggleChanged({checked}: MatSlideToggleChange) {
    if (checked) {
      // fill from temp
      this.userForm.controls['certificateId'].setValue(this.tempStoreForCertificate.certificateId);
      this.userForm.controls['subject'].setValue(this.tempStoreForCertificate.subject);
      this.userForm.controls['issuer'].setValue(this.tempStoreForCertificate.issuer);
      this.userForm.controls['serialNumber'].setValue(this.tempStoreForCertificate.serialNumber);
      this.userForm.controls['validFrom'].setValue(this.tempStoreForCertificate.validFrom);
      this.userForm.controls['validFrom'].setValue(this.tempStoreForCertificate.validFrom);
      this.userForm.controls['validTo'].setValue(this.tempStoreForCertificate.validTo);

      this.certificateValidationMessage = this.tempStoreForCertificate.invalidReason;
      this.isCertificateInvalid= this.tempStoreForCertificate.invalid;

    } else {
      // store data to temp, set values to null
      this.tempStoreForCertificate.certificateId = this.userForm.controls['certificateId'].value;
      this.tempStoreForCertificate.subject = this.userForm.controls['subject'].value;
      this.tempStoreForCertificate.issuer = this.userForm.controls['issuer'].value;
      this.tempStoreForCertificate.serialNumber = this.userForm.controls['serialNumber'].value;
      this.tempStoreForCertificate.validFrom = this.userForm.controls['validFrom'].value;
      this.tempStoreForCertificate.validTo = this.userForm.controls['validTo'].value;
      this.tempStoreForCertificate.invalidReason = this.certificateValidationMessage;
      this.tempStoreForCertificate.invalid = this.isCertificateInvalid;

      this.userForm.controls['certificateId'].setValue("");
      this.userForm.controls['subject'].setValue("");
      this.userForm.controls['issuer'].setValue("");
      this.userForm.controls['serialNumber'].setValue("");
      this.userForm.controls['validFrom'].setValue("");
      this.userForm.controls['validTo'].setValue("");
      this.userForm.controls['isCertificateValid'].setValue("true");

      this.certificateValidationMessage = null;
      this.isCertificateInvalid= false;
    }
  }

  onUserToggleChanged({checked}: MatSlideToggleChange) {
    const action = checked ? 'enable' : 'disable';
    this.userForm.get('username')[action]();
    this.userForm.get('password')[action]();
    this.userForm.get('confirmation')[action]();

    if (checked) {
      this.userForm.controls['username'].setValue(this.tempStoreForUser.username);
      this.userForm.controls['password'].setValue(this.tempStoreForUser.password);
    } else {
      // store data to temp, set values to null
      this.tempStoreForUser.username = this.userForm.controls['username'].value;
      this.tempStoreForUser.password = this.userForm.controls['password'].value;

      this.userForm.controls['username'].setValue("");
      this.userForm.controls['password'].setValue("");
    }
    this.userForm.controls['passwordToggle'].setValue(checked || !this.editMode);
  }

  onPasswordToggleChanged({checked}: MatSlideToggleChange) {
    const action = checked ? 'enable' : 'disable';
    this.userForm.get('password')[action]();
    this.userForm.get('confirmation')[action]();
    if (!checked) {
      this.userForm.get('password').setValue('');
      this.userForm.get('confirmation').setValue('');
    }
  }

  isPreferencesMode() {
    return this.mode === UserDetailsDialogMode.PREFERENCES_MODE;
  }

  public getCurrent(): UserRo {
    this.current.active = this.userForm.get('active').value;
    this.current.emailAddress = this.userForm.get('emailAddress').value;
    this.current.role = this.userForm.get('role').value;
    // certificate data
    if (this.userForm.get('certificateToggle')) {
      this.current.certificate.certificateId = this.userForm.controls['certificateId'].value;
      this.current.certificate.subject = this.userForm.controls['subject'].value;
      this.current.certificate.issuer = this.userForm.controls['issuer'].value;
      this.current.certificate.serialNumber = this.userForm.controls['serialNumber'].value;
      this.current.certificate.validFrom = this.userForm.controls['validFrom'].value;
      this.current.certificate.validTo = this.userForm.controls['validTo'].value;
    } else {
      this.current.certificate = null;
    }
    // set username and password for new
    if (this.userForm.get('userToggle')) {
      if (!this.editMode || !this.current.username) {
        this.current.username = this.userForm.controls['username'].value;
        this.current.password = this.userForm.controls['password'].value;
      }
      // if edit mode and password on  - set password
      else if (this.editMode && this.userForm.get('passwordToggle')) {
        this.current.password = this.userForm.controls['password'].value;
      }
    } else {
      this.current.username = '';
      this.current.password = '';
    }

    // update data
    return this.current;
  }

  // filters out roles so that the user cannot change from system administrator to the other roles or vice-versa
  private getAllowedRoles(userRole) {
    if (!this.editMode) {
      return Object.keys(Role);
    } else if (userRole === Role.SYSTEM_ADMIN) {
      return [Role.SYSTEM_ADMIN];
    } else {
      return Object.keys(Role).filter(role => role !== Role.SYSTEM_ADMIN);
    }
  }

  private newCertificateRo(): CertificateRo {
    return {
      subject: '',
      validFrom: null,
      validTo: null,
      issuer: '',
      serialNumber: '',
      certificateId: '',
      fingerprints: '',
    }
  }

  private newUserRo(): UserRo {
    return {
      id: null,
      index: null,
      username: '',
      emailAddress: '',
      role: '',
      active: true,
      status: SearchTableEntityStatus.NEW,
      statusPassword: SearchTableEntityStatus.NEW
    }
  }
}

export enum UserDetailsDialogMode {
  NEW_MODE = 'New User',
  EDIT_MODE = 'User Edit',
  PREFERENCES_MODE = 'Edit',
}
