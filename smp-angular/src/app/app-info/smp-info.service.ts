import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import {Router} from "@angular/router";
import {HttpEventService} from "../http/http-event.service";
import {ReplaySubject} from "rxjs";
import {SmpInfo} from "./smp-info.model";

@Injectable()
export class SmpInfoService {
  constructor(private http: Http, private router: Router) {
  }

  getSmpInfo(): Observable<SmpInfo> {
    let subject = new ReplaySubject();
    this.http.get('rest/application/info')
      .map((response: Response) => {
        const smpInfo: SmpInfo = { version: response.json().version };
        return smpInfo;
      })
      .subscribe((res: SmpInfo) => {
        subject.next(res);
      }, (error: any) => {
        console.log("getSmpInfo:" + error);
        // subject.next(null);
      });
    return subject.asObservable();
  }

}
