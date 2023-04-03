import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup} from "@angular/forms";
import {PropertyRo} from "../property-ro.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {EntityStatus} from "../../common/model/entity-status.model";
import {ServiceGroupValidationRo} from "../../service-group-edit/service-group-details-dialog/service-group-validation-edit-ro.model";
import {SmpConstants} from "../../smp.constants";
import {ServiceGroupValidationErrorCodeModel} from "../../service-group-edit/service-group-details-dialog/service-group-validation-error-code.model";
import {PropertyValidationRo} from "../property-validate-ro.model";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'property-details-dialog',
  templateUrl: './property-details-dialog.component.html',
  styleUrls: ['./property-details-dialog.component.css']
})
export class PropertyDetailsDialogComponent implements OnInit {

  static readonly NEW_MODE = 'New Property';
  static readonly EDIT_MODE = 'Property Edit';


  editMode: boolean;
  formTitle: string;
  current: PropertyRo & { confirmation?: string };
  propertyForm: UntypedFormGroup;
  disabled: true;
  showSpinner: boolean = false;


  constructor(
    public dialog: MatDialog,
    protected http: HttpClient,
    private dialogRef: MatDialogRef<PropertyDetailsDialogComponent>,
    private alertService: AlertMessageService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder) {

    this.editMode = data.edit;
    this.formTitle = this.editMode ? PropertyDetailsDialogComponent.EDIT_MODE : PropertyDetailsDialogComponent.NEW_MODE;
    this.current = this.editMode
      ? {
        ...data.row,
      }
      : {
        property: '',
        value: '',
        type: '',
        desc: '',
        readonly: false,
        status: EntityStatus.NEW,
      };

    this.propertyForm = fb.group({
      'property': new UntypedFormControl({value: '', readonly: true}, null),
      'desc': new UntypedFormControl({value: '', readonly: true}, null),
      'type': new UntypedFormControl({value: '', readonly: true}, null),
      'value': new UntypedFormControl({value: ''}),
      'valuePattern': new UntypedFormControl({value: ''}),
      'errorMessage': new UntypedFormControl({value: ''}),

    });

    this.propertyForm.controls['property'].setValue(this.current.property);
    this.propertyForm.controls['desc'].setValue(this.current.desc);
    this.propertyForm.controls['type'].setValue(this.current.type);
    this.propertyForm.controls['value'].setValue(this.valueFromPropertyStringValue(this.current.value, this.current.type));
    this.propertyForm.controls['valuePattern'].setValue(this.current.valuePattern);

    this.propertyForm.controls['errorMessage'].setValue('');
  }

  ngOnInit() {

  }

  submitForm() {
    this.checkValidity(this.propertyForm);

    let request =  this.getCurrent();
    //
    let validationObservable = this.http.post<PropertyValidationRo>(SmpConstants.REST_INTERNAL_PROPERTY_VALIDATE, request);
    this.showSpinner = true;
    validationObservable.toPromise().then((res: PropertyValidationRo) => {
      this.showSpinner = false;

      if (!res.propertyValid) {
        this.propertyForm.controls['errorMessage'].setValue(res.errorMessage?res.errorMessage:'Invalid property');
      } else {
        this.propertyForm.controls['errorMessage'].setValue("");
        // we can close the dialog
        this.dialogRef.close(true);
      }
    }).catch((err) => {
      this.alertService.error("Error occurred on Validation the property", err)
      console.log("Error occurred on Validation the property: " + err);
    });
  }

  checkValidity(g: UntypedFormGroup) {
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

  /**
   * Method casts string value to correct property type for dialog component used for editing.
   * @param value
   * @param propertyType
   */
  public valueFromPropertyStringValue(value: string, propertyType: string) {
    switch (propertyType) {
      case 'BOOLEAN':
        return value === 'true';
      default:
        return value;
    }
  }

  public valueToPropertyStringValue(value: string, propertyType: string) {
    switch (propertyType) {
      case 'BOOLEAN':
        return value === 'true';
      default:
        return value;
    }
  }

  getInputType(propertyType: string) {
    console.log("Get input type for row " + this.current.type)
    switch (propertyType) {
      case 'STRING':
      case 'LIST_STRING':
      case 'MAP_STRING':
      case 'FILENAME':
      case 'PATH':
        return 'text';
      case 'INTEGER':
        return 'text';
      case 'BOOLEAN':
        return 'checkbox';
      case 'REGEXP':
        return 'text';
      case 'EMAIL':
        return 'email';
      case 'URL':
        return 'url';
      default:
        return 'text';
    }
  }
  getInputPatternType(propertyType: string) {
    console.log("Get input pattern for row " + this.current.type)
    switch (propertyType) {
      case 'STRING':
      case 'LIST_STRING':
      case 'MAP_STRING':
      case 'FILENAME':
      case 'PATH':
        return '';
      case 'INTEGER':
        return '[0-9]*';
      case 'BOOLEAN':
        return 'true/false';
      case 'REGEXP':
        return '';
      case 'EMAIL':
        return '';
      case 'URL':
        return '';
      default:
        return '';
    }
  }

  public getCurrent(): PropertyRo {
    this.current.value = this.propertyForm.value['value'];
    return this.current;
  }

}
