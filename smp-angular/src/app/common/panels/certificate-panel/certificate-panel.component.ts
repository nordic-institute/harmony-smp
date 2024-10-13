import {Component, Input} from '@angular/core';
import {CertificateRo} from "../../model/certificate-ro.model";
import {DateTimeService} from "../../services/date-time.service";

@Component({
  selector: 'certificate-panel',
  templateUrl: './certificate-panel.component.html',
  styleUrls: ['./certificate-panel.component.scss'],
})
export class CertificatePanelComponent {

  _certificate: CertificateRo = null;

  constructor(private dateTimeService: DateTimeService) {
  }

  get certificate(): CertificateRo {
    return this._certificate;
  }

  @Input() set certificate(value: CertificateRo) {
    this._certificate = value;
  }

  public formatDate(date: Date): string {
    return this.dateTimeService.formatDateTimeForUserLocal(date);
  }

  get getCertificateValidFromFormattedDate(): string {
    return this.formatDate(this._certificate?.validFrom);
  }
  get getCertificateValidToFormattedDate(): string {
    return this.formatDate(this._certificate?.validTo);
  }
}
