import {Component, OnInit, ViewChild} from '@angular/core';
import {AlertMessageService} from './alert-message.service';

@Component({
  moduleId: module.id,
  selector: 'alert',
  templateUrl: './alert-message.component.html',
  styleUrls: ['./alert-message.component.css']
})

export class AlertMessageComponent implements OnInit {
  @ViewChild('alertMessage') alertMessage;
  showSticky:boolean = false;
  message: any=null;

  readonly successTimeout: number = 3000;


  constructor(private alertService: AlertMessageService) { }

  ngOnInit() {
    this.alertService.getMessage().subscribe(message => { this.showMessage(message); });
  }

  clearAlert():void {
    this.alertService.clearAlert();
  }

  setSticky(sticky: boolean):void {
    this.showSticky = sticky;
  }

  get messageText(){
    if (!!this.message){
      return this.message.text;
    }
  }

  showMessage(message: any) {
    this.message = message;
    if (message?.type==='success') {
      setTimeout(() => {
        this.clearAlert();
      }, this.successTimeout);
    }

  }
}
