import {Component, Inject} from "@angular/core";
import {CertificateRo} from "../../model/certificate-ro.model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import * as moment from 'moment';
import {Moment} from "moment";
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS} from "@angular/material-moment-adapter";

@Component({
  templateUrl: './prepare-certificate-dialog.component.html',
  providers: [{ provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true } }]

})
export class PrepareCertificateDialogComponent {

  keystoreCertificates: CertificateRo[];

  formTitle: string;

  form: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              public dialogRef: MatDialogRef<PrepareCertificateDialogComponent>,
              private formBuilder: FormBuilder) {
    this.formTitle = data.title;
    this.keystoreCertificates = data.certificates;

    this.form = formBuilder.group({
      'smlChangeCertificateAlias': new FormControl('', Validators.required),
      'smlChangeCertificateDate': new FormControl(new Date(), Validators.required),
      'smlChangeCertificateTime': new FormControl('', Validators.required),
    });
  }

  onApplyButtonClicked() {
    const certificateAlias = this.form.get('smlChangeCertificateAlias').value;
    const changeDate:Moment = moment(this.form.get('smlChangeCertificateDate').value);
    const changeTime = moment(this.form.get('smlChangeCertificateTime').value, 'HH:MM');
    const changeDateTime = changeDate.set({
      hours: changeTime.hours(),
      minutes: changeTime.minutes()
    });
    console.log(changeDateTime.toString());

    if(this.form.valid) {
      this.dialogRef.close({ certificateAlias,  changeDateTime });
    }
  }
}
