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
  readonly dnsDomainPattern = '^(?![0-9]+$)(?!.*-$)(?!-)[a-zA-Z0-9-]{0,63}$';
  readonly domainCodePattern = '^[a-zA-Z0-9]{1,255}$';

  editMode: boolean;
  formTitle: string;
  current: DomainRo & { confirmation?: string };
  domainForm: FormGroup;
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
        smlSubdomain: '',
        smlSmpId: '',
        smlClientKeyAlias: '',
        signatureKeyAlias: '',
        status: SearchTableEntityStatus.NEW,
      };

    this.domainForm = fb.group({

      'domainCode': new FormControl({value:'', disabled: this.editMode}, [Validators.pattern(this.domainCodePattern)]),
      'smlSubdomain': new FormControl({value: '', disabled: this.editMode},  [Validators.pattern(this.dnsDomainPattern)]),
      'smlSmpId': new FormControl({value: ''}, [Validators.pattern(this.dnsDomainPattern)]),
      'smlClientKeyAlias': new FormControl({value: ''}, null),
      'signatureKeyAlias': new FormControl({value:''}, null),

    }, {
      //validator: this.passwordConfirmationValidator
    });
    this.domainForm.controls['domainCode'].setValue(this.current.domainCode);
    this.domainForm.controls['smlSubdomain'].setValue(this.current.smlSubdomain);
    this.domainForm.controls['smlSmpId'].setValue(this.current.smlSmpId);
    this.domainForm.controls['smlClientKeyAlias'].setValue(this.current.smlClientKeyAlias);
    this.domainForm.controls['signatureKeyAlias'].setValue(this.current.signatureKeyAlias);


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

  public getCurrent(): DomainRo {

    this.current.domainCode = this.domainForm.value['domainCode'];
    this.current.smlSubdomain = this.domainForm.value['smlSubdomain'];
    this.current.smlSmpId = this.domainForm.value['smlSmpId'];
    this.current.smlClientKeyAlias = this.domainForm.value['smlClientKeyAlias'];
    this.current.signatureKeyAlias = this.domainForm.value['signatureKeyAlias'];

    return this.current;

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
