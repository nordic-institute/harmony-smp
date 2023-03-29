import {Component, EventEmitter, Input, Output} from "@angular/core";
import {NavigationNode} from "../navigation-model.service";


@Component({
  selector: "nav-tree-menu",
  templateUrl: "nav-tree-menu.component.html",
})
export class NavTreeMenu {

  @Output() notifyClickMenu: EventEmitter<NavigationNode> = new EventEmitter();
  @Input() data: NavigationNode;
  @Input() trigger = "Trigger";
  @Input() isRootNode = false;

  constructor() {
  }


  triggerClickEvent() {
    this.notifyClickMenu.emit(this.data);
  }

}
