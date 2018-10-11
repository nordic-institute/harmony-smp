import {Component, Inject} from '@angular/core';
import {MD_DIALOG_DATA, MdDialogRef} from "@angular/material";
import {AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {UserService} from "../user.service";
import {Role} from "../../security/role.model";
import {RoleService} from "../../security/role.service";
import {UserRo} from "../user-ro.model";
import {SearchTableEntityStatus} from "../../common/search-table/search-table-entity-status.model";

@Component({
  selector: 'user-details-dialog',
  templateUrl: './user-details-dialog.component.html'
})
export class UserDetailsDialogComponent {

  static readonly NEW_MODE = 'New User';
  static readonly EDIT_MODE = 'User Edit';

  // readonly emailPattern = '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}';
  readonly passwordPattern = '^(?=.*[A-Z])(?=.*[ !#$%&\'()*+,-./:;<=>?@\\[^_`{|}~\\\]"])(?=.*[0-9])(?=.*[a-z]).{8,32}$';

  editMode: boolean;
  formTitle: string;
  userRoles = [];
  existingRoles = [];
  confirmation = '';
  current: UserRo & { confirmation?: string };
  userForm: FormGroup;

  private passwordConfirmationValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const password = control.get('password');
    const confirmation = control.get('confirmation');
    return password && confirmation && password.value !== confirmation.value ? { confirmation: true } : null;
  };

  constructor(private dialogRef: MdDialogRef<UserDetailsDialogComponent>,
              private userService: UserService,
              private roleService: RoleService,
              @Inject(MD_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    this.editMode = data.edit;
    this.formTitle = this.editMode ?  UserDetailsDialogComponent.EDIT_MODE: UserDetailsDialogComponent.NEW_MODE;

    this.current = this.editMode
      ? {
        ...data.row,
        confirmation: data.row.password
      }
      : {
        userName: '',
        password: '',
        confirmation: '',
        role: '',
        status: SearchTableEntityStatus.NEW
      };

    this.userForm = fb.group({
      'userName': new FormControl({value: this.current.userName, disabled: this.editMode}, this.editMode ? Validators.nullValidator : null),
      'role': new FormControl(this.current.role, Validators.required),
      'password': new FormControl(this.current.password, [Validators.required, Validators.pattern(this.passwordPattern)]),
      'confirmation': new FormControl(this.current.password, Validators.pattern(this.passwordPattern))
    }, {
      validator: this.passwordConfirmationValidator
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
    this.current.userName = event.target.value;
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
}
