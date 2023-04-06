import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {ExtensionRo} from "./extension-ro.model";
import {ResourceDefinitionRo} from "./resource-definition-ro.model";
import {SubresourceDefinitionRo} from "./subresource-definition-ro.model";
import {ExtensionService} from "./extension.service";


/** Constants used to fill up our data base. */

const NAMES: string[] = [
  'Oasis SMP 1.0 & 2.0',
  'Peppol SMP',
  'CPPA',
  'Properties',
];

const RESOURCES: string[] = [
  'Oasis SMP 1.0',
  'Oasis SMP 2.0',
  'Peppol SMP',
  'CPPA',
];

@Component({
  moduleId: module.id,
  templateUrl: './extension.component.html',
  styleUrls: ['./extension.component.css']
})
export class ExtensionComponent implements AfterViewInit {
  displayedColumns: string[] = ['name', 'version'];
  dataSource: MatTableDataSource<ExtensionRo> =  new MatTableDataSource();
  selected?: ExtensionRo;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(extensionService: ExtensionService) {

    extensionService.onExtensionsUpdatesEvent().subscribe(updatedExtensions => {
        this.updateExtensions(updatedExtensions);
      }
    );

    extensionService.getExtensions();
  }

  updateExtensions(extensions: ExtensionRo[]){
      this.dataSource.data = extensions;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  public extensionSelected(selected: ExtensionRo) {
    this.selected = selected;
  }

}

/** Builds and returns a new User. */
function createTestExtension(id: number): ExtensionRo {
  const name =
    NAMES[Math.round(Math.random() * (NAMES.length - 1))];

  return {
    extensionId: name + "Id",
    name: name,
    version: "1.0",
    description: "description" + name,
    implementationName: "implementationName " + name,
    resourceDefinitions: createTestResourceDefinitions(Math.round(Math.random() * (100)))
  } as ExtensionRo;
}

function createTestResourceDefinitions(size: number): ResourceDefinitionRo [] {
  return Array.from({length: size}, () => createTestResourceDefinition());
}

function createTestResourceDefinition(): ResourceDefinitionRo {
  const name =
    RESOURCES[Math.round(Math.random() * (RESOURCES.length - 1))];

  return {
    identifier: name + "Id",
    name: name,
    urlSegment: "doc",
    description: "description" + name,
    mimeType: "mimeType " + name,
    subresources: Array.from({length: 4}, () => createTestSubResourceDefinition())
  } as ResourceDefinitionRo;
}

function createTestSubResourceDefinition(): SubresourceDefinitionRo {
  const name =
    RESOURCES[Math.round(Math.random() * (RESOURCES.length - 1))];

  return {
    identifier: name + "Id",
    name: name,
    urlSegment: "subres",
    description: "description" + name,
    mimeType: "mimeType " + name
  } as SubresourceDefinitionRo;
}
