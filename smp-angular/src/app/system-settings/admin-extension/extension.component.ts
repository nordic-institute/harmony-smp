import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {ExtensionRo} from "./extension-ro.model";
import {ExtensionService} from "./extension.service";
import {BeforeLeaveGuard} from "../../window/sidenav/navigation-on-leave-guard";


@Component({
  moduleId: module.id,
  templateUrl: './extension.component.html',
  styleUrls: ['./extension.component.css']
})
export class ExtensionComponent implements OnInit, AfterViewInit, BeforeLeaveGuard {
  displayedColumns: string[] = ['name', 'version'];
  dataSource: MatTableDataSource<ExtensionRo> = new MatTableDataSource();
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

  ngOnInit(): void {
    // filter predicate for search the domain
    this.dataSource.filterPredicate =
      (data: ExtensionRo, filter: string) => {
        return !filter || -1 != data.name.toLowerCase().indexOf(filter.trim().toLowerCase())
      };
  }

  updateExtensions(extensions: ExtensionRo[]) {
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

  isDirty(): boolean {
    return false;
  }
}
