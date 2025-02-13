/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
import {
  AfterViewInit,
  Component,
  forwardRef,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  ControlContainer,
  ControlValueAccessor,
  FormBuilder,
  FormControl,
  FormControlDirective,
  FormGroup,
  NG_VALUE_ACCESSOR
} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {
  BeforeLeaveGuard
} from "../../../../window/sidenav/navigation-on-leave-guard";
import {
  DocumentConfigurationRo
} from "../../../model/document-configuration-ro.model";
import {
  ReferenceDocumentDialogComponent
} from "../../../dialogs/reference-document-dialog/reference-document-dialog.component";
import {
  DocumentReferenceType
} from "../../../enums/documetn-reference-type.enum";
import {ResourceRo} from "../../../model/resource-ro.model";
import {SubresourceRo} from "../../../model/subresource-ro.model";
import {
  SearchReferenceDocument
} from "../../../model/search-reference-document-ro.model";

/**
 * Component to display the properties of a document in a table. The properties can be edited and saved.
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Component({
  selector: 'document-configuration-panel',
  templateUrl: './document-configuration-panel.component.html',
  styleUrls: ['./document-configuration-panel.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DocumentConfigurationPanelComponent),
      multi: true
    }
  ]
})
export class DocumentConfigurationPanelComponent implements OnInit, AfterViewInit, BeforeLeaveGuard, ControlValueAccessor {

  private onChangeCallback: (_: any) => void = () => {
  };

  _contextPath: string = location.pathname.substring(0, location.pathname.length - 3); // remove /ui s
  _documentConfiguration: DocumentConfigurationRo;
  documentConfigurationForm: FormGroup;
  @ViewChild(FormControlDirective, {static: true})
  formControlDirective: FormControlDirective;
  @Input() formControl: FormControl;
  @Input() formControlName: string;
  @Input() resource: ResourceRo;
  @Input() subresource: SubresourceRo;

  constructor(
    public dialog: MatDialog,
    private controlContainer: ControlContainer,
    private formBuilder: FormBuilder) {

    this.documentConfigurationForm = this.formBuilder.group({
      'name': new FormControl({value: null}),
      'mimeType': new FormControl({value: null}),
      'publishedVersion': new FormControl({value: null}),
      'sharingEnabled': new FormControl({value: null}),
      'allVersions': new FormControl({value: null}),
      'referenceDocumentId': new FormControl({value: null}),
      'referenceDocumentName': new FormControl({value: null}),
      'referenceDocumentUrl': new FormControl({value: null}),
    });
  }

  ngOnInit(): void {
    // subscribe to form changes and propagate them to the parent
    this.documentConfigurationForm.valueChanges.subscribe(() => {
      this.onChangeCallback(this.documentConfiguration);
    });
  }


  get control() {
    return this.formControl || this.controlContainer.control.get(this.formControlName);
  }

  /**
   * Implementation of the ControlValueAccessor method to  write value to the component.
   * @param eventList
   */
  writeValue(data: DocumentConfigurationRo): void {
    this.documentConfiguration = data;
    this.updateShareCheckboxStatus();
  }

  ngAfterViewInit() {

  }


  isDirty(): boolean {
    return this.documentConfigurationForm.dirty;
  }

  registerOnChange(fn: any): void {
    this.onChangeCallback = fn;

  }

  registerOnTouched(fn: any): void {
    // not implemented
  }

  setDisabledState(isDisabled: boolean): void {
    // not implemented
  }


  @Input() set documentConfiguration(value: DocumentConfigurationRo) {
    this._documentConfiguration = value;
    this.documentConfigurationForm.disable();

    if (!!value) {
      this.documentConfigurationForm.controls['name'].setValue(value.name);
      this.documentConfigurationForm.controls['mimeType'].setValue(value.mimeType);
      this.documentConfigurationForm.controls['publishedVersion'].setValue(value.publishedVersion);
      this.documentConfigurationForm.controls['allVersions'].setValue(value.allVersions);
      this.documentConfigurationForm.controls['sharingEnabled'].setValue(value.sharingEnabled);

      this.documentConfigurationForm.controls['referenceDocumentId'].setValue(value.referenceDocumentId);
      this.documentConfigurationForm.controls['referenceDocumentName'].setValue(value.referenceDocumentName);
      this.documentConfigurationForm.controls['referenceDocumentUrl'].setValue(value.referenceDocumentUrl);

      // allow to change name and sharing attribute
      this.documentConfigurationForm.controls['name'].enable();
      this.documentConfigurationForm.controls['sharingEnabled'].enable();
    } else {
      this.documentConfigurationForm.controls['name'].setValue("");
      this.documentConfigurationForm.controls['mimeType'].setValue("");
      this.documentConfigurationForm.controls['publishedVersion'].setValue("");
      this.documentConfigurationForm.controls['allVersions'].setValue([]);
      this.documentConfigurationForm.controls['sharingEnabled'].setValue(false);
      this.documentConfigurationForm.controls['referenceDocumentId'].setValue("");
      this.documentConfigurationForm.controls['referenceDocumentName'].setValue("");
      this.documentConfigurationForm.controls['referenceDocumentUrl'].setValue("");
    }
    this.documentConfigurationForm.markAsPristine();
  }

  get documentConfiguration(): DocumentConfigurationRo {
    let docConf: DocumentConfigurationRo = {...this._documentConfiguration};

    // set new updated values
    docConf.sharingEnabled = this.documentConfigurationForm.controls['sharingEnabled'].value;
    docConf.name = this.documentConfigurationForm.controls['name'].value;
    docConf.referenceDocumentId = this.documentConfigurationForm.controls['referenceDocumentId'].value;
    docConf.referenceDocumentName = this.documentConfigurationForm.controls['referenceDocumentName'].value;
    docConf.referenceDocumentUrl = this.documentConfigurationForm.controls['referenceDocumentUrl'].value;

    return docConf;
  }

  get hasReferenceDocument(): boolean {
    let val = this.documentConfigurationForm.controls['referenceDocumentId']?.value;
    return !!val && val.length > 0;
  }

  get hasReferenceDocumentUrl(): boolean {
    return !!this.documentConfigurationForm.controls['referenceDocumentUrl']?.value;
  }

  getReferencePartialURL() {
    if (this.hasReferenceDocumentUrl) {
      return this._contextPath + this.documentConfigurationForm.controls['referenceDocumentUrl'].value;
    }
    return "";
  }


  onShowSearchDialogClicked() {
    this.dialog.open(ReferenceDocumentDialogComponent, {
      data: {
        targetType: !this.subresource? DocumentReferenceType.RESOURCE:DocumentReferenceType.SUBRESOURCE,
        targetResource: this.resource,
        targetSubresource: this.subresource,
      }
    }).afterClosed().subscribe((value: SearchReferenceDocument) => {
      if (!!value) {
        this.documentConfigurationForm.controls['referenceDocumentId'].setValue(value.documentId);
        this.documentConfigurationForm.controls['referenceDocumentName'].setValue(value.documentName);
        this.documentConfigurationForm.controls['referenceDocumentUrl'].setValue(value.referenceUrl);
        this.onChangeCallback(this.documentConfiguration);
        this.updateShareCheckboxStatus();
      }
    });
  }

  onSharingEnabledChanged() {
    this.updateShareCheckboxStatus();
  }

  updateShareCheckboxStatus() {
    if (this.hasReferenceDocument) {
      this.documentConfigurationForm.controls['sharingEnabled'].setValue(false);
      this.documentConfigurationForm.controls['sharingEnabled'].disable();
    } else {
      this.documentConfigurationForm.controls['sharingEnabled'].enable();
    }
  }

  onClearReferenceDocument(): void {
    this.documentConfigurationForm.controls['referenceDocumentId'].setValue("");
    this.documentConfigurationForm.controls['referenceDocumentName'].setValue("");
    this.documentConfigurationForm.controls['referenceDocumentUrl'].setValue("");
    this.updateShareCheckboxStatus()
  }

  get disableShowReferenceDocumentButton(): boolean {
    return this.hasReferenceDocument || this.documentConfigurationForm.controls['sharingEnabled'].value;
  }

}
