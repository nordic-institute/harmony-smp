import {Component, Inject} from '@angular/core';
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

@Component({
  selector: 'app-messagelog-details',
  templateUrl: './service-group-details-dialog.component.html',
  styleUrls: ['./service-group-details-dialog.component.css']
})
export class ServiceGroupDetailsDialogComponent {

  static readonly NEW_MODE = 'New ServiceGroup';
  static readonly EDIT_MODE = 'ServiceGroup Edit';


  userObserver:  Observable< SearchTableResult> ;
  domainObserver:  Observable< SearchTableResult> ;
  userlist: Array<UserRo> = [];
  editMode: boolean;
  formTitle: string;
  current: ServiceGroupEditRo & { confirmation?: string };

  dialogForm: FormGroup;
  dialogFormBuilder: FormBuilder;
  formControlUsers: FormControl;
  /*
  selectedDomain: DomainRo;
  domainList: Array<any>;
*/

  minCountOwners(min: number) {
    return (c: AbstractControl): {[key: string]: any} => {
      if (c.value.length >= min)
        return null;

      return { 'minCountOwners': {valid: false }};
    }
  }

  constructor(protected http: HttpClient, public dialogRef: MatDialogRef<ServiceGroupDetailsDialogComponent>,
                private alertService: AlertService,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private fb: FormBuilder) {
    // init user list

   this.userObserver = this.http.get<SearchTableResult>(SmpConstants.REST_USER);
    this.userObserver.subscribe((users: SearchTableResult) => {
      this.userlist = new Array(users.serviceEntities.length)
        .map((v, index) => users.serviceEntities[index] as UserRo);

      this.userlist = users.serviceEntities.map(serviceEntity => {
        return {...<UserRo>serviceEntity}
      });
      this.updateData();
    });
    /*
    // init domains
    this.domainObserver = this.http.get<SearchTableResult>(SmpConstants.REST_DOMAIN);
    this.domainObserver.subscribe((domains: SearchTableResult) => {
      this.domainList = new Array(domains.serviceEntities.length)
        .map((v, index) => {domains.serviceEntities[index] as DomainRo;
          if ( this.editMode && v.domainCode ===data.row.domainCode) {
            this.selectedDomain = v as DomainRo;
          }
        });

      this.domainList = domains.serviceEntities.map(serviceEntity => {
        return {...serviceEntity}
      });
    });
*/
    this.dialogFormBuilder = fb;
    this.editMode = data.edit;
    this.formTitle = this.editMode ?  ServiceGroupDetailsDialogComponent.EDIT_MODE: ServiceGroupDetailsDialogComponent.NEW_MODE;
    this.current = this.editMode
      ? {
        ...data.row,
      }
      : {
        id: null,
        participantIdentifier: '',
        participantScheme:  '',
        serviceMetadata:[],
        users:[],
        domainCode:'',
        status: SearchTableEntityStatus.NEW,
      };

    this.dialogForm = this.dialogFormBuilder.group({

      'participantIdentifier': new FormControl({value: this.current.participantIdentifier, disabled: this.editMode}, this.editMode ? Validators.required : null),
      'participantScheme': new FormControl({value: this.current.participantScheme, disabled: this.editMode},  this.editMode ? Validators.required : null),
      'domainCode': new FormControl({value: this.current.domainCode},this.editMode ? Validators.required : null),

      //'users': [new FormControl({value: this.current.users}, [ this.minCountOwners(1)] )],
    });


  }

  updateData(){
    this.formControlUsers = new FormControl(this.current.users);
    this.formControlUsers.setValidators( [ this.minCountOwners(1)]);

    this.dialogForm.addControl("users",this.formControlUsers );

  }


  submitForm() {
    this.checkValidity(this.dialogForm)
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


  updateParticipantIdentifier(event) {
    this.current.participantIdentifier = event.target.value;
  }
  updateParticipantScheme(event) {
    this.current.participantScheme = event.target.value;
  }

  userListChanged(usersSelected, event){
    this.current.users = [];
    for(let usr of usersSelected) {
      this.current.users.push(usr.value);
    }
  }
  compareUserById(item1, item2): boolean{
    return item1.id=== item2.id;
  }

  isSelected(id): boolean {
    return !!this.current.users.find(user => user.id===id);
  }
}
