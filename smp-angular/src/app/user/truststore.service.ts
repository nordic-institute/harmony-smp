import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {SecurityService} from "../security/security.service";
import {User} from "../security/user.model";
import {TruststoreResult} from "./truststore-result.model";
import {CertificateRo} from "./certificate-ro.model";

@Injectable()
export class TruststoreService {

  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
  }

  uploadCertificate$(payload): Observable<CertificateRo> {
    // The user identifier below belongs to the currently logged in user and it may or may not be the same as the
    // identifier of the user being modified (e.g. a normal user editing his own details vs. a system administrator
    // adding or editing another user)

    // upload file as binary file
    const headers = new HttpHeaders()
      .set("Content-Type", "application/octet-stream");

    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<CertificateRo>(`${SmpConstants.REST_TRUSTSTORE}/${currentUser.id}/certdata`, payload, {headers});
  }

  deleteCertificateFromKeystore$(certificateAlias): Observable<TruststoreResult> {

     // encode password
    let certificateAliasEncoded = encodeURIComponent(certificateAlias);

    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.delete<TruststoreResult>(`${SmpConstants.REST_TRUSTSTORE}/${currentUser.id}/delete/${certificateAliasEncoded}`);
  }
}
