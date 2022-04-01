import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {SmpConstants} from "../smp.constants";
import {SecurityService} from "../security/security.service";
import {User} from "../security/user.model";
import {KeystoreResult} from "./keystore-result.model";
import {SMLResult} from "./sml-result.model";

@Injectable()
export class SmlIntegrationService {

  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
  }

  registerDomainToSML$(domainCode): Observable<SMLResult> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<SMLResult>(`${SmpConstants.REST_INTERNAL_DOMAIN_MANAGE}/${currentUser.userId}/sml-register/${domainCode}`, {});
  }

  unregisterDomainToSML$(domainCode): Observable<SMLResult> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<SMLResult>(`${SmpConstants.REST_INTERNAL_DOMAIN_MANAGE}/${currentUser.userId}/sml-unregister/${domainCode}`, {});
  }
}
