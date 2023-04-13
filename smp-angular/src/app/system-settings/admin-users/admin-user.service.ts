import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SecurityService} from "../../security/security.service";
import {SearchTableResult} from "../../common/search-table/search-table-result.model";
import {User} from "../../security/user.model";
import {TableResult} from "../../common/model/table-result.model";
import {MemberRo} from "../../common/model/member-ro.model";
import {SmpConstants} from "../../smp.constants";
import {UserRo} from "../user/user-ro.model";


@Injectable()
export class AdminUserService {


  constructor(
    private http: HttpClient,
    private securityService: SecurityService) {
  }

  getUsersObservable(filter: string, page: number, pageSize: number): Observable<SearchTableResult> {
    const currentUser: User = this.securityService.getCurrentUser();

    let params: HttpParams = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString())
      .set('filter', !filter ? "" : filter);

    return this.http.get<TableResult<MemberRo>>(SmpConstants.INTERNAL_USER_MANAGE_SEARCH
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, currentUser.userId), {params});
  }

  getUserDataObservable(userId: string): Observable<UserRo> {
    let user = this.securityService.getCurrentUser();
    if (!user) {
      return null;
    }
    return this.http.get<UserRo>(SmpConstants.REST_INTERNAL_USER_MANAGE_DATA
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID, userId));
  }

  updateManagedUser(managedUser: UserRo): Observable<UserRo> {
    let user = this.securityService.getCurrentUser();
    if (!user) {
      return null;
    }
    return this.http.post<UserRo>(SmpConstants.REST_INTERNAL_USER_MANAGE_UPDATE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID, managedUser.userId), managedUser);
  }

  createManagedUser(managedUser: UserRo): Observable<UserRo> {
    let user = this.securityService.getCurrentUser();
    if (!user) {
      return null;
    }
    return this.http.put<UserRo>(SmpConstants.REST_INTERNAL_USER_MANAGE_CREATE
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      , managedUser);
  }

  deleteManagedUser(managedUser: UserRo): Observable<UserRo> {
    let user = this.securityService.getCurrentUser();
    if (!user) {
      return null;
    }
    return this.http.delete<UserRo>(SmpConstants.REST_INTERNAL_USER_MANAGE_DELETE
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, user.userId)
      .replace(SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID, managedUser.userId));
  }
}
