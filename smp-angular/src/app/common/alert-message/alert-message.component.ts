import {Component, OnDestroy, OnInit} from '@angular/core';
import {AlertMessageService} from './alert-message.service';
import {Subscription} from "rxjs";

@Component({
  selector: 'alert',
  templateUrl: './alert-message.component.html',
  styleUrls: ['./alert-message.component.css']
})

export class AlertMessageComponent implements OnInit, OnDestroy {

  readonly successTimeout: number = 3000;

  message: any;

  private subscription: Subscription;

  constructor(private alertService: AlertMessageService) {
  }

  ngOnInit() {
    this.subscription = this.alertService.getMessage().subscribe(message => { this.showMessage(message); });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  clearAlert(force = false):void {
    this.alertService.clearAlert(force);
  }

  showMessage(message: any) {
    this.message = message;
    if (message && message.type && message.type === 'success') {
      setTimeout(() => {
        this.clearAlert();
      }, this.successTimeout);
    }
  }
}
