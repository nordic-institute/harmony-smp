import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable, ReplaySubject} from 'rxjs';
import {Router} from '@angular/router';
import {SmpConfig} from './smp-config.model';
import {SmpConstants} from "../smp.constants";

@Injectable()
export class SmpConfigService {
  constructor(private http: HttpClient, private router: Router) {
  }

  getSmpInfo(): Observable<SmpConfig> {
    let subject = new ReplaySubject<SmpConfig>();
    this.http.get<SmpConfig>(SmpConstants.REST_CONFIG)
      .subscribe((res: SmpConfig) => {
        subject.next(res);
      }, error => {
        console.log("getSmpConfig:" + error);
      }
    );
    return subject.asObservable();
  }
}
