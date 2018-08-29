
export class SearchTableResult {

  constructor(public serviceEntities: Array<any>,
              public pageSize: number,
              public count: number,
              public filter: any
              ) {

  }
}
