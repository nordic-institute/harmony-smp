import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {ExtensionRo} from "../extension-ro.model";
import {MatTableDataSource} from "@angular/material/table";
import {ResourceDefinitionRo} from "../resource-definition-ro.model";
import {MatPaginator} from "@angular/material/paginator";
import {MatDialog} from "@angular/material/dialog";
import {ResourceDetailsDialogComponent} from "../resource-details-dialog/resource-details-dialog.component";


@Component({
  selector: 'extension-panel',
  templateUrl: './extension-panel.component.html',
  styleUrls: ['./extension-panel.component.scss']
})
export class ExtensionPanelComponent implements AfterViewInit {

  @ViewChild('resourcePaginator') resourcePaginator: MatPaginator;
  displayedColumns: string[] = ['name', 'identifier', 'urlSegment'];

  _extension: ExtensionRo;

  resourceDefDataSource: MatTableDataSource<ResourceDefinitionRo> = new MatTableDataSource<ResourceDefinitionRo>();

  selected?: ResourceDefinitionRo;

  constructor(public dialog: MatDialog) {
  }

  ngAfterViewInit() {
    this.resourceDefDataSource.paginator = this.resourcePaginator;
  }

  get extension(): ExtensionRo {
    return this._extension;
  }

  @Input() set extension(value: ExtensionRo) {
    this._extension = value;
    this.resourceDefDataSource.data = value?.resourceDefinitions;
  }

  onShowSelectedResourceDetails() {
    this.dialog.open(ResourceDetailsDialogComponent, {
      data: {
        resourceDefinition: this.selected
      }
    });
  }

  public resourceDefinitionSelected(selected: ResourceDefinitionRo) {
    this.selected = selected;
  }

}
