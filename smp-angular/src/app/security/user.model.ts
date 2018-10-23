import {Authority} from "./authority.model";

export interface User {
  id: number;
  username: string;
  authorities: Array<Authority>;
  defaultPasswordUsed: boolean;
}
