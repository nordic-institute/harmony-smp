export interface CertificateRo {
  subject: string;
  validFrom: Date;
  validUntil: Date;
  issuer: string;
  fingerprints: string;
}
