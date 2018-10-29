
export interface ServiceGroupValidationRo  {
  serviceGroupId: number;
  participantScheme: string;
  participantIdentifier: string;
  extension: string;
  errorMessage?: string;
  statusAction:number;
  errorCode?: number;
}
