import {AfterViewInit, Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';

import {ResourceDefinitionRo} from "../resource-definition-ro.model";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {SubresourceDefinitionRo} from "../subresource-definition-ro.model";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'resource-details-dialog',
  templateUrl: './resource-details-dialog.component.html'
})
export class ResourceDetailsDialogComponent  implements AfterViewInit  {

  formTitle: string = "";
  current: ResourceDefinitionRo & { confirmation?: string };
  subresourceDefDataSource: MatTableDataSource<ResourceDefinitionRo> = new MatTableDataSource<SubresourceDefinitionRo>();
  @ViewChild('resourcePaginator') resourcePaginator: MatPaginator;
  displayedColumns: string[] = ['name', 'identifier','urlSegment'];

  constructor(
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private translateService: TranslateService) {

    this.current = { ...data.resourceDefinition }
    this.subresourceDefDataSource.data = this.current?.subresourceDefinitions;
    this.formTitle = this.translateService.instant("resource.details.dialog.title");

  }


  ngAfterViewInit() {
    this.subresourceDefDataSource.paginator = this.resourcePaginator;

  }


}
