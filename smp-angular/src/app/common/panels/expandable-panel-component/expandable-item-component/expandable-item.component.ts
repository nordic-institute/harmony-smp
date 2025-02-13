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
  ChangeDetectorRef,
  Component,
  HostBinding,
  Input,
  ViewChild,
} from '@angular/core';


/**
 * Component to display the properties of a document in a table. The properties can be edited and saved.
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Component({
  selector: 'expandable-item',
  templateUrl: './expandable-item.component.html',
  styleUrls: ['./expandable-item.component.scss'],
})
export class ExpandableItemComponent implements AfterViewInit {

  //
  @Input() showButtonSpacer: boolean = false;
  @Input() title: string;
  @Input() buttonLabel: string;
  @Input() icon: string;
  @Input() tooltip: string;
  _visible: boolean = false;


  @ViewChild('root') expandableItem: any;

  constructor(private cdr: ChangeDetectorRef) {

  }

  ngAfterViewInit() {
    this.cdr.detectChanges(); // Mark for change detection
  }

  @HostBinding('style.display')
  get display() {
    return this._visible ? 'flex' : 'none';
  }

  @Input() set showItem(show: boolean) {
    this._visible = show;
    // this.expandableItem.nativeElement.style.display = show ? 'block' : 'none';
  }
}
