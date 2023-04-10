import {DomainRo} from '../admin-domain/domain-ro.model';

export interface DomainResult {
  serviceEntities: Array<DomainRo>;
  pageSize: number;
  count: number;
  filter: any;
}
