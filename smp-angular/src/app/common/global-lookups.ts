import {Injectable} from '@angular/core';
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
import {DateAdapter} from "@angular/material/core";
import {NgxMatDateAdapter} from "@angular-material-components/datetime-picker";

/**
 * Purpose of object is to fetch lookups as domains and users
 */

@Injectable()
export class GlobalLookups {

  domainObserver: Observable<SearchTableResult>
  userObserver: Observable<SearchTableResult>
  certificateObserver: Observable<SearchTableResult>
  trustedCertificateObserver: Observable<SearchTableResult>

  cachedDomainList: Array<any> = [];
  cachedServiceGroupOwnerList: Array<any> = [];
  cachedCertificateList: Array<any> = [];
  cachedCertificateAliasList: Array<string> = [];
  cachedApplicationInfo: SmpInfo;
  cachedApplicationConfig?: SmpConfig;

  // lookup refresh subscriptions.

  constructor(protected alertService: AlertMessageService,
              protected securityService: SecurityService,
              protected http: HttpClient,
              private securityEventService: SecurityEventService,
              private dateAdapter: DateAdapter<Date>,
              private ngxMatDateAdapter: NgxMatDateAdapter<Date>
  ) {
    this.refreshApplicationInfo();
    this.refreshDomainLookupFromPublic();
    this.securityService.refreshLoggedUserFromServer();

    securityEventService.onLoginSuccessEvent().subscribe(user => {
        this.refreshLookupsOnLogin();
        // set locale
        if (!!user && user.smpLocale) {
          dateAdapter.setLocale(user.smpLocale);
          ngxMatDateAdapter.setLocale(user.smpLocale);
        }
      }
    );

    securityEventService.onLogoutSuccessEvent().subscribe(value => {
        this.clearCachedLookups();
      }
    );
    // set default locale
    dateAdapter.setLocale('fr');
    ngxMatDateAdapter.setLocale('fr');

  }

  public refreshLookupsOnLogin() {
    this.refreshCertificateLookup();
    this.refreshApplicationInfo();
    this.refreshApplicationConfiguration();
  }

  public refreshDomainLookupFromPublic() {
    let domainUrl = SmpConstants.REST_PUBLIC_DOMAIN_SEARCH;
    this.refreshDomainLookup(domainUrl);
  }

  public refreshDomainLookupForLoggedUser() {
    let domainUrl = SmpConstants.REST_PUBLIC_DOMAIN_SEARCH;
    // for authenticated admin use internal url which returns more data!
    if (this.securityService.isCurrentUserSystemAdmin()) {
      domainUrl = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_DEPRECATED;
    }
    this.refreshDomainLookup(domainUrl);
  }

  public refreshDomainLookup(domainUrl: string) {
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
        this.http.get<SmpConfig>(SmpConstants.REST_PUBLIC_APPLICATION_CONFIG)
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

  public clearCachedLookups() {
    this.cachedCertificateList = [];
    this.cachedServiceGroupOwnerList = [];
    this.cachedApplicationConfig = null;
    this.cachedDomainList = [];
  }

  public refreshCertificateLookup() {
    // call only for authenticated users.
    if (this.securityService.isCurrentUserSystemAdmin()) {

      // init users
      this.certificateObserver = this.http.get<SearchTableResult>(SmpConstants.REST_INTERNAL_KEYSTORE_DEPRECATED);
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


}
