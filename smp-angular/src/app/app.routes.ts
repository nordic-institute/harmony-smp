import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {ServiceGroupEditComponent} from './service-group-edit/service-group-edit.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {UserComponent} from './system-settings/user/user.component';
import {AlertComponent} from "./alert/alert.component";
import {PropertyComponent} from "./system-settings/property/property.component";
import {UserProfileComponent} from "./user-settings/user-profile/user-profile.component";
import {authGuard} from "./guards/auth.guard";
import {UserAccessTokensComponent} from "./user-settings/user-access-tokens/user-access-tokens.component";
import {UserCertificatesComponent} from "./user-settings/user-certificates/user-certificates.component";
import {ExtensionComponent} from "./system-settings/admin-extension/extension.component";
import {AdminTruststoreComponent} from "./system-settings/admin-truststore/admin-truststore.component";
import {AdminKeystoreComponent} from "./system-settings/admin-keystore/admin-keystore.component";
import {AdminDomainComponent} from "./system-settings/admin-domain/admin-domain.component";
import {dirtyDeactivateGuard} from "./guards/dirty.guard";


const appRoutes: Routes = [

  {path: '', component: ServiceGroupSearchComponent},
  {path: 'search', redirectTo: ''},
  {
    path: 'edit',
    component: ServiceGroupEditComponent,
    canActivate: [AuthenticatedGuard],
    canDeactivate: [dirtyDeactivateGuard]
  },
  {path: 'login', component: LoginComponent},
  {
    path: 'system-settings',
    canActivateChild: [authGuard],
    children: [
      {path: 'domain', component: AdminDomainComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'user', component: UserComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'properties', component: PropertyComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'keystore', component: AdminKeystoreComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'truststore', component: AdminTruststoreComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'extension', component: ExtensionComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'alert', component: AlertComponent, canDeactivate: [dirtyDeactivateGuard]},
    ]
  },
  {
    path: 'user-settings',
    canActivateChild: [authGuard],
    children: [
      {path: 'user-profile', component: UserProfileComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'user-access-token', component: UserAccessTokensComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'user-certificate', component: UserCertificatesComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'user-membership', component: UserProfileComponent, canDeactivate: [dirtyDeactivateGuard]},
    ]
  },
  {path: '**', redirectTo: ''},
];

export const routing = RouterModule.forRoot(appRoutes, {useHash: true});
