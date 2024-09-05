import {Component} from "@angular/core";
import {DnsToolsService} from "./dns-tools.service";
import {DnsQueryRo} from "../../common/model/dns-query-ro.model";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {GlobalLookups} from "../../common/global-lookups";
import {
  AlertMessageService
} from "../../common/alert-message/alert-message.service";

@Component({
  templateUrl: './dns-tools.component.html',
  styleUrls: ['./dns-tools.component.css']
})
export class DnsToolsComponent {

  displayedColumns: string[] = ['dnsqueryColumn'];
  dnsToolsForm: FormGroup;
  private _result: DnsQueryRo[];

  participantSchemePattern: string = '^[a-z0-9]+-[a-z0-9]+-[a-z0-9]+$';
  participantSchemeMessage: string;
  submitInProgress: boolean = false;

  constructor(private dnsToolsService: DnsToolsService,
              private lookups: GlobalLookups,
              private alertService: AlertMessageService,
              private formBuilder: FormBuilder) {

    this.dnsToolsForm = formBuilder.group({
      // common values
      'resourceIdentifier': new FormControl({value: null}, [Validators.required]),
      'resourceScheme': new FormControl({value: null}, [Validators.pattern(this.participantSchemePattern)]),
      'dnsTopDomain': [''],
    });
    this.clearData();
    // set the default system validation values
    if (this.lookups.cachedApplicationConfig) {
      this.participantSchemePattern = this.lookups.cachedApplicationConfig.participantSchemaRegExp != null ?
        this.lookups.cachedApplicationConfig.participantSchemaRegExp : ".*"
      this.participantSchemeMessage = this.lookups.cachedApplicationConfig.participantSchemaRegExpMessage;
    }

    if (!!lookups.cachedApplicationConfig.partyIDSchemeMandatory) {
      this.dnsToolsForm.controls['resourceScheme'].addValidators(Validators.required);
    }
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
    ).subscribe({
      next: (res: DnsQueryRo[]): void => {
        this._result = res;
      }, error: (err: any): void => {
        this.alertService.error(err);
      }
    })
  }

  get submitButtonEnabled(): boolean {
    return this.dnsToolsForm.valid && this.dnsToolsForm.dirty && !this.submitInProgress;
  }

  clearData(): void {
    this.dnsToolsForm.get('dnsTopDomain').setValue("");
    this.dnsToolsForm.get('resourceIdentifier').setValue("");
    this.dnsToolsForm.get('resourceScheme').setValue("");
    this._result = [];
    this.dnsToolsForm.markAsPristine();
    this.dnsToolsForm.markAsUntouched();
  }
}
