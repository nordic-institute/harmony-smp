import {Component, Input} from '@angular/core';

@Component({
  selector: 'tool-button-spacer',
  styleUrls: ['./spacer.component.css'],
  template:
    `<span [ngClass]="vertical?'vertical-spacer':'horizontal-spacer'">&nbsp;</span>
    `
})
export class SpacerComponent {
  @Input() vertical: boolean=true;
}
