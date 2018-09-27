import {Component, OnInit, TemplateRef, ViewChild} from "@angular/core";
import {ColumnPicker} from "../common/column-picker/column-picker.model";
import {MdDialog, MdDialogRef} from "@angular/material";

import {Http} from "@angular/http";
import {AlertService} from "../alert/alert.service";
import {UserController} from "./user-controller";

@Component({
  moduleId: module.id,
  templateUrl:'./user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  @ViewChild('rowMetadataAction') rowMetadataAction: TemplateRef<any>
  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>
  @ViewChild('rowActions') rowActions: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  userController: UserController;
  filter: any = {};

  constructor(protected http: Http, protected alertService: AlertService, public dialog: MdDialog) {
  }

  ngOnInit() {
    this.userController = new UserController(this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Username',
        prop: 'username',
        width: 275
      },
      {
        name: 'isAdmin',
        prop: 'isadmin',
        width: 40
      }
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Username", "isAdmin"].indexOf(col.name) != -1
    });
  }

  details(row: any) {
    this.userController.showDetails(row);

  }
}
