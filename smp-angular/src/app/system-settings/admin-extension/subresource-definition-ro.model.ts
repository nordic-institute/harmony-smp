import {EntityStatus} from "../../common/enums/entity-status.enum";

export interface SubresourceDefinitionRo {

  identifier?: string;
  name: string;
  description?: string;
  urlSegment?: string;
  mimeType?: string;

  status?: EntityStatus;

}
