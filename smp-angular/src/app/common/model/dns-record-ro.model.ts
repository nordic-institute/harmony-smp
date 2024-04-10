import {DnsTypeEnum} from "../enums/dns-type.enum";

export interface DnsRecordRo {
  dnsType:DnsTypeEnum;
  rawRecordResult?:string;
  naptrService?:string;
  value?:string;
}
