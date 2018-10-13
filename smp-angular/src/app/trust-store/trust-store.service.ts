import {HttpClient, HttpResponse} from '@angular/common/http';
import {AlertService} from 'app/alert/alert.service';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {TrustStoreEntry} from './trust-store-entry.model';
import {catchError} from 'rxjs/operators';

/**
 * @Author Dussart Thomas
 */
@Injectable()
export class TrustStoreService {

  url = "rest/truststore";

  constructor(private http: HttpClient, private alertService: AlertService) {
  }

  getEntries(): Observable<TrustStoreEntry[]> {
    return this.http.get<TrustStoreEntry[]>(this.url + '/list')
      .pipe(catchError(err => this.handleError(err)));
  }

  saveTrustStore(file, password): Observable<string> {
    let input = new FormData();
    input.append('truststore', file);
    input.append('password', password);
    return this.http.post<string>(this.url + '/save', input);
  }

  private handleError(error) {
    this.alertService.error(error, false);
    let errMsg: string;
    if (error instanceof HttpResponse) {
      const body = error || '';
      const err = body['error'] || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    console.error(errMsg);
    return Promise.reject(errMsg);
  }
}
