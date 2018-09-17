/**
 * @author Thomas Dussart
 */
export interface TrustStoreEntry {
  name: string;
  subject: string;
  issuer: string;
  validFrom: string;
  validUntil: string;
}

