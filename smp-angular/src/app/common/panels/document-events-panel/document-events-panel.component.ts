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
  forwardRef,
  Input,
  ViewChild,
} from '@angular/core';
import {MatTable, MatTableDataSource} from "@angular/material/table";
import {
  ControlContainer,
  ControlValueAccessor,
  FormControl,
  FormControlDirective,
  NG_VALUE_ACCESSOR
} from "@angular/forms";
import {MatSort} from "@angular/material/sort";
import {MatDialog} from "@angular/material/dialog";
import {MatPaginator} from "@angular/material/paginator";
import {
  DocumentVersionEventRo
} from "../../model/document-version-event-ro.model";
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {GlobalLookups} from "../../global-lookups";

/**
 * Component to display the properties of a document in a table. The properties can be edited and saved.
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Component({
  selector: 'document-events-panel',
  templateUrl: './document-events-panel.component.html',
  styleUrls: ['./document-events-panel.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DocumentEventsPanelComponent),
      multi: true
    }
  ]
})
export class DocumentEventsPanelComponent implements AfterViewInit, BeforeLeaveGuard, ControlValueAccessor {


  displayedColumns: string[] = ['date', 'eventType', 'username', 'eventSource'];
  private onChangeCallback: (_: any) => void = () => {
  };
  eventDataSource: MatTableDataSource<DocumentVersionEventRo> = new MatTableDataSource();
  dataChanged: boolean = false;
  selected: DocumentVersionEventRo;
  @ViewChild("DocumentVersionEventTable") table: MatTable<DocumentVersionEventRo>;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private globalLookups: GlobalLookups,
    public dialog: MatDialog,
    private controlContainer: ControlContainer) {
  }

  get dateTimeFormat(): string {
    return this.globalLookups.getDateTimeFormat();
  }

  @ViewChild(FormControlDirective, {static: true})
  formControlDirective: FormControlDirective;
  @Input()
  formControl: FormControl;

  @Input()
  formControlName: string;  /* get hold of FormControl instance no matter formControl or    formControlName is given. If formControlName is given, then this.controlContainer.control is the parent FormGroup (or FormArray) instance. */
  get control() {
    return this.formControl || this.controlContainer.control.get(this.formControlName);
  }

  /**
   * Implementation of the ControlValueAccessor method to  write value to the component.
   * @param eventList
   */
  writeValue(eventList: DocumentVersionEventRo[]): void {
    this.eventDataSource.data = !eventList?.length ? [] : [...eventList];
    this.dataChanged = false;
  }

  ngAfterViewInit() {
    this.eventDataSource.paginator = this.paginator;
    this.eventDataSource.sort = this.sort;
    // add custom filter to exclude filtering on  event description
    this.eventDataSource.filterPredicate = (data: DocumentVersionEventRo, filter: string) => {
      return data.eventType?.toLowerCase().includes(filter)
        || data.username?.toLowerCase().includes(filter)
        || data.eventSourceType?.toLowerCase().includes(filter)
        || data.eventOn?.toLocaleString().toLowerCase().includes(filter);
    };
  }

  applyFilter(event: Event) {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.eventDataSource.filter = filterValue?.trim().toLowerCase();

    if (this.eventDataSource.paginator) {
      this.eventDataSource.paginator.firstPage();
    }
  }

  isDirty(): boolean {
    return this.dataChanged;
  }

  registerOnChange(fn: any): void {
    this.onChangeCallback = fn;

  }

  registerOnTouched(fn: any): void {
    // not implemented
  }

  setDisabledState(isDisabled: boolean): void {
    // not implemented
  }

  getRowClass(row, oddRow: boolean) {
    return {
      'datatable-row-selected': row === this.selected,
      'datatable-row-odd': oddRow
    };
  }

}
