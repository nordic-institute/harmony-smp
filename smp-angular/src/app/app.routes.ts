import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {ServiceGroupEditComponent} from './service-group-edit/service-group-edit.component';
import {DomainComponent} from './domain/domain.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {UserComponent} from './user/user.component';
import {DirtyGuard} from "./common/dirty.guard";
import {AuthorizedAdminGuard} from "./guards/authorized-admin.guard";
import {AlertComponent} from "./alert/alert.component";
import {PropertyComponent} from "./property/property.component";


const appRoutes: Routes = [
  {path: '', component: ServiceGroupSearchComponent},
  {path: 'search', redirectTo: ''},
  {path: 'edit', component: ServiceGroupEditComponent, canActivate: [AuthenticatedGuard], canDeactivate: [DirtyGuard]},
  {
    path: 'domain',
    component: DomainComponent,
    canActivate: [AuthenticatedGuard, AuthorizedAdminGuard],
    canDeactivate: [DirtyGuard]
  },
  {
    path: 'user',
    component: UserComponent,
    canActivate: [AuthenticatedGuard, AuthorizedAdminGuard],
    canDeactivate: [DirtyGuard]
  },
  {
    path: 'alert',
    component: AlertComponent,
    canActivate: [AuthenticatedGuard, AuthorizedAdminGuard],
    canDeactivate: [DirtyGuard]
  },
  {
    path: 'property',
    component: PropertyComponent,
    canActivate: [AuthenticatedGuard, AuthorizedAdminGuard],
    canDeactivate: [DirtyGuard]
  },
  {path: 'login', component: LoginComponent},
  {path: '**', redirectTo: ''}
];

export const routing = RouterModule.forRoot(appRoutes, {useHash: true});
