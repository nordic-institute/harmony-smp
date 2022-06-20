import {Authority} from "./authority.model";

export interface User {
  userId: string;
  emailAddress: string;
  username: string;
  accessTokenId?: string;
  accessTokenExpireOn?: Date;
  authorities: Array<Authority>;
  casAuthenticated?: boolean;
  defaultPasswordUsed: boolean;
  forceChangeExpiredPassword?: boolean;
  showPasswordExpirationWarning?: boolean;
  passwordExpireOn?: Date;
  casUserDataUrl?: string;
}
