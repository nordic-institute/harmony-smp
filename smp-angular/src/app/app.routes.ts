import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ServiceGroupComponent} from './servicegroup/service-group.component';
import {DomainComponent} from './domain/domain.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {AuthorizedAdminGuard} from './guards/authorized-admin.guard';
import {UserComponent} from './user/user.component';
import {TrustStoreComponent} from 'app/truststore/trust-store.component';


const appRoutes: Routes = [
  {path: '', component: ServiceGroupComponent},
  {path: 'servicegroup', component: ServiceGroupComponent},
  {path: 'domain', component: DomainComponent},
  {path: 'user', component: UserComponent},

  {path: 'truststore', component: TrustStoreComponent, canActivate: [AuthenticatedGuard, AuthorizedAdminGuard]},

  {path: 'login', component: LoginComponent},
  {path: '**', component: ServiceGroupComponent, canActivate: [AuthenticatedGuard]}

];

export const routing = RouterModule.forRoot(appRoutes);
