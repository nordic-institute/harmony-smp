import {EntityStatus} from "../../common/model/entity-status.model";

export interface SubresourceDefinitionRo {

  identifier?: string;
  name: string;
  description?: string;
  urlSegment?: string;
  mimeType?: string;

  status?: EntityStatus;

}
