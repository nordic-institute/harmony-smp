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
import {
  BeforeLeaveGuard
} from "../../../window/sidenav/navigation-on-leave-guard";
import {DomainPropertyRo} from "../../../common/model/domain-property-ro.model";
import {MatTable, MatTableDataSource} from "@angular/material/table";
import {EntityStatus} from "../../../common/enums/entity-status.enum";
import {
  ControlContainer,
  ControlValueAccessor,
  FormControl,
  FormControlDirective,
  NG_VALUE_ACCESSOR
} from "@angular/forms";
import {DocumentPropertyRo} from "../../model/document-property-ro.model";
import {MatSort} from "@angular/material/sort";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {
  DocumentPropertyDialogComponent
} from "../../dialogs/document-property-dialog/document-property-dialog.component";
import {PropertyValueTypeEnum} from "../../enums/property-value-type.enum";
import {MatPaginator} from "@angular/material/paginator";

/**
 * Component to display the properties of a document in a table. The properties can be edited and saved.
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Component({
  selector: 'document-properties-panel',
  templateUrl: './document-properties-panel.component.html',
  styleUrls: ['./document-properties-panel.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DocumentPropertiesPanelComponent),
      multi: true
    }
  ]
})
export class DocumentPropertiesPanelComponent implements AfterViewInit, BeforeLeaveGuard, ControlValueAccessor {
  private readonly NEW_PROPERTY_NAME_TEMPLATE: string = 'document.property.v';
  displayedColumns: string[] = ['property', 'value'];
  private onChangeCallback: (_: any) => void = () => {
  };
  selected?: DocumentPropertyRo = null;
  dataChanged: boolean = false;
  initPropertyList: DocumentPropertyRo[] = [];
  propertyDataSource: MatTableDataSource<DocumentPropertyRo> = new MatTableDataSource();

  @ViewChild("DocumentPropertyTable") table: MatTable<DocumentPropertyRo>;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(public dialog: MatDialog, private controlContainer: ControlContainer) {

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


  ngAfterViewInit() {
    this.propertyDataSource.paginator = this.paginator;
    this.propertyDataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.propertyDataSource.filter = filterValue?.trim().toLowerCase();

    if (this.propertyDataSource.paginator) {
      this.propertyDataSource.paginator.firstPage();
    }
  }

  get saveButtonEnabled(): boolean {
    return this.dataChanged;
  }

  get cancelButtonEnabled(): boolean {
    return this.dataChanged;
  }

  /**
   * Method created a new property and opens the dialog to edit it.
   * After the dialog is closed with 'Save' button, the new property is added
   * to the list of properties.
   */
  public onCreateProperty(): void {
    const newProperty: DocumentPropertyRo = {
      property: this.uniquePropertyName,
      value: "",
      type: PropertyValueTypeEnum.STRING,
      desc: "",
      status: EntityStatus.NEW,
      readonly: false
    };
    this.editSelectedProperty(newProperty).afterClosed()
      .subscribe((result): void => {
        if (result) {
          this.addNewProperty(result);
        }
      });
  }

  /**
   * Method returns unique property name. The method checks all properties in the list and
   * creates a new name with index if the name is already used.
   */
  get uniquePropertyName(): string {
    let propertyNames: string[] = this.allPropertyNames;
    // create index and check "my.property.[index]" until we find unique name
    // and format index with 3 digits
    let index: number = 1;
    let propertyName: string = this.NEW_PROPERTY_NAME_TEMPLATE + index.toString().padStart(3, '0');

    while (propertyNames.includes(propertyName)) {
      index++;
      propertyName = this.NEW_PROPERTY_NAME_TEMPLATE + index.toString().padStart(3, '0');
    }
    return propertyName
  }

  private addNewProperty(newProperty: DocumentPropertyRo) {
    this.propertyDataSource.data.push(newProperty);

    // to trigger refresh . render rows does not work on angular 16
    this.propertyDataSource.data = [...this.propertyDataSource.data];
    if (this.propertyDataSource.paginator) {
      this.propertyDataSource.paginator.lastPage();
    }
    this.selected = newProperty;
    this.dataChanged = true;
    this.onChangeCallback(this.propertyDataSource.data);
  }

  public onEditSelectedProperty(): void {
    if (!this.selected) {
      return;
    }

    this.editSelectedProperty(this.selected).afterClosed()
      .subscribe((result): void => {
        if (result) {
          let indexToUpdate = this.propertyDataSource.data
            .findIndex(item => item.property === result.property);

          let property: DocumentPropertyRo = this.equals(this.initPropertyList[indexToUpdate], result) ?
            this.initPropertyList[indexToUpdate] : result;


          this.propertyDataSource.data[indexToUpdate] = property;
          // trigger reload
          this.propertyDataSource.data = [...this.propertyDataSource.data];
          this.selected = property;
          this.dataChanged = true;
          this.onChangeCallback(this.propertyDataSource.data);
        }
      });
  }

  /**
   * Method compares two DocumentPropertyRo objects and returns true if they are equal. The method
   * validates property name, value, type and description.
   * @param value1
   * @param value2
   */
  private equals(value1: DocumentPropertyRo, value2: DocumentPropertyRo): boolean {
    return value1.property === value2.property
      && value1.value === value2.value
      && value1.desc === value2.desc
      && value1.type === value2.type;
  }

  public onDeleteSelectedProperty(): void {
    if (!this.selected) {
      return;
    }
    let properties = this.propertyDataSource.data;

    if (this.selected.status === EntityStatus.NEW) {
      const index: number = properties.indexOf(this.selected);
      if (index !== -1) {
        properties.splice(index, 1);
      }
    } else {
      this.selected.status = EntityStatus.REMOVED;
      this.selected.deleted = true;
    }
    // to trigger refresh
    this.propertyDataSource.data = [...properties];

    this.onChangeCallback(this.propertyDataSource.data);
  }


  public onSaveButtonClicked() {
    // submit list of properties to the backend

  }

  /*
    * reset/reload properties from the server
   */
  public onResetButtonClicked(): void {
    this.control.setValue(this.initPropertyList);
    this.control.markAsPristine();
  }

  /**
   * return the value to be displayed in the table row. If the row is updated and not system default,
   * then display the new value else display the old value
   * @param row
   */
  getTableRowValue(row: DomainPropertyRo) {
    return row.systemDefault ? row.systemDefaultValue : row.value;
  }

  isDirty(): boolean {
    return this.dataChanged;
  }

  propertySelected(property: DocumentPropertyRo) {
    this.selected = property;
  }

  get editButtonDisabled(): boolean {
    return !this.selected;
  }

  get deleteButtonDisabled(): boolean {
    return !this.selected || this.selected.readonly;
  }

  getRowClass(row, oddRow: boolean) {
    return {
      'datatable-row-selected': row === this.selected,
      'table-row-new': (row.status === EntityStatus.NEW),
      'table-row-updated': (row.status === EntityStatus.UPDATED),
      'deleted': (row.status === EntityStatus.REMOVED),
      'datatable-row-odd': oddRow
    };
  }

  registerOnChange(fn: any): void {
    this.onChangeCallback = fn;

  }


  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
  }

  /**
   * Implementation of the ControlValueAccessor method to  write value to the component.
   * @param propertyList
   */
  writeValue(propertyList: DocumentPropertyRo[]): void {
    this.initPropertyList = propertyList;
    this.propertyDataSource.data = !propertyList?.length ? [] : [...propertyList];
    this.dataChanged = false;
  }

  public editSelectedProperty(row: DocumentPropertyRo): MatDialogRef<any> {
    return this.dialog.open(DocumentPropertyDialogComponent, {
      data: {
        editMode: row.status,
        row: row,
        allPropertyNames: this.allPropertyNames
      }
    });
  }

  get allPropertyNames(): string[] {
    return this.propertyDataSource.data.map(item => item.property);
  }
}
