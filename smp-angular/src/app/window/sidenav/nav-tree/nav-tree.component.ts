import {Component, ElementRef, HostListener, OnDestroy, QueryList, ViewChildren} from "@angular/core";
import {NavigationNode, NavigationService} from "../navigation-model.service";
import {NestedTreeControl} from "@angular/cdk/tree";
import {MatMenuTrigger} from "@angular/material/menu";
import {Subscription} from "rxjs";


/**
 * @title Tree with nested nodes
 */
@Component({
    selector: "nav-tree",
    templateUrl: "nav-tree.component.html",
    styleUrls: ["nav-tree.component.scss"],
    standalone: false
})
export class NavTree implements OnDestroy {
  @ViewChildren(MatMenuTrigger) menuTriggers: QueryList<MatMenuTrigger>;
  treeControl = new NestedTreeControl<NavigationNode>(node => node.children);

  private collapseTimeout: ReturnType<typeof setTimeout> | null = null;
  private selectedPathSub: Subscription;

  constructor(public navigationModel: NavigationService, private elementRef: ElementRef) {

    this.selectedPathSub = navigationModel.getSelectedPathObservable()
      .subscribe(selectedPath => {
        if (!selectedPath || selectedPath.length == 0) {
          return;
        }
        selectedPath.forEach(pathNode => this.treeControl.expand(pathNode));
      });
  }

  fullMenu: boolean = true;

  hasChild = (_: number, node: NavigationNode) =>
    !!node.children && node.children.length > 0;


  showExpandedMenu(expand: boolean) {
    if (this.collapseTimeout) {
      clearTimeout(this.collapseTimeout);
      this.collapseTimeout = null;
    }

    if (expand) {
      this.collapseTimeout = setTimeout(() => {
        this.fullMenu = true;
        this.collapseTimeout = null;
      }, 50);
    } else {
      this.collapseTimeout = setTimeout(() => {
        this.fullMenu = false;
        this.collapseTimeout = null;
      }, 400);
    }
  }

  ngOnDestroy() {
    this.selectedPathSub?.unsubscribe();
    if (this.collapseTimeout) {
      clearTimeout(this.collapseTimeout);
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.menuTriggers?.some(t => t.menuOpen)) return;
    const target = event.target as HTMLElement;
    const clickedInside = this.elementRef.nativeElement.contains(target);
    const clickedInOverlay = target.closest('.cdk-overlay-container');
    if (!clickedInside && !clickedInOverlay) {
      this.closeOtherMenus();
    }
  }

  closeOtherMenus(current?: MatMenuTrigger) {
    this.menuTriggers?.forEach(trigger => {
      if (trigger !== current) {
        trigger.closeMenu();
      }
    });
  }

  menuClickHandler(node: NavigationNode) {

    this.navigationModel.select(node);

  }

  isExpanded(node: NavigationNode) {
    this.treeControl.isExpanded(node)
  }


}
