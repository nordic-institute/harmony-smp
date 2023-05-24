import {
  Component, Input, TemplateRef,
} from '@angular/core';



@Component({
  selector: 'data-panel',
  templateUrl: './data-panel.component.html',
  styleUrls: ['./data-panel.component.scss']
})
export class DataPanelComponent {

  @Input() title: string;
  @Input() showTitle: boolean=true;
  @Input() text: string;
  @Input() labelColumnContent: TemplateRef<any>;

}
