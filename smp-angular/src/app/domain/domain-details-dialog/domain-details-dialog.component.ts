import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {DomainRo} from "../domain-ro.model";
import {AlertService} from "../../alert/alert.service";
import {SearchTableEntityStatus} from "../../common/search-table/search-table-entity-status.model";
import {GlobalLookups} from "../../common/global-lookups";
import {CertificateRo} from "../../user/certificate-ro.model";
import {CertificateService} from "../../user/certificate.service";

@Component({
  selector: 'domain-details-dialog',
  templateUrl: './domain-details-dialog.component.html'
})
export class DomainDetailsDialogComponent {

  static readonly NEW_MODE = 'New Domain';
  static readonly EDIT_MODE = 'Domain Edit';
  readonly subDomainPattern = '^(?![0-9]+$)(?!.*-$)(?!-)[a-zA-Z0-9-]{1,63}$';
  readonly smpIdDomainPattern = '^(?![0-9]+$)(?!.*-$)(?!-)[a-zA-Z0-9-]{0,63}$';
  readonly domainCodePattern = '^[a-zA-Z0-9]{1,255}$';

  editMode: boolean;
  formTitle: string;
  current: DomainRo & { confirmation?: string };
  domainForm: FormGroup;
  domain;

  notInList(list: string[], exception: string) {
    return (c: AbstractControl): { [key: string]: any } => {
      if (c.value && c.value !== exception && list.includes(c.value))
        return {'notInList': {valid: false}};

      return null;
    }
  }

  constructor( private certificateService: CertificateService,
              private lookups: GlobalLookups,
              private dialogRef: MatDialogRef<DomainDetailsDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {

    this.editMode = data.edit;
    this.formTitle = this.editMode ? DomainDetailsDialogComponent.EDIT_MODE : DomainDetailsDialogComponent.NEW_MODE;
    this.current = this.editMode
      ? {
        ...data.row,
      }
      : {
        domainCode: '',
        smlSubdomain: '',
        smlSmpId: '',
        smlClientKeyAlias: '',
        signatureKeyAlias: '',
        status: SearchTableEntityStatus.NEW,
      };

    this.domainForm = fb.group({
      'domainCode': new FormControl({value: '', disabled: this.editMode}, [Validators.pattern(this.domainCodePattern),
        this.notInList(this.lookups.cachedDomainList.map(a => a.domainCode), this.current.domainCode)]),
      'smlSubdomain': new FormControl({
        value: '',
        disabled: this.editMode
      }, [Validators.pattern(this.subDomainPattern),
        this.notInList(this.lookups.cachedDomainList.map(a => a.smlSubdomain), this.current.smlSubdomain)]),
      'smlSmpId': new FormControl({value: ''}, [Validators.pattern(this.smpIdDomainPattern),
        this.notInList(this.lookups.cachedDomainList.map(a => a.smlSmpId), this.current.smlSmpId)]),
      'smlClientCertHeader': new FormControl({value: ''}, null),
      'smlClientKeyAlias': new FormControl({value: ''}, null),
      'signatureKeyAlias': new FormControl({value: ''}, null),

    });
    this.domainForm.controls['domainCode'].setValue(this.current.domainCode);
    this.domainForm.controls['smlSubdomain'].setValue(this.current.smlSubdomain);
    this.domainForm.controls['smlSmpId'].setValue(this.current.smlSmpId);
    this.domainForm.controls['smlClientCertHeader'].setValue(this.current.smlClientCertHeader);
    this.domainForm.controls['smlClientKeyAlias'].setValue(this.current.smlClientKeyAlias);
    this.domainForm.controls['signatureKeyAlias'].setValue(this.current.signatureKeyAlias);


  }

  submitForm() {
    this.checkValidity(this.domainForm)
    this.dialogRef.close(true);
  }

  checkValidity(g: FormGroup) {
    Object.keys(g.controls).forEach(key => {
      g.get(key).markAsDirty();
    });
    Object.keys(g.controls).forEach(key => {
      g.get(key).markAsTouched();
    });
    //!!! updateValueAndValidity - else some filed did no update current / on blur never happened
    Object.keys(g.controls).forEach(key => {
      g.get(key).updateValueAndValidity();
    });
  }

  public getCurrent(): DomainRo {

    if (!this.editMode) {
      this.current.domainCode = this.domainForm.value['domainCode'];
      this.current.smlSubdomain = this.domainForm.value['smlSubdomain'];
    }
    this.current.smlSmpId = this.domainForm.value['smlSmpId'];
    this.current.smlClientCertHeader = this.domainForm.value['smlClientCertHeader'];
    this.current.smlClientKeyAlias = this.domainForm.value['smlClientKeyAlias'];
    this.current.signatureKeyAlias = this.domainForm.value['signatureKeyAlias'];

    return this.current;

  }

  updateDomainCode(event) {
    this.current.domainCode = event.target.value;
  }

  updateSmlDomain(event) {
    this.current.smlSubdomain = event.target.value;
  }

  updateSmlSmpId(event) {
    this.current.smlSmpId = event.target.value;
  }

  updateSmlClientKeyAlias(event) {
    this.current.smlClientKeyAlias = event.target.value;
  }

  updateSignatureKeyAlias(event) {
    this.current.signatureKeyAlias = event.target.value;
  }

  uploadCertificate(event) {
    const file = event.target.files[0];

    const reader = new FileReader();
    reader.onload = (e) => {
      this.certificateService.uploadCertificate$(file).subscribe((res: CertificateRo) => {
          if (res && res.certificateId){
            this.domainForm.patchValue({
              'smlClientCertHeader': res.blueCoatHeader
            });
          } else {
            this.alertService.exception("Error occurred while reading certificate.", "Check if uploaded file has valid certificate type.", false);
          }
        },
        err => {
          this.alertService.exception('Error uploading certificate file ' + file.name, err);
        }
      );
    };
    reader.onerror = (err) => {
      this.alertService.exception('Error reading certificate file ' + file.name, err);
    };

    reader.readAsBinaryString(file);
  }


}
