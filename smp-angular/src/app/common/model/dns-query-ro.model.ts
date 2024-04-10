import {DnsTypeEnum} from "../enums/dns-type.enum";
import {DnsRecordRo} from "./dns-record-ro.model";

export interface DnsQueryRo {
  dnsQuery:string
  dnsType:DnsTypeEnum
  dnsEntries?:DnsRecordRo[]
  error?:string[]
}
