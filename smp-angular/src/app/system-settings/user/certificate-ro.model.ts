import {EntityStatus} from "../../common/enums/entity-status.enum";

export interface CertificateRo {
  certificateId: string;
  subject: string;
  validFrom: Date;
  validTo: Date;
  issuer: string;
  serialNumber: string;
  fingerprints: string;
  clientCertHeader?:string;
  encodedValue?:string;
  crlUrl?: string;
  alias?:string;
  publicKeyType?: string;
  certificatePolicies?: string[];
  isContainingKey?:boolean;
  invalid?:boolean;
  invalidReason?:string;

  status?: EntityStatus;
  actionMessage?: string;
}
