import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {HttpResponse} from '@angular/common/http';

@Injectable()
export class HttpEventService {

  private forbiddenResponseSubject = new Subject<any>();

  onForbiddenResponse$(): Observable<any> {
    return this.forbiddenResponseSubject.asObservable();
  }

  notifyForbiddenResponse(response) {
    this.forbiddenResponseSubject.next(response);
  }
}
