export interface CertificateRo {
  certificateId: string;
  subject: string;
  validFrom: Date;
  validUntil: Date;
  issuer: string;
  serialNumber: string;
  fingerprints: string;
}
