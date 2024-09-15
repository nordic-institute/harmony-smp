import {Injectable} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {NavigationService} from "../../window/sidenav/navigation-model.service";
import {AlertMessageService} from "../alert-message/alert-message.service";

@Injectable()
export class HttpErrorHandlerService {

  constructor (private navigationService: NavigationService,
               private alertMessageService: AlertMessageService) {
  }

  public logoutOnInvalidSessionError(err: any): boolean {
    if (err instanceof HttpErrorResponse) {
      if (err.status === 401) {
        this.navigationService.navigateToLogin();
        this.alertMessageService.error("You have been logged out because of inactivity or missing access permissions.")
        return true;
      }
    }
    return false;
  }

  public handleHttpError(err: any) {
    if (err instanceof HttpErrorResponse) {
      if (this.logoutOnInvalidSessionError(err)) {
        return;
      }
      if (err.status === 0) {
        this.alertMessageService.error("Server is not reachable. Please try again later.");
      } else {
        this.alertMessageService.error(err.error.errorDescription);
      }
    } else {
      this.alertMessageService.error(err.error?.errorDescription);
    }

  }
}
