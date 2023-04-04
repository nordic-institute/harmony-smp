import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {ServiceGroupEditComponent} from './service-group-edit/service-group-edit.component';
import {DomainComponent} from './system-settings/domain/domain.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {UserComponent} from './system-settings/user/user.component';
import {DirtyGuard} from "./common/dirty.guard";
import {AuthorizedAdminGuard} from "./guards/authorized-admin.guard";
import {AlertComponent} from "./alert/alert.component";
import {PropertyComponent} from "./system-settings/property/property.component";
import {UserProfileComponent} from "./user-settings/user-profile/user-profile.component";
import {authGuard} from "./guards/auth.guard";
import {UserAccessTokensComponent} from "./user-settings/user-access-tokens/user-access-tokens.component";
import {UserCertificatesComponent} from "./user-settings/user-certificates/user-certificates.component";
import {ExtensionComponent} from "./system-settings/extension/extension.component";


const appRoutes: Routes = [

  {path: '', component: ServiceGroupSearchComponent},
  {path: 'search', redirectTo: ''},
  {path: 'edit', component: ServiceGroupEditComponent, canActivate: [AuthenticatedGuard], canDeactivate: [DirtyGuard]},
  {path: 'login', component: LoginComponent},
  {
    path: 'system-settings',
    canActivateChild: [authGuard],
    canDeactivate: [DirtyGuard],
    children: [
      { path: 'domain', component: DomainComponent  },
      { path: 'user', component: UserComponent  },
      { path: 'properties', component: PropertyComponent  },
      { path: 'keystore', component: DomainComponent  },
      { path: 'truststore', component: DomainComponent  },
      { path: 'extension', component: ExtensionComponent  },
      { path: 'alert', component: AlertComponent  },
    ]
  },
{
    path: 'user-settings',
    canActivateChild: [authGuard],
    children: [
      { path: 'user-profile', component: UserProfileComponent  },
      { path: 'user-access-token', component: UserAccessTokensComponent  },
      { path: 'user-certificate', component: UserCertificatesComponent },
      { path: 'user-membership', component: UserProfileComponent },
    ]
  },
  {path: '**', redirectTo: ''},
];

export const routing = RouterModule.forRoot(appRoutes, {useHash: true});
