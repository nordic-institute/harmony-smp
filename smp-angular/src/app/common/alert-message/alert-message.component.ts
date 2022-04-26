import {Component, OnInit} from '@angular/core';
import {AlertMessageService} from './alert-message.service';

@Component({
  moduleId: module.id,
  selector: 'alert',
  templateUrl: './alert-message.component.html',
  styleUrls: ['./alert-message.component.css']
})

export class AlertMessageComponent implements OnInit {
  message: any;

  public static readonly MAX_COUNT_CSV: number = 10000;

  constructor(private alertService: AlertMessageService) { }

  ngOnInit() {
    this.alertService.getMessage().subscribe(message => { this.message = message; });
  }

  clearAlert():void {
    this.alertService.clearAlert();
  }
}
