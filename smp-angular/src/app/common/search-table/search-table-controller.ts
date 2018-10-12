import {MatDialogConfig, MatDialogRef} from '@angular/material';
import {SearchTableEntity} from './search-table-entity.model';

export interface SearchTableController {
  showDetails(row);
  edit(row);
  delete(row);
  newRow(): SearchTableEntity;
  newDialog(config?: MatDialogConfig): MatDialogRef<any>;
}
