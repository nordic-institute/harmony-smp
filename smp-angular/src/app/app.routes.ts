import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {ServiceGroupComponent} from './service-group-edit/service-group.component';
import {DomainComponent} from './domain/domain.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {UserComponent} from './user/user.component';


const appRoutes: Routes = [
  {path: '', component: ServiceGroupSearchComponent},
  {path: 'search', component: ServiceGroupSearchComponent},
  {path: 'edit', component: ServiceGroupComponent},
  {path: 'domain', component: DomainComponent},
  {path: 'user', component: UserComponent},


  {path: 'login', component: LoginComponent},
  {path: '**', component: ServiceGroupComponent, canActivate: [AuthenticatedGuard]}

];

export const routing = RouterModule.forRoot(appRoutes);
