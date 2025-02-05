

export interface TableResult<T> {
  serviceEntities: T[];
  pageSize: number;
  page?: number;
  count: number;
  filter: any;
}
