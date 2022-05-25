import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';

import {
  AbstractControl,
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
import {AlertMessageService} from '../../common/alert-message/alert-message.service';
import {CertificateService} from '../certificate.service';
import {CertificateRo} from "../certificate-ro.model";
import {DatePipe} from "../../custom-date/date.pipe";
import {GlobalLookups} from "../../common/global-lookups";
import {UserDetailsService} from "./user-details.service";
import {MatSlideToggleChange} from "@angular/material/slide-toggle";
import {SecurityService} from "../../security/security.service";
import {UserController} from "../user-controller";
import {HttpClient} from "@angular/common/http";
import {CertificateDialogComponent} from "../../common/dialogs/certificate-dialog/certificate-dialog.component";
import {SmpConstants} from "../../smp.constants";

@Component({
  selector: 'user-details-dialog',
  templateUrl: './user-details-dialog.component.html',
  styleUrls: ['user-details-dialog.component.css'],
})
export class UserDetailsDialogComponent {

  @ViewChild('fileInput') private fileInput;

  readonly emailPattern = '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}';
  readonly dateFormat: string = 'yyyy-MM-dd HH:mm:ssZ';
  readonly usernamePattern = '^[a-zA-Z0-9]{4,32}$';
  readonly dateTimeFormat: string = SmpConstants.DATE_TIME_FORMAT;

  mode: UserDetailsDialogMode;
  editMode: boolean;
  userId: string;
  userRoles = [];
  certificateValidationMessage: string = null;
  isCertificateInvalid: boolean = true;
  existingRoles = [];
  userForm: FormGroup;
  current: UserRo;
  tempStoreForCertificate: CertificateRo = this.newCertificateRo();
  tempStoreForUser: UserRo = this.newUserRo();
  newCertFile: File = null;
  userController: UserController;


  private certificateValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const certificateId = control.get('certificateId');
    const subject = control.get('subject');
    const validFrom = control.get('validFrom');
    const validTo = control.get('validTo');
    const issuer = control.get('issuer');
    const serialNumber = control.get('serialNumber');
    return certificateId && subject && validFrom && validTo && issuer && serialNumber
    && !!certificateId.value
    && !(subject.value && validFrom.value && validTo.value && issuer.value && serialNumber.value) ? {certificateDetailsRequired: true} : null;
  };

  private certificateExistValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const certificateId = control.get('certificateId');
    // get all persisted
    const listIds = this.lookups.cachedServiceGroupOwnerList.map(a => a.certificate ? a.certificate.certificateId : "NoId");

    return  certificateId && certificateId.value
    && listIds.includes(certificateId.value) && this.current.certificate && certificateId.value !== this.current.certificate.certificateId ? {certificateIdExists: true} : null;
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
              private dialog: MatDialog,
              private http: HttpClient,
              private lookups: GlobalLookups,
              private certificateService: CertificateService,
              private userDetailsService: UserDetailsService,
              private alertService: AlertMessageService,
              private securityService: SecurityService,
              private datePipe: DatePipe,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {

    this.userController = new UserController(this.http, this.lookups, this.dialog);

    this.mode = data.mode;
    this.userId = data.row && data.row.userId;
    this.editMode = this.mode !== UserDetailsDialogMode.NEW_MODE;

    this.current = this.editMode
      ? {
        ...data.row,
        password: '', // ensures the user password is cleared before editing
        confirmation: null,
        certificate: data.row.certificate ? {...data.row.certificate} : this.newCertificateRo()
      } : {
        active: true,
        username: '',
        emailAddress: '',
        password: '',
        confirmation: null,
        role: '',
        encodedValue: '',
        crlUrl: '',
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


      'username': new FormControl({value: '', disabled: this.editMode || !bUserPasswordAuthentication},
        !this.editMode || !this.current.username
          ? [Validators.nullValidator, Validators.pattern(this.usernamePattern), this.notInList(this.lookups.cachedServiceGroupOwnerList.map(a => a.username ? a.username.toLowerCase() : null))]
          : null),
      'passwordExpireOn': new FormControl({value: '', disabled: true}),
      'accessTokenId': new FormControl({value: '', disabled: true}),

      'accessTokenExpireOn': new FormControl({value: '', disabled: true}),
      'casUserDataUrl': new FormControl({value: '', disabled: true}),


      'confirmation': new FormControl({value: '', disabled: !bUserPasswordAuthentication || !bSetPassword}),
      // certificate authentication
      'subject': new FormControl({value: '', disabled: true}, Validators.required),
      'validFrom': new FormControl({value: '', disabled: true}, Validators.required),
      'validTo': new FormControl({value: '', disabled: true}, Validators.required),
      'issuer': new FormControl({value: '', disabled: true}, Validators.required),
      'serialNumber': new FormControl({value: '', disabled: true}, Validators.required),
      'crlUrl': new FormControl({value: '', disabled: true}),
      'encodedValue': new FormControl({value: '', disabled: true}),
      'certificateId': new FormControl({value: '', disabled: true,}, [Validators.required]),
      'isCertificateValid': new FormControl({value: 'true', disabled: true,}, [Validators.requiredTrue]
      ),
    }, {
      validator: [
        this.certificateValidator,
        this.certificateExistValidator,
      ]
    });
    // bind values to form! not property
    this.userForm.controls['active'].setValue(this.current.active);
    this.userForm.controls['emailAddress'].setValue(this.current.emailAddress);
    this.userForm.controls['role'].setValue(this.current.role);
    // username/password authentication
    this.userForm.controls['username'].setValue(this.current.username);
    this.userForm.controls['passwordExpireOn'].setValue(this.current.passwordExpireOn);
    this.userForm.controls['accessTokenId'].setValue(this.current.accessTokenId);
    this.userForm.controls['accessTokenExpireOn'].setValue(this.current.accessTokenExpireOn);

    this.userForm.controls['casUserDataUrl'].setValue(this.current.casUserDataUrl);

    // certificate authentication
    this.userForm.controls['subject'].setValue(this.current.certificate.subject);
    this.userForm.controls['validFrom'].setValue(this.current.certificate.validFrom);
    this.userForm.controls['validTo'].setValue(this.current.certificate.validTo);
    this.userForm.controls['issuer'].setValue(this.current.certificate.issuer);
    this.userForm.controls['serialNumber'].setValue(this.current.certificate.serialNumber);
    this.userForm.controls['certificateId'].setValue(this.current.certificate.certificateId);
    this.userForm.controls['crlUrl'].setValue(this.current.certificate.crlUrl);
    this.userForm.controls['encodedValue'].setValue(this.current.certificate.encodedValue);
    this.userForm.controls['isCertificateValid'].setValue(!this.current.certificate.invalid);


    this.certificateValidationMessage = this.current.certificate.invalidReason;
    this.isCertificateInvalid = this.current.certificate.invalid;

  }

  changeCurrentUserPassword() {
    const formRef: MatDialogRef<any> = this.userController.changePasswordDialog({
      data:{
        user: this.getCurrent(),
        adminUser: this.securityService.isCurrentUserSystemAdmin() &&
          this.securityService.getCurrentUser().userId !== this.current.userId
      },
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        this.current.passwordExpireOn = result.passwordExpireOn;
        this.userForm.controls['passwordExpireOn'].setValue(this.current.passwordExpireOn);
      }
    });

  }

  onShowCertificateDataRow() {
    const formRef: MatDialogRef<any> = this.dialog.open(CertificateDialogComponent, {
      data: {row:this.getCurrent().certificate}
    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        // import
      }
    });
  }

  clearCertificate(){
    this.userForm.patchValue({
      'subject': null,
      'validFrom':null,
      'validTo': null,
      'issuer':null,
      'serialNumber': null,
      'certificateId': null,
      'crlUrl': null,
      'encodedValue': null,
      'isCertificateValid': null,
    });
  }

  openCurrentCasUserData() {
    window.open(this.current.casUserDataUrl, "_blank");
  }

  regenerateAccessToken() {
    const formRef: MatDialogRef<any> = this.userController.generateAccessTokenDialog({
      data: {
        user: this.getCurrent(),
        adminUser: this.securityService.isCurrentUserSystemAdmin() &&
          this.securityService.getCurrentUser().userId !== this.current.userId
      },

    });
    formRef.afterClosed().subscribe(result => {
      if (result) {
        let user = {...formRef.componentInstance.getCurrent()};
        // update value for current user
        this.current.accessTokenId = user.accessTokenId
        this.current.accessTokenExpireOn = user.accessTokenExpireOn
        // set form data
        this.userForm.controls['accessTokenId'].setValue(user.accessTokenId);
        this.userForm.controls['accessTokenExpireOn'].setValue(user.accessTokenExpireOn);

        this.lookups.refreshUserLookup();
      }
    });
  }


  submitForm() {
    this.dialogRef.close(true);
  }

  uploadCertificate(event) {
    this.newCertFile = null;
    const file = event.target.files[0];
    this.certificateService.validateCertificate(file).subscribe((res: CertificateRo) => {
        if (res && res.certificateId) {
          this.userForm.patchValue({
            'subject': res.subject,
            'validFrom': res.validFrom,
            'validTo': res.validTo,
            'issuer': res.issuer,
            'serialNumber': res.serialNumber,
            'certificateId': res.certificateId,
            'crlUrl': res.crlUrl,
            'encodedValue': res.encodedValue,
            'isCertificateValid': !res.invalid
          });
          this.certificateValidationMessage = res.invalidReason;
          this.isCertificateInvalid = res.invalid;
          this.newCertFile = file;
        } else {
          this.alertService.exception("Error occurred while reading certificate.", "Check if uploaded file has valid certificate type.", false);
        }
      },
      err => {
        this.alertService.exception('Error uploading certificate file ' + file.name, err);
      }
    );

  }



  isPreferencesMode() {
    return this.mode === UserDetailsDialogMode.PREFERENCES_MODE;
  }

  public getCurrent(): UserRo {
    this.current.active = this.userForm.get('active').value;
    this.current.emailAddress = this.userForm.get('emailAddress').value;
    this.current.role = this.userForm.get('role').value;
    // certificate data
    if (this.userForm.controls['certificateId'].value) {
      this.current.certificate.certificateId = this.userForm.controls['certificateId'].value;
      this.current.certificate.subject = this.userForm.controls['subject'].value;
      this.current.certificate.issuer = this.userForm.controls['issuer'].value;
      this.current.certificate.serialNumber = this.userForm.controls['serialNumber'].value;
      this.current.certificate.validFrom = this.userForm.controls['validFrom'].value;
      this.current.certificate.validTo = this.userForm.controls['validTo'].value;
      this.current.certificate.crlUrl = this.userForm.controls['crlUrl'].value;
      this.current.certificate.encodedValue = this.userForm.controls['encodedValue'].value;
      this.current.certificate.invalid = this.isCertificateInvalid;
      this.current.certificate.invalidReason = this.certificateValidationMessage;
    } else {
      this.current.certificate = null;
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
      crlUrl: '',
      encodedValue: '',
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
  isUserAuthSSOEnabled(): boolean {
    return this.lookups.cachedApplicationInfo?.authTypes?.includes('SSO');
  }

  isUserAuthPasswdEnabled(): boolean {
    return this.lookups.cachedApplicationInfo?.authTypes?.includes('PASSWORD');
  }

  isWebServiceUserCertificateAuthEnabled(): boolean {
    return this.lookups.cachedApplicationConfig?.webServiceAuthTypes?.includes('CERTIFICATE');
  }

  isWebServiceUserTokenAuthPasswdEnabled(): boolean {
    return this.lookups.cachedApplicationConfig?.webServiceAuthTypes?.includes('TOKEN');
  }

}

export enum UserDetailsDialogMode {
  NEW_MODE = 'New User',
  EDIT_MODE = 'User Edit',
  PREFERENCES_MODE = 'Edit',
}
