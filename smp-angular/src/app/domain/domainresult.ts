import {DomainRO} from "./domainro";

export class DomainResult {

  constructor(public serviceEntities: Array<DomainRO>,
              public pageSize: number,
              public count: number,
              public filter: any
              ) {

  }
}
