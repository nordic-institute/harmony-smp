import {DomainRO} from "./domainro";

export interface DomainResult {
  serviceEntities: Array<DomainRO>;
  pageSize: number;
  count: number;
  filter: any;
}
