import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SmpConstants} from "../../smp.constants";
import {Observable} from "rxjs";
import {AccessTokenRo} from "../../common/dialogs/access-token-generation-dialog/access-token-ro.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {UserRo} from "../user-ro.model";

@Injectable()
export class UserDetailsService {

  constructor(
    private http: HttpClient,
    private alertService: AlertMessageService,
  ) {
  }

  /**
   * Submits password to validate password
   * @param userId
   * @param password
   */
  changePassword(userId: string, newPassword: string, currentPassword: string): Observable<boolean> {
    return this.http.put<boolean>(SmpConstants.REST_PUBLIC_USER_CHANGE_PASSWORD
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, userId),
      {
          currentPassword:currentPassword,
          newPassword:newPassword
      });
  }

  changePasswordAdmin(userId: string,  updateUserId: string, newPassword: string, currentPassword: string): Observable<UserRo> {
    return this.http.put<UserRo>(SmpConstants.REST_INTERNAL_USER_CHANGE_PASSWORD
        .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, userId)
        .replace(SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID, updateUserId),
      {
        currentPassword:currentPassword,
        newPassword:newPassword
      });
  }


  /**
   * Submit request to regenerated request token!
   * @param userId
   * @param password - password to authenticate user before regenerating the access token.
   */
  regenerateAccessToken(userId: string, password: string): Observable<AccessTokenRo> {
    return this.http.post<AccessTokenRo>(SmpConstants.REST_PUBLIC_USER_GENERATE_ACCESS_TOKEN
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, userId), password)
  }

  regenerateAccessTokenAdmin(userId: string, password: string, updateUserId: string): Observable<AccessTokenRo> {
    return this.http.post<AccessTokenRo>(SmpConstants.REST_INTERNAL_USER_GENERATE_ACCESS_TOKEN
      .replace(SmpConstants.PATH_PARAM_ENC_USER_ID, userId)
      .replace(SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID, updateUserId), password)
  }
}
