import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ReplaySubject} from 'rxjs';
import {Domain} from './domain.model';

@Injectable()
export class DomainService {

  static readonly CURRENT_DOMAIN_URL: string = 'rest/security/user/domain';
  static readonly DOMAIN_LIST_URL: string = 'rest/application/domains';

  private domainSubject: ReplaySubject<Domain>;

  constructor (private http: HttpClient) {
  }



  getCurrentDomain (): Observable<Domain> {
    /*
    if (!this.domainSubject) {
      this.domainSubject = new ReplaySubject<Domain>();
      this.http.get(DomainService.CURRENT_DOMAIN_URL).subscribe((res: HttpResponse<Domain>) => {
        this.domainSubject.next(res.body);
      }, (error: any) => {
        console.log('getCurrentDomain:' + error);
        this.domainSubject.next(null);
      });
    }
    return this.domainSubject.asObservable();
    */
    return null;
  }

  resetDomain (): void {
    if (this.domainSubject) {
      this.domainSubject.unsubscribe();
    }
    this.domainSubject = null;
  }

  getDomains (): Observable<Domain[]> {
    /*
    return this.http.get<Domain[]>(DomainService.DOMAIN_LIST_URL);
    */
    return null;
  }

  setCurrentDomain (domain: Domain) {
    return this.http.put(DomainService.CURRENT_DOMAIN_URL, domain.code).toPromise().then(() => {
      if (this.domainSubject) {
        this.domainSubject.next(domain);
      }
    });
  }

}
