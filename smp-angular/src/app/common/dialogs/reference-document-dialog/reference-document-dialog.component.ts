import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef
} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AlertMessageService} from "../../alert-message/alert-message.service";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";
import {
  ResourceFilterOptionsRo
} from "../../model/resource-filter-options-ro.model";
import {
  ResourceFilterOptionsService
} from "../../services/resource-filter-options.service";
import {MatTableDataSource} from "@angular/material/table";
import {
  SearchReferenceDocument
} from "../../model/search-reference-document-ro.model";
import {MatPaginator} from "@angular/material/paginator";

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

  filterForm: FormGroup;
  dialogTitle = "Search Reference Document";
  domainList: string[];
  filter: any = {};
  displayedColumns: string[] = ['resourceValue', 'resourceScheme', 'subresourceValue', 'subresourceScheme'];
  dataSource: MatTableDataSource<SearchReferenceDocument> = new MatTableDataSource();
  @ViewChild(MatPaginator)
  paginator: MatPaginator;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private resourceFilterOptionsService: ResourceFilterOptionsService,
              public dialogRef: MatDialogRef<ReferenceDocumentDialogComponent>,
              private alertService: AlertMessageService,
              private formBuilder: FormBuilder,
              private translateService: TranslateService
  ) {
    dialogRef.disableClose = true;//disable default close operation
    this.filterForm = this.formBuilder.group({
      'resourceValue': new FormControl({value: null}),
      'resourceScheme': new FormControl({value: null}),
      'subresourceValue': new FormControl({value: null}),
      'subresourceScheme': new FormControl({value: null}),
      "domainCode": new FormControl({value: null})
    });

    this.filterForm.controls['resourceValue'].setValue("");
    this.filterForm.controls['resourceScheme'].setValue("");
    this.filterForm.controls['subresourceValue'].setValue("");
    this.filterForm.controls['subresourceScheme'].setValue("");
    this.filterForm.controls['domainCode'].setValue("");
  }

  async updateDialogTitle() {
    this.dialogTitle = await lastValueFrom(this.translateService.get("reference.document.dialog.title"))
  }

  async ngOnInit() {
    await this.updateDialogTitle();

    this.resourceFilterOptionsService.getResourceFilterOptions$().subscribe({
      next: (value: ResourceFilterOptionsRo) => {
        this.domainList = [''].concat(value.availableDomains || []);
      },
      error: async (err) => {
        this.alertService.exception(await lastValueFrom(this.translateService.get("resource.search.error.fetch.resource.metadata")), err);
      }
    });
  }

  get isDirty(): boolean {
    return this.filterForm.dirty;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  onResetButtonClicked() {
    this.filterForm.reset();
  }

  onSearchButtonClicked() {
    this.filterForm.reset();
  }


  closeDialog() {
    this.dialogRef.close();
  }

  onSaveButtonClicked() {

  }

  get submitButtonEnabled(): boolean {
    return false
  }

}
