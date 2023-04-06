import 'hammerjs';
import {CredentialDialogComponent} from "./common/dialogs/credential-dialog/credential-dialog.component";
import {AccessTokenGenerationDialogComponent} from "./common/dialogs/access-token-generation-dialog/access-token-generation-dialog.component";
import {AccessTokenPanelComponent} from "./user-settings/user-access-tokens/access-token-panel/access-token-panel.component";
import {AlertComponent} from "./alert/alert.component";
import {AlertMessageComponent} from './common/alert-message/alert-message.component';
import {AlertMessageService} from './common/alert-message/alert-message.service';
import {AppComponent} from './app.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {AuthorizedAdminGuard} from './guards/authorized-admin.guard';
import {AuthorizedGuard} from './guards/authorized.guard';
import {AutoFocusDirective} from "./common/directive/autofocus/auto-focus.directive";
import {BreadcrumbComponent} from "./window/breadcrumb/breadcrumb.component";
import {BreadcrumbItemComponent} from "./window/breadcrumb/breadcrumb-item/breadcrumb-item.component";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {BrowserModule} from '@angular/platform-browser';
import {CancelDialogComponent} from './common/dialogs/cancel-dialog/cancel-dialog.component';
import {CapitalizeFirstPipe} from './common/capitalize-first.pipe';
import {CertificateDialogComponent} from "./common/dialogs/certificate-dialog/certificate-dialog.component";
import {CertificatePanelComponent} from "./user-settings/user-certificates/certificate-panel/certificate-panel.component";
import {CertificateService} from './system-settings/user/certificate.service';
import {ClearInvalidDirective} from './custom-date/clear-invalid.directive';
import {ColumnPickerComponent} from './common/column-picker/column-picker.component';
import {ConfirmationDialogComponent} from './common/dialogs/confirmation-dialog/confirmation-dialog.component';
import {DataPanelComponent} from "./common/data-panel/data-panel.component";
import {DatePipe} from './custom-date/date.pipe';
import {DefaultPasswordDialogComponent} from './security/default-password-dialog/default-password-dialog.component';
import {DialogComponent} from './common/dialogs/dialog/dialog.component';
import {DirtyGuard} from './common/dirty.guard';
import {DomainComponent} from './system-settings/domain/domain.component';
import {DomainDetailsDialogComponent} from './system-settings/domain/domain-details-dialog/domain-details-dialog.component';
import {DomainSelectorComponent} from './common/domain-selector/domain-selector.component';
import {DomainService} from './security/domain.service';
import {DownloadService} from './download/download.service';
import {ExpiredPasswordDialogComponent} from './common/dialogs/expired-password-dialog/expired-password-dialog.component';
import {ExtendedHttpClient, extendedHttpClientCreator} from './http/extended-http-client';
import {FlexLayoutModule} from '@angular/flex-layout';
import {FooterComponent} from './footer/footer.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {GlobalLookups} from './common/global-lookups';
import {HttpClient, HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {HttpEventService} from './http/http-event.service';
import {InformationDialogComponent} from "./common/dialogs/information-dialog/information-dialog.component";
import {IsAuthorized} from './security/is-authorized.directive';
import {KeystoreEditDialogComponent} from "./system-settings/domain/keystore-edit-dialog/keystore-edit-dialog.component";
import {KeystoreImportDialogComponent} from "./system-settings/domain/keystore-import-dialog/keystore-import-dialog.component";
import {KeystoreService} from "./system-settings/domain/keystore.service";
import {LoginComponent} from './login/login.component';
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatDialogModule} from "@angular/material/dialog";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from "@angular/material/list";
import {MatMenuModule} from "@angular/material/menu";
import {MatNativeDateModule} from "@angular/material/core";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatSelectModule} from "@angular/material/select";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatTableModule} from "@angular/material/table";
import {MatTabsModule} from "@angular/material/tabs";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatTreeModule} from "@angular/material/tree";
import {NavTreeMenu} from "./window/sidenav/nav-tree-menu/nav-tree-menu.component";
import {NavTree} from "./window/sidenav/nav-tree/nav-tree.component";
import {NavigationService} from "./window/sidenav/navigation-model.service";
import {NgModule} from '@angular/core';
import {NgxDatatableModule} from '@swimlane/ngx-datatable';
import {ObjectPropertiesDialogComponent} from "./common/dialogs/object-properties-dialog/object-properties-dialog.component";
import {PasswordChangeDialogComponent} from "./common/dialogs/password-change-dialog/password-change-dialog.component";
import {PropertyComponent} from "./system-settings/property/property.component";
import {PropertyDetailsDialogComponent} from "./system-settings/property/property-details-dialog/property-details-dialog.component";
import {RowLimiterComponent} from './common/row-limiter/row-limiter.component';
import {SaveDialogComponent} from './common/dialogs/save-dialog/save-dialog.component';
import {SearchTableComponent} from './common/search-table/search-table.component';
import {SecurityEventService} from './security/security-event.service';
import {SecurityService} from './security/security.service';
import {ServiceGroupDetailsDialogComponent} from './service-group-edit/service-group-details-dialog/service-group-details-dialog.component';
import {ServiceGroupEditComponent} from './service-group-edit/service-group-edit.component';
import {ServiceGroupExtensionWizardDialogComponent} from './service-group-edit/service-group-extension-wizard-dialog/service-group-extension-wizard-dialog.component';
import {ServiceGroupMetadataDialogComponent} from './service-group-edit/service-group-metadata-dialog/service-group-metadata-dialog.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {ServiceMetadataWizardDialogComponent} from './service-group-edit/service-metadata-wizard-dialog/service-metadata-wizard-dialog.component';
import {SharedModule} from './common/module/shared.module';
import {SidenavComponent} from './window/sidenav/sidenav.component';
import {SmlIntegrationService} from "./system-settings/domain/sml-integration.service";
import {SmpInfoService} from './app-info/smp-info.service';
import {SpacerComponent} from "./common/spacer/spacer.component";
import {SpinnerComponent} from './common/spinner/spinner.component';
import {ThemeService} from "./common/theme-service/theme.service";
import {ToolbarComponent} from "./window/toolbar/toolbar.component";
import {TruststoreEditDialogComponent} from "./system-settings/user/truststore-edit-dialog/truststore-edit-dialog.component";
import {TruststoreService} from "./system-settings/user/truststore.service";
import {UserAccessTokensComponent} from "./user-settings/user-access-tokens/user-access-tokens.component";
import {UserCertificatesComponent} from "./user-settings/user-certificates/user-certificates.component";
import {UserComponent} from './system-settings/user/user.component';
import {UserDetailsDialogComponent} from './system-settings/user/user-details-dialog/user-details-dialog.component';
import {UserDetailsService} from './system-settings/user/user-details-dialog/user-details.service';
import {UserProfileComponent} from "./user-settings/user-profile/user-profile.component";
import {UserService} from './system-settings/user/user.service';
import {routing} from './app.routes';
import {ExtensionComponent} from "./system-settings/extension/extension.component";
import {MatPaginatorModule} from "@angular/material/paginator";
import {ExtensionPanelComponent} from "./system-settings/extension/extension-panel/extension-panel.component";
import {
  ResourceDetailsDialogComponent
} from "./system-settings/extension/resource-details-dialog/resource-details-dialog.component";
import {ExtensionService} from "./system-settings/extension/extension.service";


