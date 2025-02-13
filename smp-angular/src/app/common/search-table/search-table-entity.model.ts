import {EntityStatus} from '../enums/entity-status.enum';
import {VisibilityEnum} from "../enums/visibility.enum";

export interface SearchTableEntity {
  id?: number;
  index?: number;
  status?: EntityStatus;
  deleted?: boolean;
  visibility?: VisibilityEnum;
  actionMessage?: string;
}
