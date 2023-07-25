import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SmpConstants} from "../../smp.constants";
import {SecurityService} from "../../security/security.service";
import {User} from "../../security/user.model";
import {CertificateRo} from "../user/certificate-ro.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {KeystoreResult} from "../domain/keystore-result.model";

@Injectable()
export class AdminKeystoreService {

  private keystoreUpdateSubject = new Subject<CertificateRo[]>();

  private keystoreEntryUpdateSubject = new Subject<CertificateRo[]>();

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService) {
  }

  getKeystoreData() {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<CertificateRo[]>(SmpConstants.REST_INTERNAL_KEYSTORE_MANAGE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId))
      .subscribe((result: CertificateRo[]) => {
        this.notifyKeystoreUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }


  uploadKeystore(selectedFile, keystoreType, password):Observable<KeystoreResult> {

    // upload file as binary file
    const headers = new HttpHeaders()
      .set("Content-Type", "application/octet-stream");

    // encode password
    let passwordEncoded = encodeURIComponent(password);

    let currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<KeystoreResult>(SmpConstants.REST_INTERNAL_KEYSTORE_UPLOAD
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_KEYSTORE_TYPE, keystoreType)
      .replace(SmpConstants.PATH_PARAM_KEYSTORE_PWD, passwordEncoded), selectedFile, {
      headers
    });
  }

  deleteEntryFromKeystore(certificateAlias) {
    // encode password
    let certificateAliasEncoded = encodeURIComponent(certificateAlias);
    const currentUser: User = this.securityService.getCurrentUser();

    this.http.delete<CertificateRo>(SmpConstants.REST_INTERNAL_KEYSTORE_DELETE_ENTRY
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_CERT_ALIAS, certificateAliasEncoded))
      .subscribe(
        (response: CertificateRo) => {
          this.notifyKeystoreEntryUpdated(response)
        }, error => {
          this.alertService.error(error.error?.errorDescription)
        });
  }

  notifyKeystoreUpdated(res: CertificateRo[]) {
    this.keystoreUpdateSubject.next(res);
  }

  notifyKeystoreEntryUpdated(res: CertificateRo) {
    this.keystoreEntryUpdateSubject.next([res]);
  }

  notifyKeystoreEntriesUpdated(res: CertificateRo[]) {
    this.keystoreEntryUpdateSubject.next(res);
  }

  onKeystoreUpdatedEvent(): Observable<CertificateRo[]> {
    return this.keystoreUpdateSubject.asObservable();
  }

  onKeystoreEntryUpdatedEvent(): Observable<CertificateRo[]> {
    return this.keystoreEntryUpdateSubject.asObservable();
  }

}
