import {Component, Input,} from '@angular/core';
import {DomainRo} from "../../domain/domain-ro.model";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AdminDomainService} from "../admin-domain.service";
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {MatDialog} from "@angular/material/dialog";


@Component({
  selector: 'domain-sml-integration-panel',
  templateUrl: './domain-sml-integration-panel.component.html',
  styleUrls: ['./domain-sml-integration-panel.component.scss']
})
export class DomainSmlIntegrationPanelComponent {

  _domain: DomainRo = null;

  domainForm: FormGroup;

  constructor(private domainService: AdminDomainService,
              private alertService: AlertMessageService,
              private dialog: MatDialog,
              private formBuilder: FormBuilder) {

    this.domainForm = formBuilder.group({

      'smlSmpId': new FormControl({value: '', readonly: true}),
      'smlClientCertHeader': new FormControl({value: '', readonly: true}),
      'smlClientKeyAlias': new FormControl({value: '', readonly: true}),
      'smlClientKeyCertificate': new FormControl({value: '', readonly: true}),
      'smlRegistered': new FormControl({value: '', readonly: true}),
      'smlClientCertAuth': new FormControl({value: '', readonly: true}),
    });
  }

  get domain(): DomainRo {
    return this._domain;
  }

  @Input() set domain(value: DomainRo) {
    this._domain = value;
  if (!!this._domain) {
      this.domainForm.controls['smlSmpId'].setValue(this._domain.smlSmpId);
      this.domainForm.controls['smlClientKeyAlias'].setValue(this._domain.smlClientKeyAlias);
      this.domainForm.controls['smlClientCertHeader'].setValue(this._domain.smlClientCertHeader);
      this.domainForm.controls['smlRegistered'].setValue(this._domain.smlRegistered);
      this.domainForm.controls['smlClientCertAuth'].setValue(this._domain.smlClientCertAuth);
    } else {
       this.domainForm.controls['smlSmpId'].setValue("");
      this.domainForm.controls['smlClientKeyAlias'].setValue("");
      this.domainForm.controls['smlClientCertHeader'].setValue("");
      this.domainForm.controls['smlRegistered'].setValue("");
      this.domainForm.controls['smlClientCertAuth'].setValue("");
    }

    this.domainForm.markAsPristine();
  }

  onSaveClicked(){

  }
}
