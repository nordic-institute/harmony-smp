import {CertificateRo} from "../user/certificate-ro.model";

export interface KeystoreResult {

  errorMessage?: string;

  addedCertificates?: CertificateRo[];
}
