import {EntityStatus} from "../../common/enums/entity-status.enum";
import {SubresourceDefinitionRo} from "./subresource-definition-ro.model";

export interface ResourceDefinitionRo {

  identifier?: string;
  name: string;
  description?: string;
  urlSegment?: string;
  mimeType?: string;

  subresourceDefinitions?: SubresourceDefinitionRo[];
  status?: EntityStatus;

}
