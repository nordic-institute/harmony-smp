import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';
import {HttpResponse} from '@angular/common/http';

@Injectable()
export class HttpEventService extends Subject<any> {
    constructor() {
        super();
    }

    requestForbiddenEvent(error: HttpResponse<any>) {
        if(error) {
            super.next(error);
        }
    }
}
