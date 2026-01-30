import {TemplateRef} from "@angular/core";

/**
 * Interface for the column definition of the SMP table columns
 * @since 5.1
 */
export interface SmpTableColDef {
  columnDef: string;
  header: string;
  headerTooltip?: string;
  cell?: (row: any) => any;
  cellTemplate?:  TemplateRef<any>;
  icon?: (row: any) => string;
  tooltip?: (row: any) => any;
  class?: (row: any) => any;
  style?:string;
  headerStyle?: string;
}
