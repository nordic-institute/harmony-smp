import {TranslateLoader} from "@ngx-translate/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {SmpConstants} from "../smp.constants";

/**
 * Custom translate loader that downloads the locale JSON files from the server
 */
export class TranslateHttpLoader implements TranslateLoader {
  constructor(private http: HttpClient) { }

  /**
   * Gets the translations from the server
   */
  public getTranslation(code: string): Observable<Object> {
    return this.http.get(`${SmpConstants.REST_PUBLIC_LOCALE}/${code}`);
  }
}
