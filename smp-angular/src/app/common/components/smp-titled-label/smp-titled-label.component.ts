import { Component, Input } from '@angular/core';

@Component({
  selector: 'smp-titled-label',
  styleUrls: ['./smp-titled-label.component.css'],
  template: '<div class="smp-titled-label">' +
    '  <div class="smp-tl-title " >' +
    '     <mat-icon *ngIf="icon">{{icon}}</mat-icon>' +
    '    <span>{{title}}</span>' +
    '  </div>' +
    '  <div class="smp-tl-value">{{value}}</div>' +
    '</div>'
})
export class SmpTitledLabelComponent {
  @Input() title:string;
  @Input() icon:string;
  @Input() value:string;
}
