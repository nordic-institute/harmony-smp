import {Authority} from "./authority.model";

export interface User {
  userId: string;
  emailAddress: string;
  username: string;
  accessTokenId?: string;
  accessTokenExpireOn?: Date;
  sequentialTokenLoginFailureCount?:number;
  lastTokenFailedLoginAttempt?:Date;
  tokenSuspendedUtil?:Date;
  authorities: Array<Authority>;
  casAuthenticated?: boolean;
  defaultPasswordUsed: boolean;
  forceChangeExpiredPassword?: boolean;
  showPasswordExpirationWarning?: boolean;
  passwordExpireOn?: Date;
  sequentialLoginFailureCount?:number;
  lastFailedLoginAttempt?:Date;
  suspendedUtil?:Date;

  casUserDataUrl?: string;
}
