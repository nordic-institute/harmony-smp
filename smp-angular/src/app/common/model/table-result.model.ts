

export interface TableResult<T> {
  serviceEntities: T[];
  pageSize: number;
  count: number;
  filter: any;
}
