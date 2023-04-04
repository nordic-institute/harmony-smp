import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NavigationNode} from "../../sidenav/navigation-model.service";


/**
 * Top page navigation bar  Breadcrumb-  side navigation panel of the DomiSMP. The component shows all tools/pages according to user role and permissions
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component({
  moduleId: module.id,
  selector: 'smp-breadcrumb-item',
  templateUrl: './breadcrumb-item.component.html',
  styleUrls: ['./breadcrumb-item.component.scss']
})

export class BreadcrumbItemComponent {
  @Output() onClickEvent: EventEmitter<NavigationNode> = new EventEmitter();
  @Input() value : NavigationNode;


  constructor() {
  }

  get icon(){
    return this.value.icon;
  }
  get name(){
    return this.value.name;
  }

  get description(){
    return this.value.code;
  }

  triggerClickEvent() {
    this.onClickEvent.emit(this.value);
  }

}
