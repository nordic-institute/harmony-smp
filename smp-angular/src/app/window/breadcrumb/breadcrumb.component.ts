import {Component, OnInit} from '@angular/core';
import {NavigationModel, NavigationNode} from "../sidenav/navigation-model.service";


/**
 * Top page navigation bar  Breadcrumb-  side navigation panel of the DomiSMP. The component shows all tools/pages according to user role and permissions
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component({
  moduleId: module.id,
  selector: 'smp-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.css']
})
export class BreadcrumbComponent implements OnInit {

  constructor(public navigationModel: NavigationModel) {

  }

  ngOnInit(): void {
    console.log("BreadcrumbComponent onInit")

  }

  itemClickHandler(node: NavigationNode) {
    this.navigationModel.select(node);
  }

}
