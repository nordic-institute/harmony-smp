import { Component, Input } from '@angular/core';

@Component({
  selector: 'smp-warning-panel',
  template: '<div class="error-data-panel" >' +
    '<mat-icon *ngIf="icon">{{icon}}</mat-icon>' +
    '<span>{{label}}</span>' +
    '</div>'
})
export class SmpWarningPanelComponent {
  @Input() label:string;
  @Input() icon:string;
}
