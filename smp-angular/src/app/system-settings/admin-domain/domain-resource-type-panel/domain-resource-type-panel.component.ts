import {Component, EventEmitter, Input, Output,} from '@angular/core';
import {DomainRo} from "../../../common/model/domain-ro.model";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {ResourceDefinitionRo} from "../../admin-extension/resource-definition-ro.model";
import {BeforeLeaveGuard} from "../../../window/sidenav/navigation-on-leave-guard";


@Component({
  selector: 'domain-resource-type-panel',
  templateUrl: './domain-resource-type-panel.component.html',
  styleUrls: ['./domain-resource-type-panel.component.scss']
})
export class DomainResourceTypePanelComponent implements BeforeLeaveGuard {
  @Output() onSaveResourceTypesEvent: EventEmitter<DomainRo> = new EventEmitter();
  _domain: DomainRo = null;
  createMode: boolean = false;
  @Input() domiSMPResourceDefinitions: ResourceDefinitionRo[] = [];
  domainForm: FormGroup;


  @Input() set domain(value: DomainRo) {
    this._domain = value;

    if (!!value) {
      this.domainForm.controls['resourceDefinitions'].setValue(this._domain.resourceDefinitions);
      this.domainForm.enable();
    } else {
      this.domainForm.controls['resourceDefinitions'].setValue([]);
      this.domainForm.disable();
    }
    this.domainForm.markAsPristine();
  }

  constructor(
    private formBuilder: FormBuilder) {

    this.domainForm = formBuilder.group({
      'resourceDefinitions': new FormControl({value: '', readonly: this.createMode})
    });
  }

  /**
   * If domain is not set, the event is ignored
   * else it updates the resource definitions and emtis
   * "onSave event"
   */
  onSaveClicked() {
    if (!this._domain){
      return
    }
    this._domain.resourceDefinitions = this.domainForm.controls['resourceDefinitions'].value;
    this.onSaveResourceTypesEvent.emit(this._domain);
  }



  get submitButtonEnabled(): boolean {
    return this.domainForm.valid && this.domainForm.dirty;
  }

  get resetButtonEnabled(): boolean {
    return this.domainForm.dirty;
  }
  public onResetButtonClicked(){
    this.domainForm.reset(this._domain);
  }

  isDirty(): boolean {
    return this.domainForm.dirty;
  }
}
