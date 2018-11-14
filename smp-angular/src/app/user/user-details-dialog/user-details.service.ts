import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {SmpConstants} from "../../smp.constants";
import {Observable} from "rxjs";

@Injectable()
export class UserDetailsService {

  constructor(
    private http: HttpClient,
  ) { }

  isSamePreviousPasswordUsed$(userId: number, password: string): Observable<boolean> {
    return this.http.post<boolean>(`${SmpConstants.REST_USER}/${userId}/samePreviousPasswordUsed`, password);
  }
}
