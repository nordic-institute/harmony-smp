import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {CredentialRo} from "../../../security/credential.model";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";


@Component({
  selector: 'access-token-panel',
  templateUrl: './access-token-panel.component.html',
  styleUrls: ['./access-token-panel.component.scss']
})
export class AccessTokenPanelComponent implements BeforeLeaveGuard {

  @Output() onDeleteEvent: EventEmitter<CredentialRo> = new EventEmitter();
  @Output() onSaveEvent: EventEmitter<CredentialRo> = new EventEmitter();

  dateFormat: string = 'yyyy-MM-dd'

  _credential: CredentialRo;
  credentialForm: FormGroup;


  constructor(private formBuilder: FormBuilder) {
    this.credentialForm = formBuilder.group({
      // common values
      'active': new FormControl({value: '', disabled: false}),
      'description': new FormControl({value: '', disabled: false}),
      'activeFrom': new FormControl({value: '', disabled: false}),
      'expireOn': new FormControl({value: '', disabled: false})
    });
  }

  get credential(): CredentialRo {
    return this._credential;
  }

  @Input() set credential(value: CredentialRo) {
    this._credential = value;
    if (this._credential) {
      this.credentialForm.controls['active'].setValue(this._credential.active);
      this.credentialForm.controls['description'].setValue(this._credential.description);
      this.credentialForm.controls['activeFrom'].setValue(this._credential.activeFrom);
      this.credentialForm.controls['expireOn'].setValue(this._credential.expireOn);
    } else {
      this.credentialForm.controls['active'].setValue(null);
      this.credentialForm.controls['description'].setValue(null);
      this.credentialForm.controls['activeFrom'].setValue(null);
      this.credentialForm.controls['expireOn'].setValue(null);
    }

    // mark form as pristine
    this.credentialForm.markAsPristine();
  }

  onDeleteButtonClicked() {

    this.onDeleteEvent.emit(this.credential);
  }

  onSaveButtonClicked() {

    this._credential.active = this.credentialForm.controls['active'].value
    this._credential.description = this.credentialForm.controls['description'].value
    this._credential.activeFrom = this.credentialForm.controls['activeFrom'].value
    this._credential.expireOn = this.credentialForm.controls['expireOn'].value

    this.onSaveEvent.emit(this._credential);
  }

  get submitButtonEnabled(): boolean {
    return this.credentialForm.valid && this.credentialForm.dirty;
  }

  get sequentialLoginFailureCount(): string {
    return this._credential && this._credential.sequentialLoginFailureCount ?
      this._credential.sequentialLoginFailureCount + "" : "0";
  }

  get suspendedUtil(): string {
    return this._credential && this._credential.suspendedUtil ?
      this._credential.suspendedUtil.toLocaleDateString() : "---";
  }

  get lastFailedLoginAttempt(): string {
    return this._credential && this._credential.lastFailedLoginAttempt ?
      this._credential.lastFailedLoginAttempt.toLocaleDateString() : "---";
  }

  isDirty(): boolean {
    return this.credentialForm.dirty;
  }

}
