import {Component, Input} from '@angular/core';

@Component({
    selector: 'smp-warning-panel',
    templateUrl: './smp-warning-panel.component.html',
    standalone: false
})
export class SmpWarningPanelComponent {
  @Input() padding: boolean = true;
  @Input() label: string;
  @Input() htmlContent: string;
  @Input() icon: string;
  @Input() type: string = 'warning';
}
