import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup} from "@angular/forms";
import {ResourceRo} from "../../../common/model/resource-ro.model";

@Component({
  selector: 'service-group-extension-wizard',
  templateUrl: './document-wizard-dialog.component.html',
  styleUrls:  ['./document-wizard-dialog.component.css']
})
export class DocumentWizardDialogComponent {
  dialogForm: UntypedFormGroup;
  resource: ResourceRo;

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

  constructor(public dialogRef: MatDialogRef<DocumentWizardDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private dialogFormBuilder: UntypedFormBuilder) {

    this.dialogForm = this.dialogFormBuilder.group({ });
    this.resource = data.resource;

    let arrayLength = this.elements.length;
    for (var i = 0; i < arrayLength; i++) {
      this.dialogForm.addControl(this.elements[i].name, new UntypedFormControl(''));
    }
  }

  getExtensionXML(){
    let xmlString ='<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n' +
      '<ServiceGroup xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05" xmlns:ns2="http://www.w3.org/2000/09/xmldsig#">\n' +
      '   <ParticipantIdentifier scheme="'+this.resource.identifierScheme+'">'+this.resource.identifierValue+'</ParticipantIdentifier>\n' +
      '   <ServiceMetadataReferenceCollection/>\n' +
      '    <Extension xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">';
    let arrayLength = this.elements.length;
    for (var i = 0; i < arrayLength; i++) {
      let str = this.dialogForm.get(this.elements[i].name).value;
      if (str && 0 !== str.length) {
        xmlString = xmlString + '\n    <'+this.elements[i].name+'>' + this.xmlSpecialChars(str) + '</'+this.elements[i].name+'>';
      }
    }
    xmlString = xmlString+ '\n' +this.dummyXML+ '\n    </Extension>\n</ServiceGroup>'
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
