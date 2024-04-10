import {Component} from "@angular/core";
import {DnsToolsService} from "./dns-tools.service";
import {DnsQueryRo} from "../../common/model/dns-query-ro.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {GlobalLookups} from "../../common/global-lookups";

@Component({
  templateUrl: './dns-tools.component.html',
})
export class DnsToolsComponent {

  displayedColumns: string[] = ['dnsqueryColumn'];
  dnsToolsForm: FormGroup;
  private _result: DnsQueryRo[];

  constructor(private dnsToolsService: DnsToolsService,
              private lookups: GlobalLookups,
              private formBuilder: FormBuilder) {

    this.dnsToolsForm = formBuilder.group({
      // common values
      'resourceIdentifier': ['', Validators.required],
      'resourceScheme': [''],
      'dnsTopDomain': [''],
    });
  }



  get result(): DnsQueryRo[] {
    return this._result;
  }

  generateLookupQuery() {
    console.log('submit');
    this.dnsToolsService.executeDnsLookup(
      this.dnsToolsForm.get('dnsTopDomain').value,
      this.dnsToolsForm.get('resourceIdentifier').value,
      this.dnsToolsForm.get('resourceScheme').value
    ).subscribe((res: DnsQueryRo[]) => {
      this._result = res;
    })
  }
}
