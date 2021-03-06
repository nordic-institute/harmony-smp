export interface CertificateRo {
  certificateId: string;
  subject: string;
  validFrom: Date;
  validTo: Date;
  issuer: string;
  serialNumber: string;
  fingerprints: string;
  blueCoatHeader?:string;
  encodedValue?:string;
  crlUrl?: String;
  alias?:string;
  invalid?:boolean;
  invalidReason?:string;
}
