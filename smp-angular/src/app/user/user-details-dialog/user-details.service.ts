import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SmpConstants} from "../../smp.constants";
import {Observable} from "rxjs";
import {AccessTokenRo} from "../../common/access-token-generation-dialog/access-token-ro.model";
import {AlertService} from "../../alert/alert.service";

@Injectable()
export class UserDetailsService {

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
  ) {
  }

  /**
   * Submits password to validate password
   * @param userId
   * @param password
   */
  changePassword(userId: string, newPassword: string, currentPassword: string): Observable<boolean> {
    return this.http.put<boolean>(SmpConstants.REST_PUBLIC_USER_CHANGE_PASSWORD.replace('{user-id}', userId),
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
    return this.http.post<AccessTokenRo>(SmpConstants.REST_PUBLIC_USER_GENERATE_ACCESS_TOKEN.replace('{user-id}', userId), password)
  }
}
