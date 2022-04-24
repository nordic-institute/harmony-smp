import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {PropertyRo} from "../property-ro.model";
import {AlertMessageService} from "../../common/alert-message/alert-message.service";
import {SearchTableEntityStatus} from "../../common/search-table/search-table-entity-status.model";

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
  propertyForm: FormGroup;
  disabled: true;


  constructor(
    public dialog: MatDialog,
    private dialogRef: MatDialogRef<PropertyDetailsDialogComponent>,
    private alertService: AlertMessageService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder) {

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
        status: SearchTableEntityStatus.NEW,
      };

    this.propertyForm = fb.group({
      'property': new FormControl({value: '', readonly: true}, null),
      'desc': new FormControl({value: '', readonly: true}, null),
      'type': new FormControl({value: '', readonly: true}, null),
      'value': new FormControl({value: ''}),

    });

    this.propertyForm.controls['property'].setValue(this.current.property);
    this.propertyForm.controls['desc'].setValue(this.current.desc);
    this.propertyForm.controls['type'].setValue(this.current.type);
    this.propertyForm.controls['value'].setValue(this.valueFromPropertyStringValue(this.current.value, this.current.type));
  }

  ngOnInit() {

  }

  submitForm() {
    this.checkValidity(this.propertyForm)
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
        return 'number';
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

  public getCurrent(): PropertyRo {
    this.current.value = this.propertyForm.value['value'];
    return this.current;
  }

}
