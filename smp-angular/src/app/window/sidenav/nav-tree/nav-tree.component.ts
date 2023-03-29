import {Component} from "@angular/core";
import {NavigationModel, NavigationNode} from "../navigation-model.service";
import {NestedTreeControl} from "@angular/cdk/tree";


/**
 * @title Tree with nested nodes
 */
@Component({
  selector: "nav-tree",
  templateUrl: "nav-tree.component.html",
  styleUrls: ["nav-tree.component.scss"]
})
export class NavTree {
  treeControl = new NestedTreeControl<NavigationNode>(node => node.children);


  constructor(public navigationModel: NavigationModel) {

  }

  fullMenu: boolean = true;

  hasChild = (_: number, node: NavigationNode) =>
    !!node.children && node.children.length > 0;


  showExpandedMenu(expand: boolean) {
    this.fullMenu = expand;
  }

  menuClickHandler(node: NavigationNode) {

    this.treeControl.toggle(node);
    this.navigationModel.select(node);
  }

  isExpanded(node: NavigationNode) {
    this.treeControl.isExpanded(node)
  }


}
