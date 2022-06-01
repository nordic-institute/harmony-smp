import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {CertificateRo} from './certificate-ro.model';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {SecurityService} from "../security/security.service";
import {User} from "../security/user.model";

@Injectable()
export class CertificateService {

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
  ) { }

  validateCertificate(payload): Observable<CertificateRo> {
    // The user identifier below belongs to the currently logged in user and it may or may not be the same as the
    // identifier of the user being modified (e.g. a normal user editing his own details vs. a system administrator
    // adding or editing another user)

    // upload file as binary file
    const headers = new HttpHeaders()
      .set("Content-Type", "application/octet-stream");

    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<CertificateRo>(SmpConstants.REST_PUBLIC_TRUSTSTORE_CERT_VALIDATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId), payload, {headers});
  }
}
