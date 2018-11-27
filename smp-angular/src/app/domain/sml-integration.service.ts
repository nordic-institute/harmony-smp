import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {SecurityService} from "../security/security.service";
import {User} from "../security/user.model";
import {KeystoreResult} from "./keystore-result.model";

@Injectable()
export class SmlIntegrationService {

  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
  }

  registerDomainToSML$(domainCode): Observable<KeystoreResult> {

    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post<KeystoreResult>(`${SmpConstants.REST_DOMAIN}/${currentUser.id}/smlregister/${domainCode}`, {});
  }

  unregisterDomainToSML$(domainCode): Observable<KeystoreResult> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.post(`${SmpConstants.REST_DOMAIN}/${currentUser.id}/smlunregister/${domainCode}`,{});
  }
}
