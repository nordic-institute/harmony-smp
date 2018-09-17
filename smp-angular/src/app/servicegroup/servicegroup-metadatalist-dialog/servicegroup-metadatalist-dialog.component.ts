import {Component, EventEmitter, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {MdDialog, MdDialogRef} from "@angular/material";
import {ColumnPickerBase} from "../../common/column-picker/column-picker-base";
import {ServiceGroupController} from "../servicegroup-controller";
import {RowLimiterBase} from "../../common/row-limiter/row-limiter-base";
import {ServiceGroupExtensionDialogComponent} from "../servicegroup-extension-dialog/servicegroup-extension-dialog.component";
import {ServicegroupMetadataDialogComponent} from "../servicegroup-metadata-dialog/servicegroup-metadata-dialog.component";

@Component({
  selector: 'app-messagelog-dialog',
  templateUrl: './servicegroup-metadatalist-dialog.component.html',
  styleUrls: ['./servicegroup-metadatalist-dialog.component.css']
})
export class ServicegroupMetadatalistDialogComponent implements OnInit {

  @ViewChild('rowActions') rowActions: TemplateRef<any>;

  columnPicker: ColumnPickerBase = new ColumnPickerBase();
  columnActions:any;
  rowLimiter: RowLimiterBase = new RowLimiterBase();
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

  constructor(public dialogRef: MdDialogRef<ServicegroupMetadatalistDialogComponent>, public dialog: MdDialog) {
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
    let dialogRef: MdDialogRef<ServicegroupMetadataDialogComponent> = this.dialog.open(ServicegroupMetadataDialogComponent);
    //dialogRef.componentInstance.servicegroup = row;
    dialogRef.afterClosed().subscribe(result => {
      //Todo:
    });
  }
}
