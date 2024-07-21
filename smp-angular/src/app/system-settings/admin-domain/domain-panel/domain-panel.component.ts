import {Component, ElementRef, EventEmitter, Input, Output, ViewChild,} from '@angular/core';
import {DomainRo} from "../../../common/model/domain-ro.model";
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AdminDomainService} from "../admin-domain.service";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";
import {VisibilityEnum} from "../../../common/enums/visibility.enum";
import {ResourceDefinitionRo} from "../../admin-extension/resource-definition-ro.model";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";
import {CertificateRo} from "../../../common/model/certificate-ro.model";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'domain-panel',
  templateUrl: './domain-panel.component.html',
  styleUrls: ['./domain-panel.component.scss']
})
export class DomainPanelComponent implements BeforeLeaveGuard {
  @Output() onSaveBasicDataEvent: EventEmitter<DomainRo> = new EventEmitter();

  @Output() onDiscardNew: EventEmitter<any> = new EventEmitter();
  readonly warningTimeout: number = 3000;
  readonly domainCodePattern = '^[a-zA-Z0-9]{1,63}$';
  readonly domainVisibilityOptions = Object.keys(VisibilityEnum)
    .map(el => {
      return {key: el, value: VisibilityEnum[el]}
    });

  fieldWarningTimeoutMap = {
    domainCodeTimeout: null,
  };

  _domain: DomainRo = null;
  domainForm: FormGroup;
  editMode: boolean;
  createMode: boolean;

  @Input() keystoreCertificates: CertificateRo[];
  @Input() currentDomains: DomainRo[];
  @Input() domiSMPResourceDefinitions: ResourceDefinitionRo[];


  @ViewChild('domainCode', {static: false}) domainCodeField: ElementRef;

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
  onFieldKeyPressed(controlName: string, showTheWarningReference: string) {

    if (this.domainForm.controls['domainCode'].hasError('pattern')) {
      // already visible error - skip the length validation
      return;
    }

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
              private formBuilder: FormBuilder,
              private translateService: TranslateService) {

    this.domainForm = formBuilder.group({
      'domainCode': new FormControl({value: '', readonly: true}, [Validators.pattern(this.domainCodePattern),
        this.notInList(this.currentDomains?.map(a => a.domainCode), this._domain?.domainCode)]),
      'signatureKeyAlias': new FormControl({value: '', readonly: true}),
      'adminMemberCount': new FormControl({value: '', readonly: true}),
      'visibility': new FormControl({value: '', readonly: true}),
      'defaultResourceTypeIdentifier': new FormControl({value: '', disabled: this.isNewDomain()}),
    });
  }

  get domain(): DomainRo {
    let newDomain = {...this._domain};
    newDomain.domainCode = this.domainForm.get('domainCode').value;
    newDomain.signatureKeyAlias = this.domainForm.get('signatureKeyAlias').value;
    newDomain.visibility = this.domainForm.get('visibility').value;
    newDomain.defaultResourceTypeIdentifier = this.domainForm.get('defaultResourceTypeIdentifier').value;
    return newDomain;
  }

  @Input() set domain(value: DomainRo) {
    this._domain = value;

    if (!!value) {
      this.domainForm.controls['domainCode'].setValue(this._domain.domainCode);
      this.domainForm.controls['signatureKeyAlias'].setValue(this._domain.signatureKeyAlias);
      this.domainForm.controls['adminMemberCount'].setValue(this._domain.adminMemberCount);
      this.domainForm.controls['visibility'].setValue(this._domain.visibility);
      this.domainForm.controls['defaultResourceTypeIdentifier'].setValue(this._domain.defaultResourceTypeIdentifier);
      this.domainForm.enable();
      if (!!value?.domainId) {
        this.domainForm.controls['domainCode'].disable();
      }
    } else {
      this.domainForm.controls['domainCode'].setValue("");
      this.domainForm.controls['signatureKeyAlias'].setValue("");
      this.domainForm.controls['adminMemberCount'].setValue("0");
      this.domainForm.controls['visibility'].setValue(VisibilityEnum.Public);
      this.domainForm.controls['defaultResourceTypeIdentifier'].setValue("");
      this.domainForm.disable();
    }
    this.domainForm.markAsPristine();
  }

  isNewDomain(): boolean {
    return this._domain != null && !this._domain.domainId
  }

  isDirty(): boolean {
    return this.isNewDomain() || this.domainForm?.dirty;
  }

  get domainResourceTypes() {
    if (!this._domain || !this._domain.resourceDefinitions) {
      return [];
    }
    return this.domiSMPResourceDefinitions.filter(resType => this._domain.resourceDefinitions.includes(resType.identifier))
  }

  get showWarning() {
    return !!this._domain?.domainId && (!this.domainResourceTypes?.length
      || !this._domain.signatureKeyAlias
      || !this._domain.adminMemberCount
      || this._domain.adminMemberCount < 1)
  }

  get showWarningMessage() {
    let message = this.translateService.instant("domain.panel.warning.domain.configuration.prefix");
    if (!this._domain.signatureKeyAlias) {
      message += this.translateService.instant("domain.panel.warning.domain.configuration.option.signature.key");
    }
    if (!this.domainResourceTypes?.length) {
      message += this.translateService.instant("domain.panel.warning.domain.configuration.option.resource.type");
    }
    if (!this._domain.adminMemberCount || this._domain.adminMemberCount < 1) {
      message += this.translateService.instant("domain.panel.warning.domain.configuration.option.admin.member");
    }
    message += "</ul>"; // No need to translate this part
    return message;
  }

  get submitButtonEnabled(): boolean {
    return this.domainForm.valid && this.domainForm.dirty;
  }

  get resetButtonEnabled(): boolean {
    return this.domainForm.dirty || this.isNewDomain();
  }

  public onSaveButtonClicked() {
    this.onSaveBasicDataEvent.emit(this.domain);
  }

  public onResetButtonClicked() {
    if (this.isNewDomain()) {
      this.onDiscardNew.emit();
    } else {
      this.domainForm.reset(this._domain);
    }
  }

  public setFocus() {
    setTimeout(() => this.domainCodeField.nativeElement.focus());
  }

}
