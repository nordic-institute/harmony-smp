import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {DomainRo} from "../domain/domain-ro.model";
import {SearchTableResult} from "./search-table/search-table-result.model";
import {SmpConstants} from "../smp.constants";
import {Observable} from "rxjs/internal/Observable";
import {UserRo} from "../user/user-ro.model";

/**
 * Purpose of object is to fetch lookups as domains and users
 */

@Injectable()
export class GlobalLookups  implements OnInit {

  domainObserver:  Observable< SearchTableResult>
  userObserver:  Observable< SearchTableResult>
  cachedDomainList: Array<any> = [];
  cachedUserList: Array<any> = [];


  constructor(protected http: HttpClient){
    this.refreshDomainLookup();
    this.refreshUserLookup();
  }

  ngOnInit() {
  }

  public refreshDomainLookup(){
    // init domains
    this.domainObserver = this.http.get<SearchTableResult>(SmpConstants.REST_DOMAIN);
    this.domainObserver.subscribe((domains: SearchTableResult) => {
      let gotList = new Array(domains.serviceEntities.length)
        .map((v, index) => domains.serviceEntities[index] as DomainRo);
      this.cachedDomainList = domains.serviceEntities.map(serviceEntity => {
        return {...serviceEntity}

      });
    });
  }

  public refreshUserLookup(){
    // init users
    this.userObserver = this.http.get<SearchTableResult>(SmpConstants.REST_USER);
    this.userObserver.subscribe((users: SearchTableResult) => {
      let gotList = new Array(users.serviceEntities.length)
        .map((v, index) => users.serviceEntities[index] as UserRo);
      this.cachedUserList = users.serviceEntities.map(serviceEntity => {
        return {...serviceEntity}

      });
    });
  }




}
