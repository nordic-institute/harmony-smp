import {Component, Inject} from "@angular/core";
import {CertificateRo} from "../../model/certificate-ro.model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import moment from 'moment';
import {Moment} from "moment";
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS} from "@angular/material-moment-adapter";
import {AlertMessageService} from "../../alert-message/alert-message.service";

@Component({
    templateUrl: './prepare-certificate-dialog.component.html',
    providers: [{ provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true } }],
    standalone: false
})
export class PrepareCertificateDialogComponent {

  keystoreCertificates: CertificateRo[];

  formTitle: string;

  form: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              public dialogRef: MatDialogRef<PrepareCertificateDialogComponent>,
              private formBuilder: FormBuilder,
              private alertService: AlertMessageService) {
    this.formTitle = data.title;
    this.keystoreCertificates = data.certificates;

    // take the following day at midnight in UTC to ensure the migration date is really in the future according to DomiSML
    const tomorrow = moment().utc().add(1, 'day').startOf('day');
    this.form = formBuilder.group({
      'smlChangeCertificateAlias': new FormControl('', Validators.required),
      'smlChangeCertificateDate': new FormControl(tomorrow, Validators.required),
      'smlChangeCertificateTime': new FormControl(tomorrow.local().format('HH:mm'), Validators.required),
    });
  }

  onApplyButtonClicked() {
    if(!this.form.valid) {
      this.alertService.errorForTranslation('domain.sml.integration.panel.prepare.certificate.dialog.error.invalid.form');
      return;
    }

    const changeDate: Moment = moment(this.form.get('smlChangeCertificateDate').value);
    const certificateAlias = this.form.get('smlChangeCertificateAlias').value;
    const changeTime = moment(this.form.get('smlChangeCertificateTime').value, 'HH:mm');
    const changeDateTime = changeDate.set({
      hours: changeTime.hours(),
      minutes: changeTime.minutes()
    });

    // the migration date cannot be set to today's end of day
    const tomorrow = moment().utc().add(1, 'day').startOf('day');
    if (changeDateTime.isBefore(tomorrow)) {
      this.alertService.errorForTranslation('domain.sml.integration.panel.prepare.certificate.dialog.error.migration.date.in.past');
      return;
    }
    this.dialogRef.close({certificateAlias, changeDateTime});
  }
}
