import {Injectable} from "@angular/core";

@Injectable()
export class WindowSpinnerService {
  private _showSpinner: boolean = false;

  get showSpinner(): boolean {
    return this._showSpinner;
  }

  set showSpinner(value: boolean) {
    this._showSpinner = value;
  }
}
