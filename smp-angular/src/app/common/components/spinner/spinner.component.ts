import { Component, Input } from '@angular/core';

@Component({
  selector: 'spinner',
  template:
    `<div *ngIf="show" style="z-index: 500;
    position:absolute; bottom:5px; top:5px; right: 5px; left: 5px;
       background-color: #9B9B9B88">
    <mat-progress-spinner style="position: relative;
    margin-left: 50%;
    margin-top: 25%;"
      [color]="'primary'"
      [mode]="'indeterminate'"
      [value]="'50'">
    </mat-progress-spinner>
  </div>
    `
})
export class SpinnerComponent {
  @Input() size: number = 25;
  @Input() show: boolean;


}
