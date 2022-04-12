import {ChangeDetectorRef, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Observable} from "rxjs/internal/Observable";
import {HttpClient} from "@angular/common/http";
import {SmpConstants} from "../../smp.constants";
import {AlertService} from "../../alert/alert.service";
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {SearchTableEntityStatus} from "../../common/search-table/search-table-entity-status.model";
import {ServiceGroupEditRo} from "../service-group-edit-ro.model";
import {GlobalLookups} from "../../common/global-lookups";
import {ServiceGroupExtensionWizardDialogComponent} from "../service-group-extension-wizard-dialog/service-group-extension-wizard-dialog.component";
import {ServiceGroupValidationRo} from "./service-group-validation-edit-ro.model";
import {DomainRo} from "../../domain/domain-ro.model";
import {ServiceGroupDomainEditRo} from "../service-group-domain-edit-ro.model";
import {ConfirmationDialogComponent} from "../../common/confirmation-dialog/confirmation-dialog.component";
import {SecurityService} from "../../security/security.service";
import {UserRo} from "../../user/user-ro.model";
import {ServiceGroupValidationErrorCodeModel} from "./service-group-validation-error-code.model";

@Component({
  selector: 'service-group-details',
  templateUrl: './service-group-details-dialog.component.html',
  styleUrls: ['./service-group-details-dialog.component.css']
})
export class ServiceGroupDetailsDialogComponent implements OnInit {

  static readonly NEW_MODE = 'New ServiceGroup';
  static readonly EDIT_MODE = 'ServiceGroup Edit';


  participantSchemePattern = '^[a-z0-9]+-[a-z0-9]+-[a-z0-9]+$';
  participantSchemeMessage = '';

  @ViewChild('domainSelector') domainSelector: any;

  editMode: boolean;
  formTitle: string;
  current: ServiceGroupEditRo & { confirmation?: string };
  showSpinner: boolean = false;

  dialogForm: FormGroup;
  extensionObserver: Observable<ServiceGroupValidationRo>;

  extensionValidationMessage: String = null;
  isExtensionValid: boolean = true;
  userList: UserRo[];

  minSelectedListCount(min: number) {
    return (c: AbstractControl): { [key: string]: any } => {
      if (c.value && c.value.length >= min)
        return null;

      return {'minSelectedListCount': {valid: false}};
    }
  }

  multiDomainOn(multidomainOn: boolean) {
    return (c: AbstractControl): { [key: string]: any } => {
      if (c.value && c.value.length < 2 || multidomainOn)
        return null;

      return {'multiDomainError': {valid: false}};
    }
  }

  constructor(public securityService: SecurityService,
              public dialog: MatDialog,
              protected http: HttpClient,
              public dialogRef: MatDialogRef<ServiceGroupDetailsDialogComponent>,
              private alertService: AlertService,
              public lookups: GlobalLookups,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private dialogFormBuilder: FormBuilder,
              private changeDetector: ChangeDetectorRef) {
    this.editMode = this.data.edit;

    this.formTitle = this.editMode ? ServiceGroupDetailsDialogComponent.EDIT_MODE : ServiceGroupDetailsDialogComponent.NEW_MODE;
    this.current = this.editMode
      ? {
        ...this.data.row,
        // copy serviceGroupDomain array
        serviceGroupDomains: [...this.data.row.serviceGroupDomains]
      }
      : {
        id: null,
        participantIdentifier: '',
        participantScheme: '',
        serviceMetadata: [],
        users: [],
        serviceGroupDomains: [],
        extension: '',
        status: SearchTableEntityStatus.NEW,
        extensionStatus: SearchTableEntityStatus.UPDATED,
      };

    if (this.lookups.cachedApplicationConfig) {
      this.participantSchemePattern = this.lookups.cachedApplicationConfig.participantSchemaRegExp != null ?
        this.lookups.cachedApplicationConfig.participantSchemaRegExp : ".*"

      this.participantSchemeMessage = this.lookups.cachedApplicationConfig.participantSchemaRegExpMessage;
    }
    // user is new when reopening the new item in edit mode!
    // allow to change data but warn on error!

    this.dialogForm = this.dialogFormBuilder.group({
      'participantIdentifier': new FormControl({
          value: '',
          disabled: this.current.status !== SearchTableEntityStatus.NEW
        },
        this.current.status === SearchTableEntityStatus.NEW ? Validators.required : null),
      'participantScheme': new FormControl({value: '', disabled: this.current.status !== SearchTableEntityStatus.NEW},
        this.current.status === SearchTableEntityStatus.NEW ?
          [Validators.required, Validators.pattern(this.participantSchemePattern)] : null),
      'serviceGroupDomains': new FormControl({value: []}, [this.minSelectedListCount(1),
        this.multiDomainOn(this.lookups.cachedApplicationConfig.smlParticipantMultiDomainOn)]),
      'users': new FormControl({value: []}, [this.minSelectedListCount(1)]),
      'extension': new FormControl({value: ''}, []),


    });
    // update values
    this.dialogForm.controls['participantIdentifier'].setValue(this.current.participantIdentifier);
    this.dialogForm.controls['participantScheme'].setValue(this.current.participantScheme);
    this.dialogForm.controls['serviceGroupDomains'].setValue(this.current.serviceGroupDomains);
    this.dialogForm.controls['users'].setValue(this.current.users)
    this.dialogForm.controls['extension'].setValue(this.current.extension)
  }

