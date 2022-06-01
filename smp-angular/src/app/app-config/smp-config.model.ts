export interface SmpConfig {
  smlIntegrationOn?: boolean;
  smlParticipantMultiDomainOn?: boolean;
  concatEBCorePartyId?: boolean;
  partyIDSchemeMandatory?: boolean;
  participantSchemaRegExp?: string;
  participantSchemaRegExpMessage?: string;
  passwordValidationRegExp?: string;
  passwordValidationRegExpMessage?: string;
  webServiceAuthTypes?: string[];
}
