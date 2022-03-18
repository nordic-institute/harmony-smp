import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {SmpConstants} from "../../smp.constants";
import {Observable} from "rxjs";
import {AccessTokenRo} from "../access-token-ro.model";
import {AlertService} from "../../alert/alert.service";

@Injectable()
export class UserDetailsService {

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
  ) { }

  isSamePreviousPasswordUsed$(userId: number, password: string): Observable<boolean> {
    return this.http.post<boolean>(`${SmpConstants.REST_USER}/${userId}/samePreviousPasswordUsed`, password);
  }

  regenerateAccessToken(userId: number, password: string):Promise<AccessTokenRo> {
    return this.http.post<AccessTokenRo>(`${SmpConstants.REST_USER}/${userId}/generate-access-token`,password).toPromise()

  }
}
