import {Component, EventEmitter, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material';
import {ColumnPicker} from '../../common/column-picker/column-picker.model';
import {ServiceGroupController} from '../service-group-controller';
import {RowLimiter} from '../../common/row-limiter/row-limiter.model';
import {ServiceGroupExtensionDialogComponent} from '../service-group-extension-dialog/service-group-extension-dialog.component';
import {ServiceGroupMetadataDialogComponent} from '../service-group-metadata-dialog/service-group-metadata-dialog.component';

@Component({
  selector: 'app-messagelog-dialog',
  templateUrl: './service-group-metadata-list-dialog.component.html',
  styleUrls: ['./service-group-metadata-list-dialog.component.css']
})
export class ServiceGroupMetadataListDialogComponent implements OnInit {

  @ViewChild('rowActions') rowActions: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  columnActions:any;
  rowLimiter: RowLimiter = new RowLimiter();
  selected = [];

  filter: any = {};
  loading: boolean = false;
  rows = [];
  count: number = 0;
  offset: number = 0;
  //default value
  orderBy: string = null;
  //default value
  asc: boolean = false;

  messageResent = new EventEmitter(false);

  constructor(public dialogRef: MatDialogRef<ServiceGroupMetadataListDialogComponent>, public dialog: MatDialog) {
  }

  ngOnInit() {
    this.columnPicker.allColumns = [
      {
        name: 'Document Id',
        prop: 'documentId',
        width: 200,
      },
      {
        name: 'Document schema',
        prop: 'documentSchema',
        width: 200,
      }, {
        cellTemplate: this.rowActions,
        name: 'Actions',
        width: 60,
        sortable: false
      }
    ];

    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Document Id", "Document schema", "Actions"].indexOf(col.name) != -1
    });

    this.rows = [{
      documentId:"urn:be:ncpb",
      documentSchema:"ehealth-docid-qns",
    },
      {
        documentId:"urn:pl:ncpb",
        documentSchema:"ehealth-docid-qns",
      },
      {
        documentId:"urn:ge:ncpb",
        documentSchema:"ehealth-docid-qns",
      },
      {
        documentId:"urn:si:ncpb",
        documentSchema:"ehealth-docid-qns",
      }];
    this.count=3;
    this.offset=0;
    this.loading = false;
  }

  isRowSelected() {
    if (this.selected)
      return true;

    return false;
  }

  newButtonAction (){this.details()}
  editButtonAction (){this.details()}
  deleteButtonAction (){}

  details() {
    let dialogRef: MatDialogRef<ServiceGroupMetadataDialogComponent> = this.dialog.open(ServiceGroupMetadataDialogComponent);
    //dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }
}
