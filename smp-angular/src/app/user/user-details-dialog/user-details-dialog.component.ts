import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatSlideToggleChange} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {UserService} from '../user.service';
import {Role} from '../../security/role.model';
import {UserRo} from '../user-ro.model';
import {SearchTableEntityStatus} from '../../common/search-table/search-table-entity-status.model';
import {AlertService} from '../../alert/alert.service';
import {CertificateService} from '../certificate.service';
import {CertificateRo} from "../certificate-ro.model";
import {DatePipe} from "../../custom-date/date.pipe";

@Component({
  selector: 'user-details-dialog',
  templateUrl: './user-details-dialog.component.html',
  styleUrls: ['user-details-dialog.component.css']
})
export class UserDetailsDialogComponent {

  static readonly NEW_MODE = 'New User';
  static readonly EDIT_MODE = 'User Edit';

  // readonly emailPattern = '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}';
  readonly passwordPattern = '^(?=.*[A-Z])(?=.*[ !#$%&\'()*+,-./:;<=>?@\\[^_`{|}~\\\]"])(?=.*[0-9])(?=.*[a-z]).{8,32}$';
  readonly dateFormat: string = 'yyyy-MM-dd HH:mm:ssZ';

  editMode: boolean;
  formTitle: string;
  userRoles = [];
  existingRoles = [];
  userForm: FormGroup;

  userSwitch: boolean;
  certificateSwitch: boolean;

  @ViewChild('fileInput')
  private fileInput;

  private passwordConfirmationValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const password = control.get('password');
    const confirmation = control.get('confirmation');
    return password && confirmation && password.value !== confirmation.value ? { confirmation: true } : null;
  };

  constructor(private dialogRef: MatDialogRef<UserDetailsDialogComponent>,
              private userService: UserService,
              private certificateService: CertificateService,
              private alertService: AlertService,
              private datePipe: DatePipe,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    this.editMode = data.edit;
    this.formTitle = this.editMode ?  UserDetailsDialogComponent.EDIT_MODE: UserDetailsDialogComponent.NEW_MODE;

    const user: UserRo & { confirmation?: string } = this.editMode
      ? {
        ...data.row,
        password: '', // ensures the user password is cleared before editing
        confirmation: '',
        certificate: data.row.certificate || {},
      }
      : {
        userName: '',
        email: '',
        password: '',
        confirmation: '',
        role: '',
        status: SearchTableEntityStatus.NEW,
        certificate: {},
      };

    this.userForm = fb.group({
      'userName': new FormControl({value: user.userName, disabled: this.editMode}, this.editMode ? Validators.nullValidator : null),
      'role': new FormControl(user.role, Validators.required),
      'password': new FormControl(user.password, [Validators.required, Validators.pattern(this.passwordPattern)]),
      'confirmation': new FormControl(user.password, Validators.pattern(this.passwordPattern)),

      'subject': new FormControl({ value: user.certificate.subject, disabled: true }),
      'validFrom': new FormControl({ value: user.certificate.validFrom, disabled: true }),
      'validUntil': new FormControl({ value: user.certificate.validUntil, disabled: true }),
      'issuer': new FormControl({ value: user.certificate.issuer, disabled: true }),
      'fingerprints': new FormControl({ value: user.certificate.fingerprints, disabled: true }),
    }, {
      validator: this.passwordConfirmationValidator
    });

    this.userService.getUserRoles$().subscribe(userRoles => {
      this.userRoles = userRoles.json();
      this.existingRoles = this.editMode
        ? this.getAllowedRoles(this.userRoles, user.role)
        : this.userRoles;
    });
  }

  submitForm() {
    this.dialogRef.close(true);
  }

  uploadCertificate() {
    const fi = this.fileInput.nativeElement;
    const file = fi.files[0];

    const reader = new FileReader();
    reader.onload = (e) => {
      const arrayBuffer = reader.result;
      const array = new Uint8Array(arrayBuffer);
      const binaryString = String.fromCharCode.apply(null, array);

      // TODO define userName or use some sort of userId
      // this.certificateService.uploadCertificate$({content: binaryString})
        // .subscribe((res: CertificateRo) => {
            // TODO user real service
            this.userForm.patchValue({
              'subject': 'subject',
              'validFrom': this.datePipe.transform(new Date().toString(), this.dateFormat),
              'validUntil': this.datePipe.transform(new Date().toString(), this.dateFormat),
              'issuer': 'issues',
              'fingerprints': 'fingerprints',
            });
          // },
          // err => {
          //   this.alertService.exception('Error uploading certificate file ' + file.name, err);
          // }
        // );
    };
    reader.onerror = (err) => {
      this.alertService.exception('Error reading certificate file ' + file.name, err);
    };

    reader.readAsArrayBuffer(file);
  }

  onUserToggleChanged({checked}: MatSlideToggleChange) {
    const action = checked ? 'enable' : 'disable';
    this.userForm.get('userName')[action]();
    this.userForm.get('role')[action]();
    this.userForm.get('password')[action]();
    this.userForm.get('confirmation')[action]();
  }

  get current(): UserRo {
    return this.userForm.getRawValue();
  }

  // filters out roles so that the user cannot change from system administrator to the other roles or vice-versa
  private getAllowedRoles(allRoles, userRole) {
    if (userRole === Role.SYSTEM_ADMINISTRATOR) {
      return [Role.SYSTEM_ADMINISTRATOR];
    } else {
      return allRoles.filter(role => role !== Role.SYSTEM_ADMINISTRATOR);
    }
  }
}
