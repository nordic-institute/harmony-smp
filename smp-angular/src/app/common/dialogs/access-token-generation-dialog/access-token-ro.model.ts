import {Credential} from "../../../security/credential.model";

export interface AccessTokenRo {
  identifier: string;

  value: string;
  generatedOn?: Date;
  expireOn?: Date;

  credential?:  Credential;
}
