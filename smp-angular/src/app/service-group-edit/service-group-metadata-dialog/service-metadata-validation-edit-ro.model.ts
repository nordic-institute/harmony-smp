
export interface ServiceMetadataValidationEditRo {
  participantScheme: string;
  participantIdentifier: string;
  documentIdentifierScheme: string;
  documentIdentifier: string;

  errorMessage?: string;
  xmlContent?: string;
}
