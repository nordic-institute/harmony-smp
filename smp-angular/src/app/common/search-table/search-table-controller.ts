import {MatDialogRef} from '@angular/material/dialog';
import {SearchTableEntity} from './search-table-entity.model';

export interface SearchTableController<T extends SearchTableEntity> {

  showDetails(row: T): MatDialogRef<any>;

  edit(row: T): MatDialogRef<any>;

  validateDeleteOperation(rows: Array<T>);

  delete(row: T);

  newRow(): T;

  newDialog(config): MatDialogRef<any>;

  dataSaved();

  isRecordChanged(oldModel, newModel): boolean;

  /**
   * Returns whether the row expander should be shown as disabled even when the actual row is not fully disabled.
   *
   * @param row the row for which the row expander should be disabled or not
   */
  isRowExpanderDisabled(row: T): boolean;

}
