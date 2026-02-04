import {SearchTableController} from '../../search-table/search-table-controller';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {GlobalLookups} from "../../global-lookups";
import {ReviewDocumentVersionRo} from "../../model/review-document-version-ro.model";

export class ReviewTasksController implements SearchTableController<ReviewDocumentVersionRo> {

  constructor(protected lookups: GlobalLookups, public dialog: MatDialog) {
  }

  validateDeleteOperation(rows: ReviewDocumentVersionRo[]) {
    return null;
  }

  newRow(): ReviewDocumentVersionRo {
    return null;
  }

  dataSaved() {

  }

  isRecordChanged(oldModel: any, newModel: any): boolean {
    return false;
  }

  isRowExpanderDisabled(row: ReviewDocumentVersionRo): boolean {
    return true;
  }

  public showDetails(row: ReviewDocumentVersionRo): MatDialogRef<any> {
    return null;
  }

  public edit(row: ReviewDocumentVersionRo): MatDialogRef<any> {
    return null;
  }

  public delete(row: ReviewDocumentVersionRo) {
  }

  newDialog(config): MatDialogRef<any> {
    return null;
  }
}
