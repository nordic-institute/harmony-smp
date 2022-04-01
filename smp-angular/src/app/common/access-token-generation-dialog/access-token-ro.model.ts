export interface AccessTokenRo {
  identifier: string;
  value: string;
  generatedOn?: Date;
  expireOn?: Date;
}
