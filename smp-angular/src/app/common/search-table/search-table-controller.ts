import {MatDialogConfig, MatDialogRef} from '@angular/material';
import {SearchTableEntity} from './search-table-entity.model';

export interface SearchTableController {
  showDetails(row);
  edit(row);

  validateDeleteOperation(rows: Array<SearchTableEntity>);
  delete(row);
  newRow(): SearchTableEntity;
  newDialog(config?: MatDialogConfig): MatDialogRef<any>;
  dataSaved();
}