  ngOnInit() {
    // retrieve xml extension for this service group
    if (this.current.status !== SearchTableEntityStatus.NEW && !this.current.extension) {
      // init domains
      this.extensionObserver = this.http.get<ServiceGroupValidationRo>(SmpConstants.REST_PUBLIC_SERVICE_GROUP_ENTITY_EXTENSION.replace('{service-group-id}',this.current.id+""));
      this.extensionObserver.subscribe((res: ServiceGroupValidationRo) => {
        this.dialogForm.get('extension').setValue(res.extension);
        this.current.extension = res.extension;
        // store to initial data - so for next time there will be no need to retrieve data again from server!
        this.data.row.extension = res.extension;
      });
    }

    // detect changes for updated values in mat-selection-list (check change detection operations)
    // else the following error is thrown :xpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:
    // 'aria-selected: false'. Current value: 'aria-selected: true'
    //
    this.changeDetector.detectChanges()
  }


  getDomainCodeClass(domain) {
    let domainWarning = this.getDomainConfigurationWarning(domain);
    if (!!domainWarning) {
      return 'domainWarning';
    }
    return "";
  }
  getDomainConfigurationWarning(domain: DomainRo) {
    let msg =null;
    if (!domain.signatureKeyAlias) {
      msg = "The domain should have a defined signature CertAlias."
    }
    if (this.lookups.cachedApplicationConfig.smlIntegrationOn) {
      if( !domain.smlSmpId || !domain.smlClientCertHeader ){
        msg = (!msg?"": msg+" ") + "For SML integration the SMP SMP ID and SML client certificate must be defined!"
      }
    }
    if(msg) {
       msg = msg + " To use domain first fix domain configuration."
    }
    return msg;
  }

  isDomainProperlyConfigured(domain: DomainRo){
    return !this.getDomainConfigurationWarning(domain);
  }

