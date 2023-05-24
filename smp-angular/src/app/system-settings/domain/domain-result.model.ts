import {DomainRo} from '../../common/model/domain-ro.model';

export interface DomainResult {
  serviceEntities: Array<DomainRo>;
  pageSize: number;
  count: number;
  filter: any;
}
