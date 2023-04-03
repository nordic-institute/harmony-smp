import {
  Component, Input, TemplateRef,
} from '@angular/core';



@Component({
  selector: 'data-panel',
  templateUrl: './data-panel.component.html',
  styleUrls: ['./data-panel.component.scss']
})
export class DataPanelComponent {

  @Input() title: String;
  @Input() text: String;

  @Input() labelColumnContent: TemplateRef<any>;




  constructor() {

  }

}
