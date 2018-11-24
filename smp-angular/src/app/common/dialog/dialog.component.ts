import { Component, Input } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'smp-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css']
})
export class DialogComponent {

  @Input() title: String;

  @Input() type: string;

  @Input() dialogRef: MatDialogRef<any>;

  public isConfirmationDialog() {
    return this.type === 'confirmation';
  }

  public isInformationDialog() {
    return this.type === 'information';
  }

}
