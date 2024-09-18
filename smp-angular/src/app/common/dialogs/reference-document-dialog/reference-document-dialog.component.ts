import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AlertMessageService} from "../../alert-message/alert-message.service";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom, Observer} from "rxjs";
import {MatTableDataSource} from "@angular/material/table";
import {
  SearchReferenceDocument
} from "../../model/search-reference-document-ro.model";
import {MatPaginator} from "@angular/material/paginator";
import {
  ReferenceDocumentService
} from "../../services/reference-document.service";
import {
  EditResourceService
} from "../../../edit/edit-resources/edit-resource.service";
import {ResourceRo} from "../../model/resource-ro.model";
import {DocumentReferenceType} from "../../enums/documetn-reference-type.enum";
import {TableResult} from "../../model/table-result.model";
import {SubresourceRo} from "../../model/subresource-ro.model";

/**
 * Dialog component for searching reference documents
 *
 * @since 5.1
 * @author Joze RIHTARSIC
 */
@Component({
  templateUrl: './reference-document-dialog.component.html',
  styleUrls: ['./reference-document-dialog.component.css']
})
export class ReferenceDocumentDialogComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;

  filterForm: FormGroup;
  dialogTitle = "Search Reference Document";
  domainList: string[];
  _resourceDisplayedColumns: string[] = ['resourceValue', 'resourceScheme', 'urlLinkAction'];
  _subresourceDisplayedColumns: string[] = ['resourceValue', 'resourceScheme', 'subresourceValue', 'subresourceScheme', 'urlLinkAction'];
  dataSource: MatTableDataSource<SearchReferenceDocument> = new MatTableDataSource();
  _contextPath: string = location.pathname.substring(0, location.pathname.length - 3); // remove /ui s
  targetResource: ResourceRo
  targetSubresource: SubresourceRo
  targetType: DocumentReferenceType = DocumentReferenceType.RESOURCE;
  selectedRow: SearchReferenceDocument;

  // ----
  // defined observers
  loadReferenceDocumentsObserver: Partial<Observer<TableResult<SearchReferenceDocument>>> = {
    next: (data) => {
      this.dataSource.data = data.serviceEntities;
      this.dataSource.paginator.length = data.count;
    },
    error: (error) => {
      this.alertService.error("Error while searching reference documents");
    }
  }

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private referenceDocumentService: ReferenceDocumentService,
              public dialogRef: MatDialogRef<ReferenceDocumentDialogComponent>,
              private alertService: AlertMessageService,
              private formBuilder: FormBuilder,
              private translateService: TranslateService,
              private editResourceService: EditResourceService
  ) {
    dialogRef.disableClose = true;//disable default close operation
    this.filterForm = this.formBuilder.group({
      'resourceValue': new FormControl({value: null}),
      'resourceScheme': new FormControl({value: null}),
      'subresourceValue': new FormControl({value: null}),
      'subresourceScheme': new FormControl({value: null}),
    });

    this.filterForm.controls['resourceValue'].setValue("");
    this.filterForm.controls['resourceScheme'].setValue("");
    this.filterForm.controls['subresourceValue'].setValue("");
    this.filterForm.controls['subresourceScheme'].setValue("");

    this.targetResource = data.targetResource
    this.targetSubresource = data.targetSubresource
    this.targetType = data.targetType
  }

  async updateDialogTitle() {
    this.dialogTitle = await lastValueFrom(this.translateService.get("reference.document.dialog.title"))
  }

  async ngOnInit() {
    await this.updateDialogTitle();

  }

  get isDirty(): boolean {
    return this.filterForm.dirty;
  }

  get displayedColumns(): string[] {
    return this.showSubresourceFields ? this._subresourceDisplayedColumns : this._resourceDisplayedColumns;
  }

  get showSubresourceFields(): boolean {
    return this.targetType === DocumentReferenceType.SUBRESOURCE;
  }

  createURL(row: SearchReferenceDocument) {
    return this._contextPath + row.referenceUrl;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  onResetButtonClicked() {
    this.filterForm.reset();
  }

  onSearchButtonClicked() {
    // submit form data as a filter.
    let filter = this.filterForm.value;
    if (this.targetType === DocumentReferenceType.RESOURCE) {
      this.referenceDocumentService.getSearchResourceDocumentReferencesObservable$(filter, this.targetResource)
        .subscribe(this.loadReferenceDocumentsObserver);
    } else {
      this.referenceDocumentService.getSearchSubresourceDocumentReferencesObservable$(filter, this.targetResource, this.targetSubresource)
        .subscribe(this.loadReferenceDocumentsObserver);
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }

  onSaveButtonClicked() {
    this.dialogRef.close(this.selectedRow);
  }

  get submitButtonDisabled(): boolean {
    return !this.selectedRow
  }

  public onRowClicked(row: SearchReferenceDocument) {
    this.selectedRow = row;
  }

}
