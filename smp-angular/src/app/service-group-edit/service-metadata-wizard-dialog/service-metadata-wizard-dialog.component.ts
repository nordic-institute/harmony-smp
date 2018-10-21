import {ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {Observable} from "rxjs/internal/Observable";
import {SearchTableResult} from "../../common/search-table/search-table-result.model";
import {HttpClient} from "@angular/common/http";
import {SmpConstants} from "../../smp.constants";
import {UserRo} from "../../user/user-ro.model";
import {AlertService} from "../../alert/alert.service";
import {DomainDetailsDialogComponent} from "../../domain/domain-details-dialog/domain-details-dialog.component";
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SearchTableEntityStatus} from "../../common/search-table/search-table-entity-status.model";
import {DomainRo} from "../../domain/domain-ro.model";
import {ServiceGroupEditRo} from "../service-group-edit-ro.model";
import {ServiceMetadataEditRo} from "../service-metadata-edit-ro.model";
import {GlobalLookups} from "../../common/global-lookups";

@Component({
  selector: 'app-messagelog-details',
  templateUrl: './service-group-extension-wizard-dialog.component.html',
  styleUrls: ['./service-group-extension-wizard-dialog.component.css']
})
export class ServiceGroupExtensionWizardDialogComponent  {
  dialogForm: FormGroup;

  dummyXML: string ="<!-- Custom element is mandatory by OASIS SMP schema.\n    Replace following element with your XML structure. -->\n<ext:example xmlns:ext=\"http://my.namespace.eu\">my mandatory content</ext:example>"


  constructor(public dialogRef: MatDialogRef<ServiceGroupExtensionWizardDialogComponent>,
              private dialogFormBuilder: FormBuilder) {

    this.dialogForm = dialogFormBuilder.group({

      'documentIdentifier': new FormControl({value: ''}, [Validators.required]),
      'documentIdentifierScheme':  new FormControl({value: ''  }, null),
      'processSchema':  new FormControl({value: ''}, [Validators.required]),
      'processIdentifier':  new FormControl({value: ''}, [Validators.required]),
      'endpointUrl':  new FormControl({value: ''}, [Validators.required]),
      'endpointCertificate':  new FormControl({value: ''}, null),

    });
  }

  getExtensionXML(){
    /*
    var xmlString = '<Extension xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">'
    let arrayLength = this.elements.length;
    for (var i = 0; i < arrayLength; i++) {
      let str = this.dialogForm.get(this.elements[i].name).value;
      if (str && 0 !== str.length) {
        xmlString = xmlString + '\n    <'+this.elements[i].name+'>' + this.xmlSpecialChars(str) + '</'+this.elements[i].name+'>';
      }
    }
    xmlString = xmlString+ '\n' +this.dummyXML+ '\n</Extension>'

    return xmlString;*/
  }

  xmlSpecialChars(unsafe) {
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }



}
