export interface SmpInfo {
  version: string;
  smlIntegrationOn?: boolean;
  contextPath?: string;
  smlParticipantMultiDomainOn?: boolean
  authTypes?: string[];
  ssoAuthenticationLabel?: string;
}
