import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {UserRo} from "../../user/user-ro.model";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {DomainRo} from "../domain-ro.model";
import {AlertService} from "../../alert/alert.service";
import {UserDetailsDialogComponent} from "../../user/user-details-dialog/user-details-dialog.component";
import {CertificateService} from "../../user/certificate.service";
import {UserService} from "../../user/user.service";
import {SearchTableEntityStatus} from "../../common/search-table/search-table-entity-status.model";

@Component({
  selector: 'domain-details-dialog',
  templateUrl: './domain-details-dialog.component.html'
})
export class DomainDetailsDialogComponent {

  static readonly NEW_MODE = 'New Domain';
  static readonly EDIT_MODE = 'Domain Edit';
  readonly dnsDomainPattern = '^(?!(\\d|-|_)+)[a-zA-Z0-9-]{1,63}$';
  readonly domainCodePattern = '^[a-zA-Z]{1,255}$';

  editMode: boolean;
  formTitle: string;
  current: DomainRo & { confirmation?: string };
  domainForm: FormGroup;

  userSwitch: boolean;
  certificateSwitch: boolean;

  domain;


  constructor(private dialogRef: MatDialogRef<DomainDetailsDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {

    this.editMode = data.edit;
    this.formTitle = this.editMode ?  DomainDetailsDialogComponent.EDIT_MODE: DomainDetailsDialogComponent.NEW_MODE;
    this.current = this.editMode
      ? {
        ...data.row,
      }
      : {
        domainCode: '',
        email: '',
        password: '',
        confirmation: '',
        role: '',
        status: SearchTableEntityStatus.NEW,
        certificate: {},
      };

    this.domainForm = fb.group({

      'domainCode': new FormControl({value: this.current.domainCode, disabled: this.editMode}, [Validators.pattern(this.domainCodePattern)]),
      'smlSubdomain': new FormControl({value: this.current.smlSubdomain, disabled: this.editMode},  [Validators.pattern(this.dnsDomainPattern)]),
      'smlSmpId': new FormControl({value: this.current.smlSmpId}, [Validators.required, Validators.pattern(this.dnsDomainPattern)]),
      'smlClientKeyAlias': new FormControl({value: this.current.signatureKeyAlias}, null),
      'signatureKeyAlias': new FormControl({value: this.current.signatureKeyAlias}, null),


    }, {
      //validator: this.passwordConfirmationValidator
    });
  }
  submitForm() {
    this.checkValidity(this.domainForm)
     this.dialogRef.close(true);
  }

  checkValidity(g: FormGroup) {
    Object.keys(g.controls).forEach(key => {
      g.get(key).markAsDirty();
    });
    Object.keys(g.controls).forEach(key => {
      g.get(key).markAsTouched();
    });
    //!!! updateValueAndValidity - else some filed did no update current / on blur never happened
    Object.keys(g.controls).forEach(key => {
      g.get(key).updateValueAndValidity();
    });
  }


  updateDomainCode(event) {
    this.current.domainCode = event.target.value;
  }
  updateSmlDomain(event) {
    this.current.smlSubdomain = event.target.value;
  }
  updateSmlSmpId(event) {
    this.current.smlSmpId = event.target.value;
  }

  updateSmlClientKeyAlias(event) {
    this.current.smlClientKeyAlias = event.target.value;
  }

  updateSignatureKeyAlias(event) {
    this.current.signatureKeyAlias = event.target.value;
  }


}
