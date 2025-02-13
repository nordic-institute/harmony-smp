import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../../smp.constants";
import {SecurityService} from "../../security/security.service";
import {User} from "../../security/user.model";
import {SMLResult} from "../model/sml-result.model";
import {DomainRo} from "../model/domain-ro.model";

@Injectable()
export class SmlIntegrationService {

  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
  }

  registerDomainToSML$(domain: DomainRo): Observable<SMLResult> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<SMLResult>(SmpConstants.REST_INTERNAL_DOMAIN_SML_REGISTER
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId), {});
  }

  unregisterDomainToSML$(domain: DomainRo): Observable<SMLResult> {
    const currentUser: User = this.securityService.getCurrentUser();
    return this.http.put<SMLResult>(SmpConstants.REST_INTERNAL_DOMAIN_SML_UNREGISTER
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId), {});
  }
}
