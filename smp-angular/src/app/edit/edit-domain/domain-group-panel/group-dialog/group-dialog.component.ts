import {Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {DomainRo} from "../../../../common/model/domain-ro.model";
import {AlertMessageService} from "../../../../common/alert-message/alert-message.service";
import {VisibilityEnum} from "../../../../common/enums/visibility.enum";
import {GroupRo} from "../../../../common/model/group-ro.model";
import {EditDomainService} from "../../edit-domain.service";


@Component({
  templateUrl: './group-dialog.component.html',
  styleUrls: ['./group-dialog.component.css']
})
export class GroupDialogComponent {

  readonly groupVisibilityOptions = Object.keys(VisibilityEnum)
    .filter(el => el !== "Private").map(el => {
      return {key: el, value: VisibilityEnum[el]}
    });
  formTitle = "Group dialog";
  groupForm: FormGroup;

  message: string;
  messageType: string = "alert-error";

  _group: GroupRo;
  _currentDomain: DomainRo;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private editDomainService: EditDomainService,
              public dialogRef: MatDialogRef<GroupDialogComponent>,
              private alertService: AlertMessageService,
              private formBuilder: FormBuilder
  ) {
    dialogRef.disableClose = true;//disable default close operation
    this.formTitle = data.formTitle;
    this._currentDomain = data.domain;

    this.groupForm = formBuilder.group({
      'name': new FormControl({value: null}),
      'description': new FormControl({value: null}),
      'visibility': new FormControl({value: null}),
      '': new FormControl({value: null})
    });
    this.group = data.group;
  }

  get newMode(): boolean {
    return !this._group?.groupId
  }

  get group(): GroupRo {
    let group = {...this._group};
    group.groupName = this.groupForm.get('name').value;
    group.groupDescription = this.groupForm.get('description').value;
    group.visibility = this.groupForm.get('visibility').value;
    return group;
  }

  @Input() set group(value: GroupRo) {
    this._group = value;

    if (!!value) {
      this.groupForm.enable();
      this.groupForm.controls['name'].setValue(value.groupName);
      // control disable enable did not work??
      if (this.newMode) {
        this.groupForm.controls['name'].enable();
      } else {
        this.groupForm.controls['name'].disable();
      }

      this.groupForm.controls['description'].setValue(value.groupDescription);
      this.groupForm.controls['visibility'].setValue(value.visibility);

    } else {
      this.groupForm.disable();
      this.groupForm.controls['name'].setValue("");
      this.groupForm.controls['description'].setValue("");
      this.groupForm.controls['visibility'].setValue("");
    }

    this.groupForm.markAsPristine();
  }

  clearAlert() {
    this.message = null;
    this.messageType = null;
  }


  closeDialog() {
    this.dialogRef.close()
  }

  get submitButtonEnabled(): boolean {
    return this.groupForm.valid && this.groupForm.dirty;
  }

  public onSaveButtonClicked() {

    let group = this.group;
    if (this.newMode) {
      this.createGroup(group);
    } else {
      this.saveGroup(group);
    }
  }

  public createGroup(group: GroupRo) {

    this.editDomainService.createDomainGroupObservable(this._currentDomain.domainId, group).subscribe((group: GroupRo) => {
      if (!!group) {
        this.closeDialog();
      }
    }, (error) => {
      this.alertService.error(error.error?.errorDescription)
    });

  }

  public saveGroup(group: GroupRo) {
    this.editDomainService.saveDomainGroupObservable(this._currentDomain.domainId, group).subscribe((group: GroupRo) => {
      if (!!group) {
        this.closeDialog();
      }
    }, (error) => {
      this.alertService.error(error.error?.errorDescription)
    });
  }


}
