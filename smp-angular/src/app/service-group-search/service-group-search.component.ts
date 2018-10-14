import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ColumnPicker} from '../common/column-picker/column-picker.model';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AlertService} from '../alert/alert.service';
import {ServiceGroupSearchController} from './service-group-search-controller';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs/index";
import {SearchTableResult} from "../common/search-table/search-table-result.model";
import {DomainRo} from "../domain/domain-ro.model";
import {SearchTableEntityStatus} from "../common/search-table/search-table-entity-status.model";

@Component({
  moduleId: module.id,
  templateUrl:'./service-group-search.component.html',
  styleUrls: ['./service-group-search.component.css']
})
export class ServiceGroupSearchComponent implements OnInit {

  @ViewChild('rowExtensionAction') rowExtensionAction: TemplateRef<any>
  @ViewChild('rowSMPUrlLinkAction') rowSMPUrlLinkAction: TemplateRef<any>
  @ViewChild('rowActions') rowActions: TemplateRef<any>;

  columnPicker: ColumnPicker = new ColumnPicker();
  serviceGroupSearchController: ServiceGroupSearchController;
  filter: any = {};
  domainlist: Array<any>;
  domainObserver:  Observable< SearchTableResult> ;
  contextPath: string = location.pathname.substring(0,location.pathname.length -3); // remove /ui s

  constructor(protected http: HttpClient, protected alertService: AlertService, public dialog: MatDialog) {
    this.domainObserver = this.http.get<SearchTableResult>('rest/domain');

    this.domainObserver.subscribe((domains: SearchTableResult) => {
      this.domainlist = new Array(domains.serviceEntities.length)
        .map((v, index) => domains.serviceEntities[index] as DomainRo);

      this.domainlist = domains.serviceEntities.map(serviceEntity => {
        return {...serviceEntity}
      });
    });
  }

  ngOnDestroy() {
  //  this.domainObserver.unsubscribe();
  }

  ngOnInit() {
    this.serviceGroupSearchController = new ServiceGroupSearchController(this.dialog);

    this.columnPicker.allColumns = [
      {
        name: 'Metadata count',
        prop: 'serviceMetadata.length',
        width: 60
      },
      {
        name: 'Participant scheme',
        prop: 'participantScheme',
      },
      {
        name: 'Participant identifier',
        prop: 'participantIdentifier',
        width: 275
      },
      {
        cellTemplate: this.rowSMPUrlLinkAction,
        name: 'SMP Url',
        width: 80,
        sortable: false
      },
      {
        cellTemplate: this.rowExtensionAction,
        name: 'Extension',
        width: 80,
        sortable: false
      }
    ];


    this.columnPicker.selectedColumns = this.columnPicker.allColumns.filter(col => {
      return ["Metadata count", "Participant scheme", "Participant identifier","SMP Url", "Extension"].indexOf(col.name) != -1
    });
  }

  details(row: any) {
    this.serviceGroupSearchController.showDetails(row);

  }
}
