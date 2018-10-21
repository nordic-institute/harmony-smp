import {ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {Observable} from "rxjs/internal/Observable";
import {SearchTableResult} from "../common/search-table/search-table-result.model";
import {HttpClient} from "@angular/common/http";
import {SmpConstants} from "../smp.constants";
import {UserRo} from "../user/user-ro.model";
import {AlertService} from "../alert/alert.service";
import {DomainDetailsDialogComponent} from "../domain/domain-details-dialog/domain-details-dialog.component";
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SearchTableEntityStatus} from "../common/search-table/search-table-entity-status.model";
import {DomainRo} from "../domain/domain-ro.model";
import {ServiceGroupEditRo} from "./service-group-edit-ro.model";
import {ServiceMetadataEditRo} from "./service-metadata-edit-ro.model";
import {GlobalLookups} from "../common/global-lookups";

@Component({
  selector: 'app-messagelog-details',
  templateUrl: './service-group-metadata-wizard-dialog/service-group-extension-wizard-dialog.component.html',
  styleUrls: ['./service-group-metadata-wizard-dialog/service-group-extension-wizard-dialog.component.css']
})
export class ServiceGroupExtensionWizardDialogComponent  {
  dialogForm: FormGroup;

  dummyXML: string ="<!-- Custom element is mandatory by OASIS SMP schema.\n    Replace following element with your XML structure. -->\n<ext:example xmlns:ext=\"http://my.namespace.eu\">my mandatory content</ext:example>"

  elements: any[] = [
    {name:'ExtensionID', description:'An identifier for the Extension assigned by the creator of the extension.'},
    {name:'ExtensionName', description:'A name for the Extension assigned by the creator of the extension.'},
    {name:'ExtensionAgencyID', description:'An agency that maintains one or more Extensions.'},
    {name:'ExtensionAgencyName', description:'The name of the agency that maintains the Extension.'},
    {name:'ExtensionAgencyURI', description:'A URI for the Agency that maintains the Extension.'},
    {name:'ExtensionVersionID', description:'The version of the Extension.'},
    {name:'ExtensionURI', description:'A URI for the Extension.'},
    {name:'ExtensionReasonCode', description:'A code for reason the Extension is being included.'},
    {name:'ExtensionReason', description:'A description of the reason for the Extension.'},
    ];

  constructor(public dialogRef: MatDialogRef<ServiceGroupExtensionWizardDialogComponent>,
              private dialogFormBuilder: FormBuilder) {

    this.dialogForm = this.dialogFormBuilder.group({ });

    let arrayLength = this.elements.length;
    for (var i = 0; i < arrayLength; i++) {
      this.dialogForm.addControl(this.elements[i].name, new FormControl(''));
    }
  }

  getExtensionXML(){
    var xmlString = '<Extension xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">'
    let arrayLength = this.elements.length;
    for (var i = 0; i < arrayLength; i++) {
      let str = this.dialogForm.get(this.elements[i].name).value;
      if (str && 0 !== str.length) {
        xmlString = xmlString + '\n    <'+this.elements[i].name+'>' + this.xmlSpecialChars(str) + '</'+this.elements[i].name+'>';
      }
    }
    xmlString = xmlString+ '\n' +this.dummyXML+ '\n</Extension>'

    return xmlString;
  }

  xmlSpecialChars(unsafe) {
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }



}
