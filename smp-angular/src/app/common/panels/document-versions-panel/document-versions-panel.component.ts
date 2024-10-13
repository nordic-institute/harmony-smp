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
  EventEmitter,
  forwardRef,
  Input,
  Output,
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
import {DocumentVersionRo} from "../../model/document-version-ro.model";
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {GlobalLookups} from "../../global-lookups";
import {DateTimeService} from "../../services/date-time.service";

/**
 * Component to display the properties of a document in a table. The properties can be edited and saved.
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Component({
  selector: 'document-versions-panel',
  templateUrl: './document-versions-panel.component.html',
  styleUrls: ['./document-versions-panel.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DocumentVersionsPanelComponent),
      multi: true
    }
  ]
})
export class DocumentVersionsPanelComponent implements AfterViewInit, BeforeLeaveGuard, ControlValueAccessor {
  @Output() selectedVersionChange: EventEmitter<number> = new EventEmitter<number>();

  displayedColumns: string[] = ['version', 'status', 'createdOn', 'lastUpdatedOn'];
  private onChangeCallback: (_: any) => void = () => {
  };
  versionDataSource: MatTableDataSource<DocumentVersionRo> = new MatTableDataSource();
  dataChanged: boolean = false;
  selected: DocumentVersionRo;
  _currentVersion: number;

  @ViewChild("DocumentVersionsTable") table: MatTable<DocumentVersionRo>;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private dateTimeService: DateTimeService,
    public dialog: MatDialog,
    private controlContainer: ControlContainer) {
  }

  get dateTimeFormat(): string {
    return this.dateTimeService.userDateTimeFormat;
  }

  @Input() set selectedVersion(version: number) {
    this._currentVersion = version;
    // find selected version
    this.updateSelectedVersion();
  }

  get selectedVersion(): number {
    return this._currentVersion;
  }

  /**
   * Private method to locate selected row for current version
   * @private
   */
  private updateSelectedVersion(): void {

    const selectedVersion :DocumentVersionRo = this.versionDataSource.data.find(v => v.version === this._currentVersion);
    this.selected = selectedVersion;
  }

  /**
   * Method to handle row selection
   * @param row
   */
  onRowSelect(row: DocumentVersionRo) {
    this.selected = row;
    this.selectedVersionChange.emit(row.version);
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
  writeValue(eventList: DocumentVersionRo[]): void {
    this.versionDataSource.data = !eventList?.length ? [] : [...eventList];
    this.updateSelectedVersion();
    this.dataChanged = false;
  }

  ngAfterViewInit() {
    this.versionDataSource.paginator = this.paginator;
    this.versionDataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.versionDataSource.filter = filterValue?.trim().toLowerCase();

    if (this.versionDataSource.paginator) {
      this.versionDataSource.paginator.firstPage();
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
