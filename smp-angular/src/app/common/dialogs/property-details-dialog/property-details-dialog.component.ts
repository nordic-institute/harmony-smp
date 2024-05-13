import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup} from "@angular/forms";;
import {AlertMessageService} from "../../../common/alert-message/alert-message.service";
import {EntityStatus} from "../../../common/enums/entity-status.enum";
import {SmpConstants} from "../../../smp.constants";
import {HttpClient} from "@angular/common/http";
import {HttpErrorHandlerService} from "../../../common/error/http-error-handler.service";
import {
  PropertyRo
} from "../../../system-settings/admin-properties/property-ro.model";
import {
  PropertyValidationRo
} from "../../../system-settings/admin-properties/property-validate-ro.model";
import {PropertyTypeEnum} from "../../enums/property-type.enum";
import {firstValueFrom} from "rxjs";

@Component({
  selector: 'property-details-dialog',
  templateUrl: './property-details-dialog.component.html',
  styleUrls: ['./property-details-dialog.component.css']
})
export class PropertyDetailsDialogComponent implements OnInit {

  static readonly NEW_MODE: string = 'New {TYPE} Property';
  static readonly EDIT_MODE: string  = '{TYPE} Property Edit';


  editMode: boolean;
  formTitle: string;
  current: PropertyRo & { confirmation?: string, systemDefault?: boolean };
  propertyForm: UntypedFormGroup;
  disabled: true;
  showSpinner: boolean = false;
  propertyType: PropertyTypeEnum = PropertyTypeEnum.SYSTEM;


  constructor(
    public dialog: MatDialog,
    private httpErrorHandlerService: HttpErrorHandlerService,
    protected http: HttpClient,
    private dialogRef: MatDialogRef<PropertyDetailsDialogComponent>,
    private alertService: AlertMessageService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder) {

    this.editMode = data.edit;
    this.propertyType = data.propertyType;
    this.propertyType = !data.propertyType?PropertyTypeEnum.SYSTEM: data.propertyType;
    this.formTitle = (this.editMode ? PropertyDetailsDialogComponent.EDIT_MODE : PropertyDetailsDialogComponent.NEW_MODE)
      .replace('{TYPE}', this.capitalize(this.propertyType));

    this.current = this.editMode
      ? {
        ...data.row ,
      }
      : {
        property: '',
        value: '',
        type: '',
        desc: '',
        readonly: false,
        status: EntityStatus.NEW,
        systemDefault: false,
      };

    this.propertyForm = fb.group({
      'property': new UntypedFormControl({value: '', readonly: true}, null),
      'desc': new UntypedFormControl({value: '', readonly: true}, null),
      'type': new UntypedFormControl({value: '', readonly: true}, null),
      'value': new UntypedFormControl({value: ''}),
      'valuePattern': new UntypedFormControl({value: ''}),
      'errorMessage': new UntypedFormControl({value: ''}),
      'systemDefault': new UntypedFormControl({value: 'true' }),

    });

    this.propertyForm.controls['property'].setValue(this.current.property);
    this.propertyForm.controls['desc'].setValue(this.current.desc);
    this.propertyForm.controls['type'].setValue(this.current.type);
    this.propertyForm.controls['value'].setValue(this.valueFromPropertyStringValue(this.current.value, this.current.type));
    this.propertyForm.controls['valuePattern'].setValue(this.current.valuePattern);
    this.propertyForm.controls['systemDefault'].setValue(this.current.systemDefault);

    this.propertyForm.controls['errorMessage'].setValue('')
    this.updateValueState()
  }

  ngOnInit() {

  }

  /**
   * Methods validates the property with server validator. If property value is ok
   * it closes the dialog else writes the errorMessage.
   */
  async submitForm() {
    this.checkValidity(this.propertyForm);

    let request: PropertyRo =  this.getCurrent();

    // if domain property we do not need to validate
    if (this.propertyType == PropertyTypeEnum.DOMAIN) {
      this.propertyForm.controls['errorMessage'].setValue("");
      // we can close the dialog
      this.closeDialog();
      return;
    }
    // if system property validate property
    let validationObservable = this.http
      .post<PropertyValidationRo>(SmpConstants.REST_INTERNAL_PROPERTY_VALIDATE, request);


    this.showSpinner = true;

    try {
      const result: PropertyValidationRo = await firstValueFrom(validationObservable);
      this.showSpinner = false;
      if (!result.propertyValid) {
        this.propertyForm.controls['errorMessage'].setValue(result.errorMessage?result.errorMessage:'Invalid property');
      } else {
        this.propertyForm.controls['errorMessage'].setValue("");
        // we can close the dialog
        this.closeDialog();
      }
    } catch(err) {
      if (this.httpErrorHandlerService.logoutOnInvalidSessionError(err)){
        this.closeDialog();
        return;
      }
      this.alertService.error("Error occurred on Validation the property", err)
      console.log("Error occurred on Validation the property: " + err);
    }
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
    this.current.status= EntityStatus.UPDATED;
    this.current.value = this.propertyForm.value['value'];
    this.current.systemDefault = this.propertyForm.value['systemDefault'];
    return this.current;
  }

  closeDialog() {
    this.dialogRef.close(true);
  }

  get isSystemDefault(): boolean {
    let systemDefault = this.propertyForm.value['systemDefault'];
    return  systemDefault;
  }

  /**
   * Method updates the state of the value field based on the system default checkbox.
   */
  updateValueState(): void {
    if (!this.isDomainProperty ||  !this.isSystemDefault) {
      this.propertyForm.controls['value'].enable();
      this.propertyForm.controls['value'].setValue(this.current.value);
    } else {
      this.propertyForm.controls['value'].setValue(this.current.systemDefaultValue);
      this.propertyForm.controls['value'].disable();
    }
  }

  get isDomainProperty(): boolean {
    return this.propertyType == PropertyTypeEnum.DOMAIN;
  }

  capitalize<T extends string>(str: T):string{
    return (str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()) as Capitalize<T>;
  }

  get isDirty(): boolean {
    return this.propertyForm.dirty;
  }
}
