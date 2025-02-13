import {CertificateRo} from "./certificate-ro.model";

export interface KeystoreResult {

  errorMessage?: string;

  addedCertificates?: CertificateRo[];

  ignoredAliases?: String[];
}
