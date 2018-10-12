import {Role} from './role.model';

export interface User {
  id: number;
  username: string;
  authorities: Array<Role>;
  defaultPasswordUsed: boolean;
}
