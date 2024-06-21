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

  displayedColumns: string[] = ['property', 'value'];
  private onChangeCallback: (_: any) => void = () => {
  };
  selected?: DocumentPropertyRo = null;
  dataChanged: boolean = false
  showPropertyPanel: boolean = true;
  initPropertyList: DocumentPropertyRo[] = [];
  propertyDataSource: MatTableDataSource<DocumentPropertyRo> = new MatTableDataSource();

  @ViewChild("DocumentPropertyTable") table: MatTable<DocumentPropertyRo>;
  //@ViewChild(MatPaginator) paginator: MatPaginator
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
    //  this.propertyDataSource.paginator = this.paginator;
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

  public onToggleButtonClicked(): void {
    this.showPropertyPanel = !this.showPropertyPanel;
  }

  /**
   * Method created a new property and opens the dialog to edit it.
   * After the dialog is closed with 'Save' button, the new property is added
   * to the list of properties.
   */
  public onCreateProperty(): void {
    const newProperty: DocumentPropertyRo = {
      property: "my.property",
      value: "",
      type: PropertyValueTypeEnum.STRING,
      desc: "",
      status: EntityStatus.NEW,
      readonly: false
    };
    this.addNewProperty(newProperty);
    /* this.editSelectedProperty(newProperty).afterClosed().subscribe((result) => {
        if (result) {
          this.addNewProperty(result);
        }
      });

     */
  }

  private addNewProperty(newProperty: DocumentPropertyRo) {
    this.propertyDataSource.data.push(newProperty);
    this.dataChanged = true
    // to trigger refresh . render rows does not work on angular 16
    this.propertyDataSource.data = [...this.propertyDataSource.data];
    this.table.renderRows();
    if (this.propertyDataSource.paginator) {
      this.propertyDataSource.paginator.lastPage();
    }
    this.onChangeCallback(this.propertyDataSource.data);
  }

  public onEditSelectedProperty(): void {
    if (!this.selected) {
      return;
    }

    this.editSelectedProperty(this.selected);
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
    console.log("writeValue" + propertyList)
    this.propertyDataSource.data = !propertyList?.length ?
      [] : [...propertyList]
    this.dataChanged = false
  }

  public editSelectedProperty(row: DocumentPropertyRo): MatDialogRef<any> {
    return this.dialog.open(DocumentPropertyDialogComponent, {
      data: {editMode: row.status, row: row}
    });

  }
}
