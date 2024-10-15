/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
import {
  AfterViewInit,
  Component,
  ContentChildren, EventEmitter,
  Input, Output,
  QueryList,
} from '@angular/core';
import {
  ExpandableItemComponent
} from "./expandable-item-component/expandable-item.component";

/**
 * Component to display the properties of a document in a table. The properties can be edited and saved.
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Component({
  selector: 'expandable-panel',
  templateUrl: './expandable-panel.component.html',
  styleUrls: ['./expandable-panel.component.scss'],
})
export class ExpandablePanelComponent implements AfterViewInit {
  @ContentChildren(ExpandableItemComponent) private _expandableItems: QueryList<ExpandableItemComponent>;

  @Output() onButtonDoubleClickEvent: EventEmitter<number> = new EventEmitter();
  @Input() showButtonLabel: boolean = false;
  expandPanel: boolean = true;
  selectedIndex: number = 0;

  constructor() {

  }

  ngAfterViewInit(): void {
    this.updateShowItem()
  }

  get expandableItems(): ExpandableItemComponent[] {
    return this._expandableItems?.toArray();
  }

  public onToggleExpandButtonClicked(): void {
    this.expandPanel = !this.expandPanel;
  }

  selectItem(item: ExpandableItemComponent, index: number): void {
    this.selectedIndex = index;
    this.updateShowItem();
  }

  // show item at index and hide all others
  updateShowItem(): void {
    if (!this._expandableItems) {
      return;
    }
    this._expandableItems.forEach((item: ExpandableItemComponent, i: number) => {
      item.showItem = (i === this.selectedIndex);
    });
  }

  getButtonClass(index: number) {
    return index === this.selectedIndex ? 'button-selected' : 'button-deselected';
  }

  onDoubleClick(item: ExpandableItemComponent, index: number) {
    this.onButtonDoubleClickEvent.emit(index);
    this.onToggleExpandButtonClicked();
  }
}
