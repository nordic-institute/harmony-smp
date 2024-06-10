import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';

import {HttpClient} from '@angular/common/http';
import {SecurityService} from "../../security/security.service";
import {
  AlertMessageService
} from "../../common/alert-message/alert-message.service";
import {DomainRo} from "../../common/model/domain-ro.model";
import {User} from "../../security/user.model";
import {SmpConstants} from "../../smp.constants";
import {DomainPropertyRo} from "../../common/model/domain-property-ro.model";

@Injectable()
export class AdminDomainService {

  private domainUpdateSubject: Subject<DomainRo[]> = new Subject<DomainRo[]>();
  private domainEntryUpdateSubject: Subject<DomainRo> = new Subject<DomainRo>();
  private domainPropertyUpdateSubject: Subject<DomainPropertyRo[]> = new Subject<DomainPropertyRo[]>();

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService) {
  }

  /**
   * Get list of all domains the current user can administer
   */
  public getDomains() {

    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<DomainRo[]>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId))
      .subscribe({
        next: (result: DomainRo[]) => {
          this.notifyDomainsUpdated(result);
        },
        error: (error: any) => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  public getDomainProperties(domain: DomainRo): void {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.get<DomainPropertyRo[]>(SmpConstants.REST_INTERNAL_DOMAIN_PROPERTIES_MANAGE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId))
      .subscribe({
        next: (result: DomainPropertyRo[]): void => {
          this.notifyPropertiesUpdated(domain, result);
        },
        error: (error: any): void => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  /**
   * Update basic domain data
   * @param domain Domain to update
   */
  public updateDomainData(domain: DomainRo): void {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<DomainPropertyRo[]>(SmpConstants.REST_INTERNAL_DOMAIN_PROPERTIES_MANAGE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId),
      domain)
      .subscribe({
        next: (result: DomainPropertyRo[]): void => {
          this.notifyPropertiesUpdated(domain, result);
        },
        error: (error: any): void => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  /**
   * Update domain property list
   * @param domain Domain to update
   * @param domainProperties List of domain properties
   */
  public updateDomainProperties(domain: DomainRo, domainProperties: DomainPropertyRo[] ): void {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<DomainPropertyRo[]>(SmpConstants.REST_INTERNAL_DOMAIN_PROPERTIES_MANAGE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId),
      domainProperties)
      .subscribe({
        next: (result: DomainPropertyRo[]): void => {
          this.notifyPropertiesUpdated(domain, result);
        },
        error: (error: any): void => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  /**
   *  Delete a domain from the system
   * @param domain Domain to delete
   */
  public deleteDomain(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.delete<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId))
      .subscribe({
        next: (result: DomainRo) => {
          this.notifyDomainEntryUpdated(result);
        },
        error: (error: any) => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  /**
   * Update a domain data
   * @param domain Domain to update
   */
  public updateDomain(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_UPDATE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      , domain)
      .subscribe({
        next: (result: DomainRo) => {
          this.notifyDomainEntryUpdated(result);
        }, error: (error: any) => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  /**
   * Create a new domain
   * @param domain
   */
  public createDomain(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.put<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_CREATE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
      , domain)
      .subscribe({
        next: (result: DomainRo) => {
          this.notifyDomainEntryUpdated(result);
        }, error: (error: any) => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  /**
   * Update the domain SML integration data
   * @param domain Domain to update
   */
  public updateDomainSMLIntegrationData(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_UPDATE_SML_INTEGRATION
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      , domain)
      .subscribe({
        next: (result: DomainRo) => {
          this.notifyDomainEntryUpdated(result);
        }, error: (error: any) => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }

  /**
   * Update the domain resource types
   * @param domain Domain to update
   */
  public updateDomainResourceTypes(domain: DomainRo) {
    const currentUser: User = this.securityService.getCurrentUser();
    this.http.post<DomainRo>(SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_UPDATE_RESOURCE_TYPES
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId)
        .replace(SmpConstants.PATH_PARAM_ENC_DOMAIN_ID, domain.domainId)
      , domain.resourceDefinitions)
      .subscribe({
        next: (result: DomainRo) => {
          this.notifyDomainEntryUpdated(result);
        }, error: (error: any) => {
          this.alertService.error(error.error?.errorDescription)
        }
      });
  }


  notifyDomainsUpdated(res: DomainRo[]) {
    this.domainUpdateSubject.next(res);
  }

  notifyDomainEntryUpdated(res: DomainRo) {
    this.domainEntryUpdateSubject.next(res);
  }

  notifyPropertiesUpdated(domainRo: DomainRo, properties: DomainPropertyRo[]) {
    this.domainPropertyUpdateSubject.next(properties);
  }

  onDomainUpdatedEvent(): Observable<DomainRo[]> {
    return this.domainUpdateSubject.asObservable();
  }

  onDomainEntryUpdatedEvent(): Observable<DomainRo> {
    return this.domainEntryUpdateSubject.asObservable();
  }

  onDomainPropertyUpdatedEvent(): Observable<DomainPropertyRo[]> {
    return this.domainPropertyUpdateSubject.asObservable();
  }

}
