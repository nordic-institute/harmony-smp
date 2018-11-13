import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {SmpConstants} from "../../smp.constants";
import {ServiceMetadataEditRo} from "../service-metadata-edit-ro.model";
import {CertificateService} from "../../user/certificate.service";

@Component({
  selector: 'service-metadata-wizard-dialog',
  templateUrl: './service-metadata-wizard-dialog.component.html',
  styleUrls: ['./service-metadata-wizard-dialog.component.css']
})
export class ServiceMetadataWizardDialogComponent {

  static readonly NEW_MODE = 'New ServiceMetadata XML';
  static readonly EDIT_MODE = 'Edit ServiceMetadata XML';

  editMode: boolean;
  current: ServiceMetadataEditRo & { confirmation?: string };
  dialogForm: FormGroup;
  certificateValidationMessage: string;
  isCertificateValid: string;
  selectedFile: File;

  // dummyXML: string = "<!-- Custom element is mandatory by OASIS SMP schema.\n    Replace following element with your XML structure. -->\n<ext:example xmlns:ext=\"http://my.namespace.eu\">my mandatory content</ext:example>"

  constructor(
    private http: HttpClient,
    private dialogRef: MatDialogRef<ServiceMetadataWizardDialogComponent>,
    private dialogFormBuilder: FormBuilder,
    private certificateService: CertificateService,
  ) {

    this.dialogForm = dialogFormBuilder.group({
      'documentIdentifier': new FormControl({value: ''}, [Validators.required]),
      'documentIdentifierScheme': new FormControl({value: ''}, null),
      'processScheme': new FormControl({value: ''}, [Validators.required]),
      'processIdentifier': new FormControl({value: ''}, [Validators.required]),
      'endpointUrl': new FormControl({value: ''}, [Validators.required]),
      'endpointCertificate': new FormControl({value: ''}, null),

    });
  }

  onFileChanged(event) {
    this.selectedFile = event.target.files[0]
  }

  onUpload() {
    // this.http is the injected HttpClient
    this.certificateService.uploadCertificate$(this.selectedFile)
      .subscribe(event => {
        console.log(event); // handle event here
      });
  }

  getExtensionXML() {
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
