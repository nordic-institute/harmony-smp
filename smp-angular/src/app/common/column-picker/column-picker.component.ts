import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-column-picker',
  templateUrl: './column-picker.component.html'
})
export class ColumnPickerComponent {

  columnSelection: boolean;

  @Input()
  allColumns = [];

  @Input()
  selectedColumns = [];

  @Output()
  onSelectedColumnsChanged = new EventEmitter<Array<any>>();

  constructor() {
  }

  toggleColumnSelection() {
    this.columnSelection = !this.columnSelection
  }

  /*
  * Note: if an 'Actions' column exists, it will be the last one of the array
  * */
  toggle(col) {
    const isChecked = this.isChecked(col);

    if (isChecked) {
      this.selectedColumns = this.selectedColumns.filter(c => {
        return c.name !== col.name;
      });
    } else {
      this.selectedColumns.splice(this.allColumns.indexOf(col), 0, col);
      this.selectedColumns = [...this.selectedColumns];
    }

    this.setLastColumn(this.selectedColumns, 'Actions');

    this.onSelectedColumnsChanged.emit(this.selectedColumns);
  }

  setLastColumn(array : Array<any>, colName : any) {
    let col = array.find(x => x.name === colName);
    if(col !== null && col !== undefined) {
      let posCol = array.indexOf(col);
      array.splice(posCol, 1);
      array.push(col);
    }
  }

  isChecked(col) {
    return this.selectedColumns.find(c => {
      return c.name === col.name;
    });
  }

  selectAllColumns() {
    this.selectedColumns = [...this.allColumns];
    this.onSelectedColumnsChanged.emit(this.selectedColumns);
  }

  selectNoColumns() {
    this.selectedColumns = [];
    this.onSelectedColumnsChanged.emit(this.selectedColumns);
  }

}
