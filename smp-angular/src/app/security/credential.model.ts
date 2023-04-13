import {EntityStatus} from "../common/enums/entity-status.enum";
import {CertificateRo} from "../system-settings/user/certificate-ro.model";

export interface CredentialRo {

  credentialId?: string;
  name: string;
  active: boolean;
  description?: string;
  updatedOn?: Date;
  expireOn?: Date;
  activeFrom?: Date;
  sequentialLoginFailureCount?: number;
  lastFailedLoginAttempt?: Date;
  suspendedUtil?: Date;
  certificate?: CertificateRo;
  status?: EntityStatus;

}
