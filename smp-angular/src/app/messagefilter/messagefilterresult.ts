import {BackendFilterEntry} from "./backendfilterentry";

export interface MessageFilterResult {
  messageFilterEntries: Array<BackendFilterEntry>;
  areFiltersPersisted: boolean;
}
