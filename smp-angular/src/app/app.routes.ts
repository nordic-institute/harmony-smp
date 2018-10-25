import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {ServiceGroupEditComponent} from './service-group-edit/service-group-edit.component';
import {DomainComponent} from './domain/domain.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {UserComponent} from './user/user.component';
import {DirtyGuard} from "./common/dirty.guard";


const appRoutes: Routes = [
  {path: '', component: ServiceGroupSearchComponent},
  {path: 'search', redirectTo: ''},
  {path: 'edit', component: ServiceGroupEditComponent,  canActivate: [AuthenticatedGuard],  canDeactivate: [DirtyGuard]},
  {path: 'domain', component: DomainComponent, canActivate: [AuthenticatedGuard], canDeactivate: [DirtyGuard]},
  {path: 'user', component: UserComponent, canDeactivate: [DirtyGuard]},
  {path: 'login', component: LoginComponent},
  {path: '**', redirectTo: ''}
];

export const routing = RouterModule.forRoot(appRoutes, {useHash: true});
