import { Component, Input } from '@angular/core';

@Component({
  selector: 'smp-label',
  template: '<div style="display:flex; flex-direction: row;gap:0.5em; align-items: center;">' +
    '<mat-icon *ngIf="icon">{{icon}}</mat-icon>' +
    '<span>{{label}}</span>' +
    '</div>'
})
export class SmpLabelComponent {
  @Input() label:string;
  @Input() icon:string;
}
