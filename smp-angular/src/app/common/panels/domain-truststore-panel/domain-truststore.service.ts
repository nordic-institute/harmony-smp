import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

import {HttpClient, HttpHeaders} from '@angular/common/http';
import {CertificateRo} from "../../model/certificate-ro.model";
import {SecurityService} from "../../../security/security.service";
import {AlertMessageService} from "../../alert-message/alert-message.service";
import {User} from "../../../security/user.model";
import {SmpConstants} from "../../../smp.constants";


@Injectable()
export class DomainTruststoreService {

  private truststoreUpdateSubject = new Subject<CertificateRo[]>();

  private truststoreEntryUpdateSubject = new Subject<CertificateRo>();

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService) {
  }

  getTruststoreData() {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<CertificateRo[]>(SmpConstants.REST_INTERNAL_TRUSTSTORE_MANAGE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId))
      .subscribe((result: CertificateRo[]) => {
        this.notifyTruststoreUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }


  uploadCertificate$(payload) {
    // upload file as binary file
    const headers = new HttpHeaders()
      .set("Content-Type", "application/octet-stream");

    const currentUser: User = this.securityService.getCurrentUser();

    return this.http.post<CertificateRo>(
      SmpConstants.REST_INTERNAL_TRUSTSTORE_UPLOAD_CERT
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId),

      payload, {headers}).subscribe(
      (response: CertificateRo) => {
        this.notifyTruststoreEntryUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  deleteCertificateFromTruststore(certificateAlias) {
    // encode password
    let certificateAliasEncoded = encodeURIComponent(certificateAlias);
    const currentUser: User = this.securityService.getCurrentUser();

    this.http.delete<CertificateRo>(SmpConstants.REST_INTERNAL_TRUSTSTORE_DELETE_CERT
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_CERT_ALIAS, certificateAliasEncoded))
      .subscribe(
        (response: CertificateRo) => {
          this.notifyTruststoreEntryUpdated(response)
        }, error => {
          this.alertService.error(error.error?.errorDescription)
        });
  }

  notifyTruststoreUpdated(res: CertificateRo[]) {
    this.truststoreUpdateSubject.next(res);
  }

  notifyTruststoreEntryUpdated(res: CertificateRo) {
    this.truststoreEntryUpdateSubject.next(res);
  }

  onTruststoreUpdatedEvent(): Observable<CertificateRo[]> {
    return this.truststoreUpdateSubject.asObservable();
  }

  onTruststoreEntryUpdatedEvent(): Observable<CertificateRo> {
    return this.truststoreEntryUpdateSubject.asObservable();
  }

}
