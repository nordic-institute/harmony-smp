import {Injectable} from '@angular/core';
import {EMPTY, Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {HttpEventService} from './http-event.service';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';

// Angular CLI configuration thing.
export interface IRequestOptions {
  headers?: HttpHeaders;
  observe?: 'body';
  params?: HttpParams;
  reportProgress?: boolean;
  responseType?: 'json';
  withCredentials?: boolean;
  body?: any;
}

export function extendedHttpClientCreator(http: HttpClient, httpEventService: HttpEventService) {
  return new ExtendedHttpClient(http, httpEventService);
}

@Injectable()
export class ExtendedHttpClient {

  public constructor(private http: HttpClient, private httpEventService: HttpEventService) { }

  setOptions(options?: IRequestOptions): IRequestOptions {
    if (!options) {
      options = {};
    }
    if (!options.headers) {
      options.headers = new HttpHeaders();
    }
    return options;
  }

  /**
   * GET request
   * @param {string} endPoint it doesn't need / in front of the end point
   * @param {IRequestOptions} options options of the request like headers, body, etc.
   * @param {string} api use if there is needed to send request to different back-end than the default one.
   * @returns {Observable<T>}
   */
  public Get<T>(endPoint: string, options?: IRequestOptions): Observable<T> {
    options = this.setOptions(options);
    let response$ = this.http.get<T>(endPoint, options);
    return this.authenticate(response$);
  }

  /**
   * POST request
   * @param {string} endPoint end point of the api
   * @param {Object} params body of the request.
   * @param {IRequestOptions} options options of the request like headers, body, etc.
   * @returns {Observable<T>}
   */
  public Post<T>(endPoint: string, params: Object, options?: IRequestOptions): Observable<T> {
    options = this.setOptions(options);
    let response$ = this.http.post<T>(endPoint, params, options);
    return this.authenticate(response$);
  }

  /**
   * PUT request
   * @param {string} endPoint end point of the api
   * @param {Object} params body of the request.
   * @param {IRequestOptions} options options of the request like headers, body, etc.
   * @returns {Observable<T>}
   */
  public Put<T>(endPoint: string, params: Object, options?: IRequestOptions): Observable<T> {
    options = this.setOptions(options);
    let response$ = this.http.put<T>(endPoint, params, options);
    return this.authenticate(response$);
  }

  /**
   * DELETE request
   * @param {string} endPoint end point of the api
   * @param {IRequestOptions} options options of the request like headers, body, etc.
   * @returns {Observable<T>}
   */
  public Delete<T>(endPoint: string, options?: IRequestOptions): Observable<T> {
    options = this.setOptions(options);
    let response$ = this.http.delete<T>(endPoint, options);
    return this.authenticate(response$);
  }

  private authenticate<T>(response$: Observable<T>): Observable<T> {
    return response$.pipe(catchError(error => {
      if ((error.status === 403)) {
        console.log('The authentication session expires or the user is not authorised');
        this.httpEventService.requestForbiddenEvent(error);
        return EMPTY;
      }
      return throwError(error);
    }));
  }
}
