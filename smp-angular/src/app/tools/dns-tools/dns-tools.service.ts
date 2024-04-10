import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {SmpConstants} from "../../smp.constants";
import {DnsQueryRo} from "../../common/model/dns-query-ro.model";
import {Observable} from "rxjs";
import {DnsQueryRequestRo} from "../../common/model/dns-query-request-ro.model";

@Injectable()
export class DnsToolsService {

  constructor(private http: HttpClient) {
  }

  executeDnsLookup(topDNSDomain:string, value: string, scheme: string): Observable<DnsQueryRo[]> {
    console.log('getHashValues: val: ' + value + ' scheme:' + scheme);

    let resource: DnsQueryRequestRo =  {
      identifierValue: value,
      identifierScheme: scheme,
      topDnsDomain: topDNSDomain,
    };
    let headers: HttpHeaders = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<DnsQueryRo[]>(SmpConstants.REST_PUBLIC_DNS_TOOLS_GEN_QUERY,
      resource, {headers});
  }
}
