import {Authority} from "./authority.model";

export interface User {
  id: number;
  username: string;
  accessTokenId?: string;
  authorities: Array<Authority>;
  defaultPasswordUsed: boolean;
}
