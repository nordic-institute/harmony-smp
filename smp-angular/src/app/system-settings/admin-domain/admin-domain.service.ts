import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

import {HttpClient} from '@angular/common/http';
import {SecurityService} from "../../security/security.service";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {DomainRo} from "../../common/model/domain-ro.model";
import {User} from "../../security/user.model";
import {SmpConstants} from "../../smp.constants";

@Injectable()
export class AdminDomainService {

  private domainUpdateSubject = new Subject<DomainRo[]>();
  private domainEntryUpdateSubject = new Subject<DomainRo>();

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService) {
  }

  public getDomains() {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<DomainRo[]>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId))
      .subscribe((result: DomainRo[]) => {
        this.notifyDomainsUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  public deleteDomains(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.delete<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId))
      .subscribe((result: DomainRo) => {
        this.notifyDomainEntryUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  public updateDomain(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_UPDATE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      , domain)
      .subscribe((result: DomainRo) => {
        this.notifyDomainEntryUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }


  public createDomain(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.put<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_CREATE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      , domain)
      .subscribe((result: DomainRo) => {
        this.notifyDomainEntryUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  public updateDomainSMLIntegrationData(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_UPDATE_SML_INTEGRATION
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      , domain)
      .subscribe((result: DomainRo) => {
        this.notifyDomainEntryUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  public updateDomainResourceTypes(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_UPDATE_RESOURCE_TYPES
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      , domain.resourceDefinitions)
      .subscribe((result: DomainRo) => {
        this.notifyDomainEntryUpdated(result);
      }, (error: any) => {
        this.alertService.error(error.error?.errorDescription)
      });
  }


  notifyDomainsUpdated(res: DomainRo[]) {
    this.domainUpdateSubject.next(res);
  }

  notifyDomainEntryUpdated(res: DomainRo) {
    this.domainEntryUpdateSubject.next(res);
  }

  onDomainUpdatedEvent(): Observable<DomainRo[]> {
    return this.domainUpdateSubject.asObservable();
  }

  onDomainEntryUpdatedEvent(): Observable<DomainRo> {
    return this.domainEntryUpdateSubject.asObservable();
  }

}
