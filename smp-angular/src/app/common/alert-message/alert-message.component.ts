import {Component, OnDestroy, OnInit} from '@angular/core';
import {AlertMessageService} from './alert-message.service';
import {Subscription} from "rxjs";


/**
 * This component is used to display alert messages/notifications on the top of the page in an overlay (growl).
 * In case of success messages, the message will be displayed for a certain amount of time and it will automatically disappear
 * unless sticky flat is set to true.
 *
 * The messages can be of different types: success, error, info, warning.
 */
@Component({
  selector: 'alert',
  templateUrl: './alert-message.component.html',
  styleUrls: ['./alert-message.component.css']
})
export class AlertMessageComponent implements OnInit, OnDestroy {

  message: any;

  private subscription: Subscription;

  constructor(private alertService: AlertMessageService) {
  }

  ngOnInit() {
    this.subscription = this.alertService.getMessage().subscribe(message => {
      this.showMessage(message);
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  clearAlert(force = false): void {
    this.alertService.clearAlert(force);
  }

  showMessage(message: any) {
    this.message = message;
    if (message && message.timeoutInSeconds && message.timeoutInSeconds > 0) {
      setTimeout(() => {
        this.clearAlert();
      }, this.message.timeoutInSeconds * 1000);
    }
  }
}
