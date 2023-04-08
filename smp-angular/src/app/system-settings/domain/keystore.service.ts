import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {HttpClient, HttpHeaders} from '@angular/common/http';
import {SmpConstants} from "../../smp.constants";
import {SecurityService} from "../../security/security.service";
import {User} from "../../security/user.model";
import {KeystoreResult} from "./keystore-result.model";

@Injectable()
export class KeystoreService {

  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
  }

  uploadKeystore(selectedFile, keystoreType, password): Observable<KeystoreResult> {

    // upload file as binary file
    const headers = new HttpHeaders()
      .set("Content-Type", "application/octet-stream");

    // encode password
    let passwordEncoded = encodeURIComponent(password);

    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<KeystoreResult>(SmpConstants.REST_INTERNAL_KEYSTORE_UPLOAD
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_KEYSTORE_TYPE, keystoreType)
      .replace(SmpConstants.PATH_PARAM_KEYSTORE_PWD, passwordEncoded), selectedFile, {
      headers
    });
  }

  deleteCertificateFromKeystore$(certificateAlias): Observable<KeystoreResult> {

    // encode password
    let certificateAliasEncoded = encodeURIComponent(certificateAlias);

    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.delete<KeystoreResult>(`${SmpConstants.REST_INTERNAL_KEYSTORE_DEPRECATED}/${currentUser.userId}/delete/${certificateAliasEncoded}`);
  }
}
