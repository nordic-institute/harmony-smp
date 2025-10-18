import {EntityStatus} from "../enums/entity-status.enum";
import {CertificateExtensionRo} from "./certificate-extension-ro.model";

export interface CertificateRo {
  certificateId: string;
  subject: string;
  validFrom: Date;
  validTo: Date;
  issuer: string;
  serialNumber: string;
  fingerprints: string;
  clientCertHeader?: string;
  encodedValue?: string;
  crlUrl?: string;
  alias?: string;
  publicKeyType?: string;
  certificatePolicies?: string[];
  containingKey?: boolean;
  invalid?: boolean;
  error?: boolean;
  invalidReason?: string;
  extensions?: CertificateExtensionRo[];

  status?: EntityStatus;
  actionMessage?: string;

}
