import { Component, Input } from '@angular/core';

@Component({
  selector: 'spinner',
  template:
    ` <div *ngIf="show">
          <span><i class="fa fa-spinner fa-spin" [ngStyle]="{'font-size': size+'px'}" aria-hidden="true"></i></span>
      </div>
    `
})
export class SpinnerComponent {
  @Input() size: number = 25;
  @Input() show: boolean;


}
