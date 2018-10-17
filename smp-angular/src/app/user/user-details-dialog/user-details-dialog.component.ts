import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {UserService} from '../user.service';
import {Role} from '../../security/role.model';
import {RoleService} from '../../security/role.service';
import {UserRo} from '../user-ro.model';
import {SearchTableEntityStatus} from '../../common/search-table/search-table-entity-status.model';
import {AlertService} from '../../alert/alert.service';
import {CertificateService} from '../certificate.service';
import {Observable} from "rxjs/index";
import {CertificateRo} from "../certificate-ro.model";
import {SearchTableResult} from "../../common/search-table/search-table-result.model";


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

  editMode: boolean;
  formTitle: string;
  userRoles = [];
  role: string; // temporally added by JRC just to compile the code
  dateFormat: string; // temporally added by JRC just to compile the code
  existingRoles = [];
  current: UserRo & { confirmation?: string };
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
              private roleService: RoleService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    this.editMode = data.edit;
    this.formTitle = this.editMode ?  UserDetailsDialogComponent.EDIT_MODE: UserDetailsDialogComponent.NEW_MODE;

    this.current = this.editMode
      ? {
        ...data.row,
        confirmation: data.row.password,
        certificate: data.row.certificate,
      }
      : {
        username: '',
        email: '',
        password: '',
        confirmation: '',
        role: '',
        status: SearchTableEntityStatus.NEW,
        certificate: {},
      };

    this.userForm = fb.group({
      'username': new FormControl({value: this.current.username, disabled: this.editMode}, this.editMode ? Validators.nullValidator : null),
      'role': new FormControl(this.current.role, Validators.required),
      'password': new FormControl(this.current.password, [Validators.required, Validators.pattern(this.passwordPattern)]),
      'confirmation': new FormControl(this.current.password, Validators.pattern(this.passwordPattern)), }, {  validator: this.passwordConfirmationValidator
    });

    this.userService.getUserRoles$().subscribe(userRoles => {
      this.userRoles = userRoles.json();
      this.existingRoles = this.editMode
        ? this.getAllowedRoles(this.userRoles, this.current.role)
        : this.userRoles;
    });
  }

  submitForm() {
    this.dialogRef.close(true);
  }

  updateUserName(event) {
    this.current.username = event.target.value;
  }

  updatePassword(event) {
    this.current.password = event.target.value;
  }

  getRoleLabel(role: Role): string {
    return this.roleService.getLabel(role);
  }

  // filters out roles so that the user cannot change from system administrator to the other roles or vice-versa
  private getAllowedRoles(allRoles, userRole) {
    if (userRole === Role.SYSTEM_ADMINISTRATOR) {
      return [Role.SYSTEM_ADMINISTRATOR];
    } else {
      return allRoles.filter(role => role !== Role.SYSTEM_ADMINISTRATOR);
    }
  }

  uploadCertificate () {
    const fi = this.fileInput.nativeElement;
    const file = fi.files[0];

    const reader = new FileReader();
    reader.onload = (e) => {
      const arrayBuffer = reader.result;
      const array = new Uint8Array(arrayBuffer);
      const binaryString = String.fromCharCode.apply(null, array);

      // TODO define userName or use some sort of userId
      this.certificateService.uploadCertificate$({content: binaryString}, 'TODO')
        .subscribe(res => {
            this.current.certificate = res;
          },
          err => {
            this.alertService.exception('Error uploading certificate file ' + file.name, err);
          }
        );
    };
    reader.onerror = (err) => {
      this.alertService.exception('Error reading certificate file ' + file.name, err);
    };

    reader.readAsArrayBuffer(file);
  }


  // example
  selectedFile: File;
  obrs:  Observable< CertificateRo> ;
  certData:  CertificateRo;
  onFileChanged(event) {
    this.selectedFile = event.target.files[0]
  }

  onUpload() {
     this.obrs = this.certificateService.onUpload(this.selectedFile);
    this.obrs.subscribe((cert: CertificateRo) => {
      this.certData = cert;
    });

  }
}
