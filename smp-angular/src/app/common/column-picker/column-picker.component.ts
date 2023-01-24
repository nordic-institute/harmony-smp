import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {AlertMessageService} from "../alert-message/alert-message.service";

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

  constructor(private alertService: AlertMessageService) {
  }

  ngOnChanges(changes: SimpleChanges) {
    this.allColumns.forEach(col => col.isSelected = this.isChecked(col));
  }

  toggleColumnSelection() {
    this.columnSelection = !this.columnSelection;
    this.alertService.clearAlert();
  }

  /*
  * Note: if an 'Actions' column exists, it will be the last one of the array
  * */
  toggle(col) {
    setTimeout(() => {
      this.selectedColumns = this.allColumns.filter(col => col.isSelected);
      this.onSelectedColumnsChanged.emit(this.selectedColumns);
    });
    this.alertService.clearAlert();
  }

  selectAllColumns() {
    this.selectedColumns = [...this.allColumns];
    this.onSelectedColumnsChanged.emit(this.selectedColumns);
    this.alertService.clearAlert();
  }

  selectNoColumns() {
    this.selectedColumns = [];
    this.onSelectedColumnsChanged.emit(this.selectedColumns);
    this.alertService.clearAlert();
  }

  isChecked(col) {
    const isChecked = this.selectedColumns.find(c => c.name === col.name) != null;
    return isChecked;
  }
}
