import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {NavigationNode} from "../navigation-model.service";
import {TranslateService} from "@ngx-translate/core";


@Component({
  selector: "nav-tree-menu",
  templateUrl: "nav-tree-menu.component.html",
})
export class NavTreeMenu implements OnInit {

  @Output() notifyClickMenu: EventEmitter<NavigationNode> = new EventEmitter();
  @Input() data: NavigationNode;
  @Input() trigger = "Trigger";
  @Input() isRootNode = false;

  name = "";

  constructor(public translateService: TranslateService) {
  }

  triggerClickEvent() {
    this.notifyClickMenu.emit(this.data);
  }

  ngOnInit() {
    console.log(1, this.data.i18n)
    this.translateService.get(this.data.i18n).subscribe(value => this.name = value);
  }

  get isLeaf() {
    return !this.data.children || this.data.children.length == 0
  }

}
