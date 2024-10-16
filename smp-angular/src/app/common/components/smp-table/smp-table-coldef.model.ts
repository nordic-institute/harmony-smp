/**
 * Interface for the column definition of the SMP table columns
 * @since 5.1
 */
export interface SmpTableColDef {
  columnDef: string;
  header: string;
  cell?: (row: any) => any;
  tooltip?: (row: any) => any;
  style?:string  ;
}
