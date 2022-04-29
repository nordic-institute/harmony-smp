import {Injectable, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SearchTableResult} from "./search-table/search-table-result.model";
import {SmpConstants} from "../smp.constants";
import {Observable} from "rxjs/internal/Observable";
import {SecurityService} from "../security/security.service";
import {Role} from "../security/role.model";
import {AlertMessageService} from "./alert-message/alert-message.service";
import {Subscription} from "rxjs/internal/Subscription";
import {SmpInfo} from "../app-info/smp-info.model";
import {SmpConfig} from "../app-config/smp-config.model";
import {SecurityEventService} from "../security/security-event.service";

/**
 * Purpose of object is to fetch lookups as domains and users
 */

@Injectable()
export class GlobalLookups implements OnInit {

  domainObserver: Observable<SearchTableResult>
  userObserver: Observable<SearchTableResult>
  certificateObserver: Observable<SearchTableResult>
  trustedCertificateObserver: Observable<SearchTableResult>

  cachedDomainList: Array<any> = [];
  cachedServiceGroupOwnerList: Array<any> = [];
  cachedCertificateList: Array<any> = [];
  cachedCertificateAliasList: Array<String> = [];
  cachedApplicationInfo: SmpInfo;
  cachedApplicationConfig: SmpConfig;
  cachedTrustedCertificateList: Array<any> = [];

  loginSubscription: Subscription;
  logoutSubscription: Subscription;


  constructor(protected alertService: AlertMessageService,
              protected securityService: SecurityService,
              protected http: HttpClient,
              private securityEventService: SecurityEventService) {
    securityService.refreshLoggedUserFromServer();
    this.refreshDomainLookup();
    this.refreshCertificateLookup();
    this.refreshApplicationInfo();
    this.refreshApplicationConfiguration();
    this.refreshTrustedCertificateLookup();
  }

  ngOnInit() {
  }

  public refreshDomainLookup() {
    let domainUrl = SmpConstants.REST_PUBLIC_DOMAIN_SEARCH;
    // for authenticated admin use internal url which returns more data!
    if (this.securityService.isCurrentUserSMPAdmin() || this.securityService.isCurrentUserSystemAdmin()) {
      domainUrl = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE;
    }
    let params: HttpParams = new HttpParams()
      .set('page', '-1')
      .set('pageSize', '-1');
    // init domains
    this.domainObserver = this.http.get<SearchTableResult>(domainUrl, {params});
    this.domainObserver.subscribe((domains: SearchTableResult) => {
      this.cachedDomainList = domains.serviceEntities.map(serviceEntity => {
          return {...serviceEntity}
        },
        (error: any) => {
          this.alertService.error("Error occurred while loading domain lookup [" + error + "].")
        });
    });
  }

  public refreshApplicationInfo() {

    this.http.get<SmpInfo>(SmpConstants.REST_PUBLIC_APPLICATION_INFO)
      .subscribe((res: SmpInfo) => {
          this.cachedApplicationInfo = res;
        }, error => {
          console.log("getSmpInfo:" + error);
        }
      );

  }

  public refreshApplicationConfiguration() {
    console.log("Refresh application configuration ")
    // check if authenticated
    this.securityService.isAuthenticated(false).subscribe((isAuthenticated: boolean) => {
      console.log("Refresh application configuration is authenticated " + isAuthenticated)
      if (isAuthenticated) {
        this.http.get<SmpConfig>(SmpConstants.REST_INTERNAL_APPLICATION_CONFIG)
          .subscribe((res: SmpConfig) => {
              this.cachedApplicationConfig = res;
            }, error => {
              console.log("getSmpConfig:" + error);
            }
          );
      }
    });
  }

  public refreshUserLookup() {
    // call only for authenticated users.
    if (this.securityService.isCurrentUserSMPAdmin() || this.securityService.isCurrentUserSystemAdmin()) {
      let params: HttpParams = new HttpParams()
        .set('page', '-1')
        .set('pageSize', '-1');

      // return only smp and service group admins..
      if (this.securityService.isCurrentUserSMPAdmin()) {
        params = params.set('roles', Role.SMP_ADMIN + "," + Role.SERVICE_GROUP_ADMIN);
      }

      // retrieve user list
      this.userObserver = this.http.get<SearchTableResult>(SmpConstants.REST_INTERNAL_USER_MANAGE, {params});
      let sub: Subscription = this.userObserver.subscribe((users: SearchTableResult) => {
        this.cachedServiceGroupOwnerList = users.serviceEntities.map(serviceEntity => {
          return {...serviceEntity}

        });
        sub.unsubscribe();
      }, (error: any) => {
        // check if unauthorized
        // just console try latter
        sub.unsubscribe();
        console.log("Error occurred while loading user owners lookup [" + error + "]");
      });
    }

  }

  public refreshCertificateLookup() {
    // call only for authenticated users.
    if (this.securityService.isCurrentUserSystemAdmin()) {

      // init users
      this.certificateObserver = this.http.get<SearchTableResult>(SmpConstants.REST_INTERNAL_KEYSTORE);
      this.certificateObserver.subscribe((certs: SearchTableResult) => {
        this.cachedCertificateList = certs.serviceEntities.map(serviceEntity => {
          return {...serviceEntity}
        });
        //update alias list
        this.cachedCertificateAliasList = this.cachedCertificateList.map(cert => cert.alias);
      }, (error: any) => {
        // check if unauthorized
        // just console try latter
        console.log("Error occurred while loading user owners lookup [" + error + "]");
      });
    }
  }


  public refreshTrustedCertificateLookup() {
    // call only for authenticated users.
    if (this.securityService.isCurrentUserSystemAdmin()) {

      // init users
      this.trustedCertificateObserver = this.http.get<SearchTableResult>(SmpConstants.REST_INTERNAL_TRUSTSTORE);
      this.trustedCertificateObserver.subscribe((certs: SearchTableResult) => {
        this.cachedTrustedCertificateList = certs.serviceEntities.map(serviceEntity => {
          return {...serviceEntity}

        });
      }, (error: any) => {
        // check if unauthorized
        // just console try latter
        console.log("Error occurred while loading trusted certifcates lookup [" + error + "]");
      });
    }
  }
}
