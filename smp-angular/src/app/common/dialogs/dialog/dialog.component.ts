import {Component, Input} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'smp-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css']
})
export class DialogComponent {

  @Input() title: String;
  @Input() text: String;

  @Input() type: string;

  @Input() dialogRef: MatDialogRef<any>;

  public isConfirmationDialog(): boolean {
    return this.type === 'confirmation';
  }

  public isInformationDialog(): boolean {
    return this.type === 'information';
  }

  public isWarningDialog(): boolean {
    return this.type === 'warning';
  }

}