  submitForm() {
    this.checkValidity(this.dialogForm);


    let request: ServiceGroupValidationRo = {
      serviceGroupId: this.current.id,
      participantScheme: this.dialogForm.controls['participantScheme'].value,
      participantIdentifier: this.dialogForm.controls['participantIdentifier'].value,
      extension: this.dialogForm.controls['extension'].value,
      statusAction: this.editMode ? SearchTableEntityStatus.UPDATED : SearchTableEntityStatus.NEW,
    }
    //
    let validationObservable = this.http.post<ServiceGroupValidationRo>(SmpConstants.REST_SERVICE_GROUP_EXTENSION_VALIDATE, request);
    this.showSpinner = true;
    validationObservable.toPromise().then((res: ServiceGroupValidationRo) => {
      if (res.errorMessage) {

        this.isExtensionValid = false;
        this.showSpinner = false;
        if (res.errorCode == ServiceGroupValidationErrorCodeModel.ERROR_CODE_SERVICE_GROUP_EXISTS) {
          this.dialogForm.controls['participantIdentifier'].setErrors({'dbExist': true});
        } else {
          this.extensionValidationMessage = res.errorMessage;
        }
      } else {
        this.extensionValidationMessage = "Extension is valid!";
        this.isExtensionValid = true;
        this.showSpinner = false;
        // we can close the dialog
        this.dialogRef.close(true);
      }
    }).catch((err) => {
      console.log("Error occurred on Validation Extension: " + err);
    });;


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


  compareTableItemById(item1, item2): boolean {
    return item1.id === item2.id;
  }

  compareDomain(domain: DomainRo, serviceGroupDomain: ServiceGroupDomainEditRo): boolean {
    return domain.id === serviceGroupDomain.domainId;
  }


  public getCurrent(): ServiceGroupEditRo {
    // change this two properties only on new
    if (this.current.status === SearchTableEntityStatus.NEW) {
      this.current.participantIdentifier = this.dialogForm.value['participantIdentifier'];
      this.current.participantScheme = this.dialogForm.value['participantScheme'];
    } else {
      this.current.extensionStatus =
        SearchTableEntityStatus.UPDATED;
    }
    this.current.users = this.dialogForm.value['users'];
    this.current.extension = this.dialogForm.value['extension'];


    let domainOptions = this.domainSelector.options._results;
    domainOptions.forEach(opt => {
      let domValue = opt.value;
      let sgd = this.getServiceGroupDomain(domValue.domainCode);
      // if contains and deselected  - delete
      if (sgd && !opt.selected) {
        this.current.serviceMetadata.forEach(metadata => {
          if (metadata.domainCode === sgd.domainCode) {
            metadata.status = SearchTableEntityStatus.REMOVED;
            metadata.deleted = true;
          }
        });

        var index = this.current.serviceGroupDomains.indexOf(sgd);
        if (index !== -1) this.current.serviceGroupDomains.splice(index, 1);

        // delete service group
      } else if (!sgd && opt.selected) {
        let newDomain: ServiceGroupDomainEditRo = {
          id: null,
          domainId: domValue.id,
          domainCode: domValue.domainCode,
          smlSubdomain: domValue.domainCode,
          smlRegistered: false,
          serviceMetadataCount: 0,
          status: SearchTableEntityStatus.NEW,
        };
        this.current.serviceGroupDomains.push(newDomain);
      }
    });
    return this.current;
  }

  dataChanged() {
    if (this.current.status === SearchTableEntityStatus.NEW) {
      return true;
    }
    return this.current.users !== this.dialogForm.value['users'];
  }

  extensionChanged():boolean {
    return  !this.isEqual(this.current.extension, this.dialogForm.value['extension'].toString());
  }

  onExtensionDelete() {
    this.dialogForm.controls['extension'].setValue("");
  }

  onStartWizardDialog() {

    const formRef: MatDialogRef<any> = this.dialog.open(ServiceGroupExtensionWizardDialogComponent);
    formRef.afterClosed().subscribe(result => {
      if (result) {
        let existingXML = this.dialogForm.controls['extension'].value;
        let val = (existingXML ? existingXML + '\n' : '') + formRef.componentInstance.getExtensionXML();
        this.dialogForm.controls['extension'].setValue(val);
      }
    });
  }

  public onExtensionValidate() {

    let request: ServiceGroupValidationRo = {
      serviceGroupId: this.current.id,
      participantScheme: this.dialogForm.controls['participantScheme'].value,
      participantIdentifier: this.dialogForm.controls['participantIdentifier'].value,
      extension: this.dialogForm.controls['extension'].value,
      statusAction: SearchTableEntityStatus.UPDATED, // do not validate as new  - for new participant id and schema is also validated
    }
    //
    let validationObservable = this.http.post<ServiceGroupValidationRo>(SmpConstants.REST_SERVICE_GROUP_EXTENSION_VALIDATE, request);
    this.showSpinner = true;
    validationObservable.toPromise().then((res: ServiceGroupValidationRo) => {
      if (res.errorMessage) {
        this.extensionValidationMessage = res.errorMessage;
        this.isExtensionValid = false;
        this.showSpinner = false;
      } else {
        this.extensionValidationMessage = "Extension is valid!";
        this.isExtensionValid = true;
        this.showSpinner = false;
      }
    }).catch((err) => {
      console.log("Error occurred on Validation Extension: " + err);
    });

  }

  onPrettyPrintExtension() {

  }

  onDomainSelectionChanged(event) {
    // if deselected warn  serviceMetadata will be deleted
    let domainCode = event.option.value.domainCode;
    if (!event.option.selected) {
      let smdCount = this.getServiceMetadataCountOnDomain(domainCode);
      if (smdCount >0) {
        this.dialog.open(ConfirmationDialogComponent, {
          data: {
            title: "Registered serviceMetadata on domain!",
            description: "Unregistering service group from domain will also delete its serviceMetadata (count: "+smdCount+") from the domain! Do you want to continue?"
          }
        }).afterClosed().subscribe(result => {
          if (!result) {
            event.option.selected = true;
          }
        })
      }
    }
  }

  public getServiceMetadataCountOnDomain(domainCode: String) {
    return this.current.serviceMetadata.filter(smd => {
      return smd.domainCode === domainCode
    }).length;
  }

  public getServiceGroupDomain(domainCode: String) {
    return this.current.serviceGroupDomains ?
      this.current.serviceGroupDomains.find(smd => {
        return smd.domainCode === domainCode
      }) : null;
  }

  isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }
}
