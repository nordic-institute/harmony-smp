import {Injectable, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SearchTableResult} from "./search-table/search-table-result.model";
import {SmpConstants} from "../smp.constants";
import {Observable} from "rxjs/internal/Observable";
import {UserRo} from "../user/user-ro.model";
import {SecurityService} from "../security/security.service";
import {Role} from "../security/role.model";
import {AlertService} from "../alert/alert.service";

/**
 * Purpose of object is to fetch lookups as domains and users
 */

@Injectable()
export class GlobalLookups implements OnInit {

  domainObserver: Observable<SearchTableResult>
  userObserver: Observable<SearchTableResult>
  cachedDomainList: Array<any> = [];
  cachedServiceGroupOwnerList: Array<any> = [];


  constructor(protected alertService: AlertService,  protected securityService: SecurityService, protected http: HttpClient) {
    this.refreshDomainLookup();
    this.refreshUserLookup();
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
      this.userObserver.subscribe((users: SearchTableResult) => {
        this.cachedServiceGroupOwnerList = users.serviceEntities.map(serviceEntity => {
          return {...serviceEntity}

        });
      },(error:any) => {
        // check if unauthorized
        // just console try latter
          console.log("Error occurred while loading user owners lookup [" + error + "]");
        });
    }
  }


}
