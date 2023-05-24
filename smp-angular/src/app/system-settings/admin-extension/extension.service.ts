import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SmpConstants} from "../../smp.constants";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {SecurityService} from "../../security/security.service";
import {Observable, Subject} from "rxjs";
import {ExtensionRo} from "./extension-ro.model";

/**
 * Class handle extensions... ,
 */
@Injectable()
export class ExtensionService {
  private extensionsUpdateSubject = new Subject<ExtensionRo[]>();

  constructor(
    private http: HttpClient,
    private securityService: SecurityService,
    private alertService: AlertMessageService,
  ) {
  }


  getExtensions() {
    this.http.get<ExtensionRo[]>(SmpConstants.REST_INTERNAL_EXTENSION_MANAGE)
      .subscribe((response: ExtensionRo[]) => {
        this.notifyExtensionsUpdated(response)
      }, error => {
        this.alertService.error(error.error?.errorDescription)
      });
  }

  // notification event methods
  notifyExtensionsUpdated(res: ExtensionRo[]) {
    this.extensionsUpdateSubject.next(res);
  }

  // Observables for registering the observers
  onExtensionsUpdatesEvent(): Observable<ExtensionRo[]> {
    return this.extensionsUpdateSubject.asObservable();
  }
}
