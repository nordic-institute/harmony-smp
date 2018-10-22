import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable, ReplaySubject} from 'rxjs';
import {Router} from '@angular/router';
import {HttpEventService} from '../http/http-event.service';
import {SmpInfo} from './smp-info.model';
import {SmpConstants} from "../smp.constants";

@Injectable()
export class SmpInfoService {
  constructor(private http: HttpClient, private router: Router) {
  }

  getSmpInfo(): Observable<SmpInfo> {
    let subject = new ReplaySubject<SmpInfo>();
    this.http.get<SmpInfo>(SmpConstants.REST_APPLICATION)
      .subscribe((res: SmpInfo) => {
        subject.next(res);
      }, error => {
        console.log("getSmpInfo:" + error);
      }
    );
    return subject.asObservable();
  }

}
