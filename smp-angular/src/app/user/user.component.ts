import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AlertService} from '../alert/alert.service';
import {UserController} from './user-controller';
import {HttpClient} from '@angular/common/http';
import {Role} from "../security/role.model";
import {UserRo} from "./user-ro.model";

@Component({
  templateUrl:'./user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  @ViewChild('roleCellTemplate') roleCellTemplate: TemplateRef<any>

  columnPicker: ColumnPicker = new ColumnPicker();
  userController: UserController;
  filter: any = {};

  constructor(protected http: HttpClient, protected alertService: AlertService, public dialog: MatDialog) {
  }

  ngOnInit() {
    this.userController = new UserController(this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Username',
        prop: 'username',
        canAutoResize: true
      },
      {
        name: 'Certificate',
        prop: 'subject',
        canAutoResize: true
      },
      {
        cellTemplate: this.roleCellTemplate,
        name: 'Role',
        prop: 'role',
        canAutoResize: true
      },
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ['Username', 'Certificate', 'Role'].indexOf(col.name) != -1
    });
  }

  details(row: any) {
    this.userController.showDetails(row);
  }

  getRoleLabel(role: string): Role {
    return Role[role];
  }
}
