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
import {DomainRo} from "./model/domain-ro.model";
import {Subject} from "rxjs";
import {
  FormatWidth,
  getLocaleDateFormat,
  getLocaleDateTimeFormat,
  getLocaleTimeFormat
} from "@angular/common";
import StringUtils from "./utils/string-utils";

/**
 * Purpose of object is to fetch lookups as domains and users
 */

@Injectable()
export class GlobalLookups {
  // global data observers. The components will subscribe to these Subject to get
  // data updates.
  private smpInfoUpdateSubject: Subject<SmpInfo> = new Subject<SmpInfo>();
  private readonly DEFAULT_LOCALE: string = 'fr';

  domainObserver: Observable<SearchTableResult>
  userObserver: Observable<SearchTableResult>
  cachedDomainList: Array<DomainRo> = [];
  cachedServiceGroupOwnerList: Array<any> = [];
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
    dateAdapter.setLocale(this.DEFAULT_LOCALE);
    ngxMatDateAdapter.setLocale(this.DEFAULT_LOCALE);

  }

  public refreshLookupsOnLogin() {
    this.refreshApplicationInfo();
    this.refreshApplicationConfiguration();
  }

  public refreshDomainLookupFromPublic() {
    let domainUrl = SmpConstants.REST_PUBLIC_DOMAIN;
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

  getCurrentLocale(): string {
    if (this.securityService.getCurrentUser() == null) {
      return this.DEFAULT_LOCALE;
    }
    return this.securityService.getCurrentUser().smpLocale;
  }

  public getDateTimeFormat(withSeconds: boolean = true): string {
    let locale = this.getCurrentLocale();
    locale = locale ? locale : this.DEFAULT_LOCALE;
    let format: string = getLocaleDateTimeFormat(locale, FormatWidth.Short);
    let fullTime = getLocaleTimeFormat(locale,withSeconds? FormatWidth.Medium:FormatWidth.Short);
    let fullDate = getLocaleDateFormat(locale, FormatWidth.Short);
    let result = StringUtils.format(format, [fullTime, fullDate]);
    return result;
  }

  private format(str, opt_values) {
    if (opt_values) {
      str = str.replace(/\{([^}]+)}/g, function (match, key) {
        return (opt_values != null && key in opt_values) ? opt_values[key] : match;
      });
    }
    return str;
  }

  public refreshApplicationInfo() {

    this.http.get<SmpInfo>(SmpConstants.REST_PUBLIC_APPLICATION_INFO)
      .subscribe({
        next: (res: SmpInfo): void => {
          this.cachedApplicationInfo = res;
          this.smpInfoUpdateSubject.next(res);
        },
        error: (err: any): void => {
          console.log("getSmpInfo:" + err);
        }
      });

  }

  public refreshApplicationConfiguration() {
    console.log("Refresh application configuration ")
    // check if authenticated
    this.securityService.isAuthenticated(false).subscribe((isAuthenticated: boolean) => {
      console.log("Refresh application configuration is authenticated " + isAuthenticated)
      if (isAuthenticated) {
        this.http.get<SmpConfig>(SmpConstants.REST_PUBLIC_APPLICATION_CONFIG)
          .subscribe({
            next: (res: SmpConfig): void => {
              this.cachedApplicationConfig = res;
            },
            error: (err: any) => {
              console.log("getSmpConfig:" + err);
            }
          });
      }
    });
  }

  public refreshUserLookup() {
    // call only for authenticated users.
    if (this.securityService.isCurrentUserSMPAdmin() || this.securityService.isCurrentUserSystemAdmin()) {
      let params: HttpParams = new HttpParams()
        .set('page', '-1')
        .set('pageSize', '-1');

      // return only smp and resource admins...
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
    this.cachedServiceGroupOwnerList = [];
    this.cachedApplicationConfig = null;
    this.cachedDomainList = [];
  }

  public onSmpInfoUpdateEvent(): Observable<SmpInfo> {
    return this.smpInfoUpdateSubject.asObservable();
  }
}
