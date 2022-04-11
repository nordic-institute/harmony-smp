import {ɵstringify} from "@angular/core";

export interface SearchTableValidationResult{
  validOperation: boolean;
  stringMessage?: string;

  listId?: Array<string>;
  listDeleteNotPermitedId?: Array<number>;
}
