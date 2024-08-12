import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NavigationNode} from "../../sidenav/navigation-model.service";
import {TranslateService} from "@ngx-translate/core";


/**
 * Top page navigation bar  Breadcrumbs - side navigation panel of the DomiSMP. The component shows all tools/pages according to user role and permissions
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component({
  selector: 'smp-breadcrumb-item',
  templateUrl: './breadcrumb-item.component.html',
  styleUrls: ['./breadcrumb-item.component.scss']
})

export class BreadcrumbItemComponent implements OnInit {
  @Output() onClickEvent: EventEmitter<NavigationNode> = new EventEmitter();
  @Input() value : NavigationNode;

  name = "";

  constructor(public translationService: TranslateService) {
  }

  ngOnInit() {
    this.translationService.get(this.value.i18n).subscribe(value1 => this.name = value1);
  }

  get icon() {
    return this.value.icon;
  }

  get description() {
    return this.value.code;
  }

  triggerClickEvent() {
    this.clickable && this.onClickEvent.emit(this.value);
  }

  get clickable(): boolean {
    return this.value.clickable;
  }
}
