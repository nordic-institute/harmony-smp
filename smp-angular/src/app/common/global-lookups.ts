import {Injectable, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {DomainRo} from "../domain/domain-ro.model";
import {SearchTableResult} from "./search-table/search-table-result.model";
import {SmpConstants} from "../smp.constants";
import {Observable} from "rxjs/internal/Observable";
import {UserRo} from "../user/user-ro.model";
import {SecurityService} from "../security/security.service";

/**
 * Purpose of object is to fetch lookups as domains and users
 */

@Injectable()
export class GlobalLookups  implements OnInit {

  domainObserver:  Observable< SearchTableResult>
  userObserver:  Observable< SearchTableResult>
  cachedDomainList: Array<any> = [];
  cachedUserList: Array<any> = [];


  constructor(protected securityService: SecurityService,  protected http: HttpClient){
    this.refreshDomainLookup();
    this.refreshUserLookup();
  }

  ngOnInit() {
  }

  public refreshDomainLookup(){
    let params: HttpParams = new HttpParams()
      .set('page', '-1')
      .set('pageSize', '-1');
    // init domains
    this.domainObserver = this.http.get<SearchTableResult>(SmpConstants.REST_DOMAIN,{params});
    this.domainObserver.subscribe((domains: SearchTableResult) => {
      let gotList = new Array(domains.serviceEntities.length)
        .map((v, index) => domains.serviceEntities[index] as DomainRo);
      this.cachedDomainList = domains.serviceEntities.map(serviceEntity => {
        return {...serviceEntity}

      });
    });
  }

  public refreshUserLookup(){
    // call service if authenticated
    if (this.securityService.isAuthenticated(false)) {
      let params: HttpParams = new HttpParams()
        .set('page', '-1')
        .set('pageSize', '-1');
      // init users
      this.userObserver = this.http.get<SearchTableResult>(SmpConstants.REST_USER, {params});
      this.userObserver.subscribe((users: SearchTableResult) => {
        let gotList = new Array(users.serviceEntities.length)
          .map((v, index) => users.serviceEntities[index] as UserRo);
        this.cachedUserList = users.serviceEntities.map(serviceEntity => {
          return {...serviceEntity}

        });
      });
    }
  }




}
