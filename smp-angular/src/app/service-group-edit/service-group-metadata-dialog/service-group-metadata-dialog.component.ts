import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {DomainRo} from "../../domain/domain-ro.model";
import {AlertService} from "../../alert/alert.service";
import {DomainDetailsDialogComponent} from "../../domain/domain-details-dialog/domain-details-dialog.component";
import {SearchTableEntityStatus} from "../../common/search-table/search-table-entity-status.model";
import {ServiceMetadataEditRo} from "../service-metadata-edit-ro.model";

@Component({
  selector: 'app-messagelog-dialog',
  templateUrl: './service-group-metadata-dialog.component.html',
  styleUrls: ['./service-group-metadata-dialog.component.css']
})
export class ServiceGroupMetadataDialogComponent {

  static readonly NEW_MODE = 'New ServiceMetadata';
  static readonly EDIT_MODE = 'Domain ServiceMetadata';

  editMode: boolean;
  formTitle: string;
  current: ServiceMetadataEditRo & { confirmation?: string };
  dialogForm: FormGroup;


  constructor(private dialogRef: MatDialogRef<ServiceGroupMetadataDialogComponent>,
              private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {

    this.editMode = data.edit;
    this.formTitle = this.editMode ?  ServiceGroupMetadataDialogComponent.EDIT_MODE: ServiceGroupMetadataDialogComponent.NEW_MODE;
    this.current = this.editMode
      ? {
        ...data.row,
      }
      : {
        documentIdentifier: '',
        documentIdentifierScheme: '',
        smlSubdomain: '',
        domainCode: '',
        processSchema: '',
        processIdentifier: '',
        endpointUrl:  '',
        endpointCertificate: '',
        status: SearchTableEntityStatus.NEW,
      };

    this.dialogForm = fb.group({

      'documentIdentifier': new FormControl({value: this.current.documentIdentifier}, [Validators.required]),
      'documentIdentifierScheme':  new FormControl({value: this.current.documentIdentifierScheme  }, null),
      'domainCode':  new FormControl({value: this.current.domainCode}, [Validators.required]),
      'processSchema':  new FormControl({value: this.current.processSchema}, [Validators.required]),
      'processIdentifier':  new FormControl({value: this.current.processIdentifier}, [Validators.required]),
      'endpointUrl':  new FormControl({value: this.current.endpointUrl}, [Validators.required]),
      'endpointCertificate':  new FormControl({value: this.current.endpointCertificate}, null),

    });

  }

}
