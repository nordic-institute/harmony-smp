import {MdDialogConfig, MdDialogRef} from "@angular/material";
import {SearchTableEntity} from "./search-table-entity.model";

export interface SearchTableController {
  showDetails(row);
  edit(row);
  delete(row);
  newRow(): SearchTableEntity;
  newDialog(config?: MdDialogConfig): MdDialogRef<any>;
}
