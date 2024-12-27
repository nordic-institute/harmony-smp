import {Component, Input} from "@angular/core";
import {DnsQueryRo} from "../../../common/model/dns-query-ro.model";

/**
 * This is a generic dns query panel component for previewing dns results
 */
@Component({
  selector: 'dns-query-panel',
  templateUrl: './dns-query-panel.component.html',
  styleUrls: ['./dns-query-panel.component.css']
})
export class DnsQueryPanelComponent {


  @Input() dnsQeury: DnsQueryRo = null;

  constructor() {
  }

  get dnsQueryNotResolved(): boolean {
    return this.dnsQeury
      && (this.dnsQeury?.dnsEntries === null
        || this.dnsQeury?.dnsEntries.length === 0);
  }
}
