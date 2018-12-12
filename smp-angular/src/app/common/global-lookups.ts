import {Injectable, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SearchTableResult} from "./search-table/search-table-result.model";
import {SmpConstants} from "../smp.constants";
import {Observable} from "rxjs/internal/Observable";
import {SecurityService} from "../security/security.service";
import {Role} from "../security/role.model";
import {AlertService} from "../alert/alert.service";
import {Subscription} from "rxjs/internal/Subscription";
import {SmpInfo} from "../app-info/smp-info.model";
import {ReplaySubject} from "rxjs/index";

/**
 * Purpose of object is to fetch lookups as domains and users
 */

@Injectable()
export class GlobalLookups implements OnInit {

  domainObserver: Observable<SearchTableResult>
  userObserver: Observable<SearchTableResult>
  certificateObserver: Observable<SearchTableResult>
  cachedDomainList: Array<any> = [];
  cachedServiceGroupOwnerList: Array<any> = [];
  cachedCertificateList: Array<any> = [];
  cachedCertificateAliasList: Array<String> = [];
  cachedApplicationInfo: SmpInfo;



  constructor(protected alertService: AlertService,  protected securityService: SecurityService, protected http: HttpClient) {
    this.refreshDomainLookup();
    this.refreshUserLookup();
    this.refreshCertificateLookup();
    this.refreshApplicationInfo();

  }

  ngOnInit() {

  }

  public refreshDomainLookup() {
    let params: HttpParams = new HttpParams()
      .set('page', '-1')
      .set('pageSize', '-1');
    // init domains
    this.domainObserver = this.http.get<SearchTableResult>(SmpConstants.REST_DOMAIN, {params});
    this.domainObserver.subscribe((domains: SearchTableResult) => {
      this.cachedDomainList = domains.serviceEntities.map(serviceEntity => {
        return {...serviceEntity}
      },
      (error:any) => {
          this.alertService.error("Error occurred while loading domain lookup [" + error + "].")
      });
    });
  }

  public refreshApplicationInfo() {

    this.http.get<SmpInfo>(SmpConstants.REST_APPLICATION)
      .subscribe((res: SmpInfo) => {
          this.cachedApplicationInfo = res;
        }, error => {
          console.log("getSmpInfo:" + error);
        }
      );

  }

  public refreshUserLookup() {
    // call only for authenticated users.
    if (this.securityService.isCurrentUserSMPAdmin() || this.securityService.isCurrentUserSystemAdmin() ) {
      let params: HttpParams = new HttpParams()
        .set('page', '-1')
        .set('pageSize', '-1');

      // return only smp and service group admins..
      if (this.securityService.isCurrentUserSMPAdmin() ) {
        params = params .set('roles', Role.SMP_ADMIN +","+Role.SERVICE_GROUP_ADMIN);
      }

      // init users
      this.userObserver = this.http.get<SearchTableResult>(SmpConstants.REST_USER, {params});
      let sub: Subscription = this.userObserver.subscribe((users: SearchTableResult) => {
        this.cachedServiceGroupOwnerList = users.serviceEntities.map(serviceEntity => {
          return {...serviceEntity}

        });
        sub.unsubscribe();
      },(error:any) => {
        // check if unauthorized
        // just console try latter
        sub.unsubscribe();
          console.log("Error occurred while loading user owners lookup [" + error + "]");
        });
    }

  }

  public refreshCertificateLookup() {
    // call only for authenticated users.
    if ( this.securityService.isCurrentUserSystemAdmin() ) {

      // init users
      this.certificateObserver = this.http.get<SearchTableResult>(SmpConstants.REST_KEYSTORE );
      this.certificateObserver.subscribe((certs: SearchTableResult) => {
        this.cachedCertificateList = certs.serviceEntities.map(serviceEntity => {
          return {...serviceEntity}

        });
        //update alias list
        this.cachedCertificateAliasList =this.cachedCertificateList.map(cert => cert.alias);
      },(error:any) => {
        // check if unauthorized
        // just console try latter
        console.log("Error occurred while loading user owners lookup [" + error + "]");
      });
    }
  }




}
