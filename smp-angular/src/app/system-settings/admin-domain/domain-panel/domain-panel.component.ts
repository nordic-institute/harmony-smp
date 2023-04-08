import {Component, Input,} from '@angular/core';
import {DomainRo} from "../../domain/domain-ro.model";
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AdminDomainService} from "../admin-domain.service";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {CertificateRo} from "../../user/certificate-ro.model";


@Component({
  selector: 'domain-panel',
  templateUrl: './domain-panel.component.html',
  styleUrls: ['./domain-panel.component.scss']
})
export class DomainPanelComponent {

  // Request from test team can not automate test if this is less than 10 seconds :(. Initialy it was 2s
  readonly warningTimeout : number = 10000;
  readonly domainCodePattern = '^[a-zA-Z0-9]{1,63}$';
  readonly dnsDomainPattern = '^([a-zA-Z]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?){0,63}$';
  readonly subDomainPattern = this.dnsDomainPattern;
  readonly smpIdDomainPattern = this.dnsDomainPattern;

  fieldWarningTimeoutMap = {
    domainCodeTimeout: null,
    smlSubdomainTimeout: null,
  };

  _domain: DomainRo = null;
  domainForm: FormGroup;
  editMode: boolean;
  createMode: boolean;

  @Input() keystoreCertificates:CertificateRo[];
  @Input() currentDomains:DomainRo[];

  notInList(list: string[], exception: string) {
    if (!list || !exception) {
      return (c: AbstractControl): { [key: string]: any } => {
        return null;
      }
    }

    return (c: AbstractControl): { [key: string]: any } => {
      if (c.value && c.value !== exception && list.includes(c.value))
        return {'notInList': {valid: false}};
      return null;
    }
  }

  /**
   * Show warning if domain code exceed the maxlength.
   * @param value
   */
  onFieldKeyPressed(controlName: string, showTheWarningReference:string) {
    let value = this.domainForm.get(controlName).value

    if (!!value && value.length >= 63 && !this.fieldWarningTimeoutMap[showTheWarningReference]) {
      this.fieldWarningTimeoutMap[showTheWarningReference] = setTimeout(() => {
        this.fieldWarningTimeoutMap[showTheWarningReference] = null;
      }, this.warningTimeout);
    }
  }



  constructor(private domainService: AdminDomainService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private formBuilder: FormBuilder) {

    this.domainForm = formBuilder.group({
      'domainCode': new FormControl({value: '', readonly: this.createMode},  [Validators.pattern(this.domainCodePattern),
        this.notInList(this.currentDomains?.map(a => a.domainCode), this._domain?.domainCode)]),
      'smlSubdomain': new FormControl({value: '', readonly: this.editMode}, [Validators.pattern(this.subDomainPattern),
        this.notInList(this.currentDomains?.map(a => a.smlSubdomain), this._domain?.smlSubdomain)]),
      'signatureKeyAlias': new FormControl({value: '', readonly: this.editMode}),
    });
  }

  get domain(): DomainRo {
    return this._domain;
  }

  @Input() set domain(value: DomainRo) {
    this._domain = value;

    if (!!value) {
      this.domainForm.controls['domainCode'].setValue(this._domain.domainCode);
      this.domainForm.controls['smlSubdomain'].setValue(this._domain.smlSubdomain);
      this.domainForm.controls['smlSmpId'].setValue(this._domain.smlSmpId);
      this.domainForm.controls['smlClientKeyAlias'].setValue(this._domain.smlClientKeyAlias);
      this.domainForm.controls['smlClientCertHeader'].setValue(this._domain.smlClientCertHeader);
      this.domainForm.controls['signatureKeyAlias'].setValue(this._domain.signatureKeyAlias);
      this.domainForm.controls['smlRegistered'].setValue(this._domain.smlRegistered);
      this.domainForm.controls['smlClientCertAuth'].setValue(this._domain.smlClientCertAuth);
    } else {
      this.domainForm.controls['domainCode'].setValue("");
      this.domainForm.controls['smlSubdomain'].setValue("");
      this.domainForm.controls['smlSmpId'].setValue("");
      this.domainForm.controls['smlClientKeyAlias'].setValue("");
      this.domainForm.controls['smlClientCertHeader'].setValue("");
      this.domainForm.controls['signatureKeyAlias'].setValue("");
      this.domainForm.controls['smlRegistered'].setValue("");
      this.domainForm.controls['smlClientCertAuth'].setValue("");
    }

    this.domainForm.markAsPristine();
  }

  onSaveClicked(){

  }
}
