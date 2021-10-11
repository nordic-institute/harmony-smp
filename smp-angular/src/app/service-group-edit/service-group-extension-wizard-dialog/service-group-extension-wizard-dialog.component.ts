import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'service-group-extension-wizard',
  templateUrl: './service-group-extension-wizard-dialog.component.html',
  styleUrls:  ['./service-group-extension-wizard-dialog.component.css']
})
export class ServiceGroupExtensionWizardDialogComponent  {
  dialogForm: FormGroup;

  dummyXML: string ="<!-- Custom element is mandatory by OASIS SMP schema.\n    Replace following element with your XML structure. -->\n<ext:example xmlns:ext=\"http://my.namespace.eu\">my mandatory content</ext:example>"

  elements: any[] = [
    {name:'ExtensionID', description:'An identifier for the Extension assigned by the creator of the extension.', type:'text'},
    {name:'ExtensionName', description:'A name for the Extension assigned by the creator of the extension.', type:'text'},
    {name:'ExtensionAgencyID', description:'An agency that maintains one or more Extensions.', type:'text'},
    {name:'ExtensionAgencyName', description:'The name of the agency that maintains the Extension.', type:'text'},
    {name:'ExtensionAgencyURI', description:'A URI for the Agency that maintains the Extension.', type:'url'},
    {name:'ExtensionVersionID', description:'The version of the Extension.', type:'text'},
    {name:'ExtensionURI', description:'A URI for the Extension.', type:'url'},
    {name:'ExtensionReasonCode', description:'A code for reason the Extension is being included.', type:'text'},
    {name:'ExtensionReason', description:'A description of the reason for the Extension.', type:'text'},
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
