import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {AlertComponent} from "./alert/alert.component";
import {PropertyComponent} from "./system-settings/property/property.component";
import {UserProfileComponent} from "./user-settings/user-profile/user-profile.component";
import {authenticationGuard} from "./guards/authentication.guard";
import {UserAccessTokensComponent} from "./user-settings/user-access-tokens/user-access-tokens.component";
import {UserCertificatesComponent} from "./user-settings/user-certificates/user-certificates.component";
import {ExtensionComponent} from "./system-settings/admin-extension/extension.component";
import {AdminTruststoreComponent} from "./system-settings/admin-truststore/admin-truststore.component";
import {AdminKeystoreComponent} from "./system-settings/admin-keystore/admin-keystore.component";
import {AdminDomainComponent} from "./system-settings/admin-domain/admin-domain.component";
import {dirtyDeactivateGuard} from "./guards/dirty.guard";
import {AdminUserComponent} from "./system-settings/admin-users/admin-user.component";
import {EditDomainComponent} from "./edit/edit-domain/edit-domain.component";
import {EditGroupComponent} from "./edit/edit-group/edit-group.component";
import {EditResourceComponent} from "./edit/edit-resources/edit-resource.component";
import {
  ResourceDocumentPanelComponent
} from "./edit/edit-resources/resource-document-panel/resource-document-panel.component";


const appRoutes: Routes = [

  {path: '', component: ServiceGroupSearchComponent},
  {path: 'search', redirectTo: ''},
  {path: 'login', component: LoginComponent},
  {
    path: 'edit',
    canActivateChild: [authenticationGuard],
    children: [
      {path: 'edit-domain', component: EditDomainComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'edit-group', component: EditGroupComponent, canDeactivate: [dirtyDeactivateGuard]},
      {
        path: 'edit-resource',

        canDeactivate: [dirtyDeactivateGuard],
        children: [
          {path: 'resource-document', component: ResourceDocumentPanelComponent, canDeactivate: [dirtyDeactivateGuard]},
          {path: '', component: EditResourceComponent, canDeactivate: [dirtyDeactivateGuard]},
        ]
      }
    ]
  },
  {
    path: 'system-settings',
    canActivateChild: [authenticationGuard],
    children: [
      {path: 'domain', component: AdminDomainComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'user', component: AdminUserComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'properties', component: PropertyComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'keystore', component: AdminKeystoreComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'truststore', component: AdminTruststoreComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'extension', component: ExtensionComponent, canDeactivate: [dirtyDeactivateGuard]},
      {path: 'alert', component: AlertComponent, canDeactivate: [dirtyDeactivateGuard]},
    ]
  },
  {
    path: 'user-settings',
    canActivateChild: [authenticationGuard],
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
