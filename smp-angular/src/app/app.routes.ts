import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {ServiceGroupEditComponent} from './service-group-edit/service-group-edit.component';
import {DomainComponent} from './domain/domain.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {UserComponent} from './user/user.component';
import {DirtyGuard} from "./common/dirty.guard";
import {AuthorizedAdminGuard} from "./guards/authorized-admin.guard";


const appRoutes: Routes = [
  {path: '', component: ServiceGroupSearchComponent},
  {path: 'search', component: ServiceGroupSearchComponent},
  {path: 'edit', component: ServiceGroupEditComponent, canActivate: [AuthenticatedGuard, AuthorizedAdminGuard],
  canDeactivate: [DirtyGuard]},
  {path: 'domain', component: DomainComponent, canActivate: [AuthenticatedGuard, AuthorizedAdminGuard],
  canDeactivate: [DirtyGuard]},
  {path: 'user', component: UserComponent, canActivate: [AuthenticatedGuard, AuthorizedAdminGuard],
    canDeactivate: [DirtyGuard]},

  {path: 'login', component: LoginComponent},
  {path: '**', component: ServiceGroupEditComponent, canActivate: [AuthenticatedGuard]}

];

export const routing = RouterModule.forRoot(appRoutes);