@NgModule({
  declarations: [
    CredentialDialogComponent,
    AccessTokenGenerationDialogComponent,
    AccessTokenPanelComponent,
    AlertComponent,
    AlertMessageComponent,
    AppComponent,
    AutoFocusDirective,
    BreadcrumbComponent,
    BreadcrumbItemComponent,
    CancelDialogComponent,
    CapitalizeFirstPipe,
    CertificateDialogComponent,
    CertificatePanelComponent,
    ClearInvalidDirective,
    ColumnPickerComponent,
    ConfirmationDialogComponent,
    DataPanelComponent,
    DatePipe,
    DefaultPasswordDialogComponent,
    DialogComponent,
    DomainComponent,
    DomainDetailsDialogComponent,
    DomainSelectorComponent,
    ExpiredPasswordDialogComponent,
    ExtensionComponent,
    ExtensionPanelComponent,
    FooterComponent,
    InformationDialogComponent,
    IsAuthorized,
    KeystoreEditDialogComponent,
    KeystoreImportDialogComponent,
    LoginComponent,
    NavTree,
    NavTreeMenu,
    ObjectPropertiesDialogComponent,
    PasswordChangeDialogComponent,
    PropertyComponent,
    PropertyDetailsDialogComponent,
    ResourceDetailsDialogComponent,
    RowLimiterComponent,
    SaveDialogComponent,
    SearchTableComponent,
    ServiceGroupDetailsDialogComponent,
    ServiceGroupEditComponent,
    ServiceGroupExtensionWizardDialogComponent,
    ServiceGroupMetadataDialogComponent,
    ServiceGroupSearchComponent,
    ServiceMetadataWizardDialogComponent,
    SidenavComponent,
    SpacerComponent,
    SpinnerComponent,
    ToolbarComponent,
    TruststoreEditDialogComponent,
    UserAccessTokensComponent,
    UserCertificatesComponent,
    UserComponent,
    UserDetailsDialogComponent,
    UserProfileComponent,
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    FlexLayoutModule,
    FormsModule,
    HttpClientModule,
    HttpClientXsrfModule.withOptions({
      cookieName: 'XSRF-TOKEN',
      headerName: 'X-XSRF-TOKEN'
    }),
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogModule,
    MatExpansionModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSidenavModule,
    MatSlideToggleModule,
    MatTableModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatTreeModule,
    NgxDatatableModule,
    ReactiveFormsModule,
    SharedModule,
    routing,
  ],
  providers: [
    AlertMessageService,
    AuthenticatedGuard,
    AuthorizedAdminGuard,
    AuthorizedGuard,
    CertificateService,
    DatePipe,
    DirtyGuard,
    DomainService,
    DownloadService,
    ExtensionService,
    GlobalLookups,
    HttpEventService,
    KeystoreService,
    NavigationService,
    SecurityEventService,
    SecurityService,
    SmlIntegrationService,
    SmpInfoService,
    ThemeService,
    TruststoreService,
    UserDetailsService,
    UserService,
    {
      provide: ExtendedHttpClient,
      useFactory: extendedHttpClientCreator,
      deps: [HttpClient, HttpEventService, SecurityService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
