export class ColumnPicker {
  columnSelection: boolean;
  allColumns = [];
  selectedColumns = [];

  changeSelectedColumns(newSelectedColumns: Array<any>) {
    this.selectedColumns = newSelectedColumns
  }

}
