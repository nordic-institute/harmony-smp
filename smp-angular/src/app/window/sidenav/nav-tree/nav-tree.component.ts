import {Component} from "@angular/core";
import {NavigationService, NavigationNode} from "../navigation-model.service";
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


  constructor(public navigationModel: NavigationService) {

    navigationModel.getSelectedPathObservable()
      .subscribe( selectedPath => {
        if (!selectedPath || selectedPath.length == 0) {
          return;
        }
        this.treeControl.collapseAll();
        selectedPath.forEach(pathNode => this.treeControl.expand(pathNode));
      });
  }

  fullMenu: boolean = true;

  hasChild = (_: number, node: NavigationNode) =>
    !!node.children && node.children.length > 0;


  showExpandedMenu(expand: boolean) {
    this.fullMenu = expand;
  }

  menuClickHandler(node: NavigationNode) {

    this.navigationModel.select(node);

  }
  isExpanded(node: NavigationNode) {
    this.treeControl.isExpanded(node)
  }


}
