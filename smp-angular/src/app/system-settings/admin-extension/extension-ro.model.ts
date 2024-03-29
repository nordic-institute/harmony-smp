import {EntityStatus} from "../../common/enums/entity-status.enum";
import {ResourceDefinitionRo} from "./resource-definition-ro.model";

export interface ExtensionRo {

  extensionId?: string;
  name: string;
  version: string;
  description?: string;
  implementationName?: string;
  resourceDefinitions: ResourceDefinitionRo[];

  errorMessage?: string;

  status?: EntityStatus;

}
