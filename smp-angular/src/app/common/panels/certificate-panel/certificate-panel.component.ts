import {Component, Input} from '@angular/core';
import {CertificateRo} from "../../../system-settings/user/certificate-ro.model";

@Component({
  selector: 'certificate-panel',
  templateUrl: './certificate-panel.component.html',
  styleUrls: ['./certificate-panel.component.scss'],
  providers: [
    // `MomentDateAdapter` and `MAT_MOMENT_DATE_FORMATS` can be automatically provided by importing
    // `MatMomentDateModule` in your applications root module. We provide it at the component level
    // here, due to limitations of our example generation script.

  ],
})
export class CertificatePanelComponent {

  _certificate: CertificateRo = null;

  get certificate(): CertificateRo {
    return this._certificate;
  }

  @Input() set certificate(value: CertificateRo) {
    this._certificate = value;
  }

}
