
export interface ServiceMetadataWizardRo  {
  isNewServiceMetadata: boolean;
  participantScheme: string;
  participantIdentifier: string;
  documentIdentifierScheme: string;
  documentIdentifier: string;
  processScheme: string;
  processIdentifier: string;
  transportProfile: string;
  endpointUrl: string;
  endpointCertificate: string;
  serviceDescription: string;
  technicalContactUrl: string;
  contentXML?: string
  errorMessage?: string
}
