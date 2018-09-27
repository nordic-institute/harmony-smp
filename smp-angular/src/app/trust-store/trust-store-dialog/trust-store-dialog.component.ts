import {Component, Inject} from "@angular/core";
import {TrustStoreEntry} from "../trust-store-entry.model";
import {MD_DIALOG_DATA, MdDialogRef} from "@angular/material";

/**
 * @Author Dussart Thomas
 */
@Component({
  selector: 'app-truststore-dialog',
  templateUrl: './trust-store-dialog.component.html'
})
export class TrustStoreDialogComponent {

  dateFormat: String = 'yyyy-MM-dd HH:mm:ssZ';
  trustStoreEntry: TrustStoreEntry;

  constructor(public dialogRef: MdDialogRef<TrustStoreDialogComponent>, @Inject(MD_DIALOG_DATA) public data: any) {
    this.trustStoreEntry = data.trustStoreEntry;
  }

}
