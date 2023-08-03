import 'hammerjs';
import {AccessTokenPanelComponent} from "./user-settings/user-access-tokens/access-token-panel/access-token-panel.component";
import {AdminDomainComponent} from "./system-settings/admin-domain/admin-domain.component";
import {AdminDomainService} from "./system-settings/admin-domain/admin-domain.service";
import {AdminKeystoreComponent} from "./system-settings/admin-keystore/admin-keystore.component";
import {AdminKeystoreService} from "./system-settings/admin-keystore/admin-keystore.service";
import {AdminTruststoreComponent} from "./system-settings/admin-truststore/admin-truststore.component";
import {AdminTruststoreService} from "./system-settings/admin-truststore/admin-truststore.service";
import {AlertComponent} from "./alert/alert.component";
import {AlertMessageComponent} from './common/alert-message/alert-message.component';
import {AlertMessageService} from './common/alert-message/alert-message.service';
import {AppComponent} from './app.component';
import {AuthorizedGuard} from './guards/authorized.guard';
import {AutoFocusDirective} from "./common/directive/autofocus/auto-focus.directive";
import {BreadcrumbComponent} from "./window/breadcrumb/breadcrumb.component";
import {BreadcrumbItemComponent} from "./window/breadcrumb/breadcrumb-item/breadcrumb-item.component";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {BrowserModule} from '@angular/platform-browser';
import {CancelDialogComponent} from './common/dialogs/cancel-dialog/cancel-dialog.component';
import {CapitalizeFirstPipe} from './common/capitalize-first.pipe';
import {CertificateDialogComponent} from "./common/dialogs/certificate-dialog/certificate-dialog.component";
import {CertificatePanelComponent} from "./common/panels/certificate-panel/certificate-panel.component";
import {CertificateService} from './system-settings/user/certificate.service';
import {ClearInvalidDirective} from './custom-date/clear-invalid.directive';
import {ColumnPickerComponent} from './common/column-picker/column-picker.component';
import {ConfirmationDialogComponent} from './common/dialogs/confirmation-dialog/confirmation-dialog.component';
import {CredentialDialogComponent} from "./common/dialogs/credential-dialog/credential-dialog.component";
import {DataPanelComponent} from "./common/panels/data-panel/data-panel.component";
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatNativeDateModule} from "@angular/material/core";
import {DatePipe} from './custom-date/date.pipe';
import {DefaultPasswordDialogComponent} from './security/default-password-dialog/default-password-dialog.component';
import {DialogComponent} from './common/dialogs/dialog/dialog.component';
import {DomainComponent} from './system-settings/domain/domain.component';
import {DomainDetailsDialogComponent} from './system-settings/domain/domain-details-dialog/domain-details-dialog.component';
import {DomainPanelComponent} from "./system-settings/admin-domain/domain-panel/domain-panel.component";
import {DomainResourceTypePanelComponent} from "./system-settings/admin-domain/domain-resource-type-panel/domain-resource-type-panel.component";
import {DomainSelectorComponent} from './common/domain-selector/domain-selector.component';
import {DomainService} from './security/domain.service';
import {DomainSmlIntegrationPanelComponent} from "./system-settings/admin-domain/domain-sml-panel/domain-sml-integration-panel.component";
import {DownloadService} from './download/download.service';
import {ExpiredPasswordDialogComponent} from './common/dialogs/expired-password-dialog/expired-password-dialog.component';
import {ExtendedHttpClient, extendedHttpClientCreator} from './http/extended-http-client';
import {ExtensionComponent} from "./system-settings/admin-extension/extension.component";
import {ExtensionPanelComponent} from "./system-settings/admin-extension/extension-panel/extension-panel.component";
import {ExtensionService} from "./system-settings/admin-extension/extension.service";
import {FlexLayoutModule} from '@angular/flex-layout';
import {FooterComponent} from './window/footer/footer.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {GlobalLookups} from './common/global-lookups';
import {HttpClient, HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {HttpEventService} from './http/http-event.service';
import {InformationDialogComponent} from "./common/dialogs/information-dialog/information-dialog.component";
import {IsAuthorized} from './security/is-authorized.directive';
import {KeystoreEditDialogComponent} from "./system-settings/domain/keystore-edit-dialog/keystore-edit-dialog.component";
import {KeystoreImportDialogComponent} from "./system-settings/admin-keystore/keystore-import-dialog/keystore-import-dialog.component";
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
import {MatPaginatorModule} from "@angular/material/paginator";
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
import {ResourceDetailsDialogComponent} from "./system-settings/admin-extension/resource-details-dialog/resource-details-dialog.component";
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
import {SidenavComponent} from './window/sidenav/sidenav.component';
import {SmlIntegrationService} from "./system-settings/domain/sml-integration.service";
import {SmpInfoService} from './app-info/smp-info.service';
import {SpacerComponent} from "./common/components/spacer/spacer.component";
import {SpinnerComponent} from './common/components/spinner/spinner.component';
import {ThemeService} from "./common/theme-service/theme.service";
import {ToolbarComponent} from "./window/toolbar/toolbar.component";
import {UserAccessTokensComponent} from "./user-settings/user-access-tokens/user-access-tokens.component";
import {UserCertificatePanelComponent} from "./user-settings/user-certificates/user-certificate-panel/user-certificate-panel.component";
import {UserCertificatesComponent} from "./user-settings/user-certificates/user-certificates.component";
import {UserDetailsService} from './system-settings/user/user-details.service';
import {UserProfileComponent} from "./user-settings/user-profile/user-profile.component";
import {UserService} from './system-settings/user/user.service';
import {routing} from './app.routes';
import {MAT_MOMENT_DATE_FORMATS, MatMomentDateModule, MomentDateAdapter} from "@angular/material-moment-adapter";
import {NGX_MAT_DATE_FORMATS,NgxMatDateAdapter,NgxMatDatetimePickerModule} from "@angular-material-components/datetime-picker";
import {NGX_MAT_MOMENT_DATE_ADAPTER_OPTIONS,NGX_MAT_MOMENT_FORMATS, NgxMatMomentAdapter,NgxMatMomentModule} from "@angular-material-components/moment-adapter";
import {MembershipPanelComponent} from "./common/panels/membership-panel/membership-panel.component";
import {MemberDialogComponent} from "./common/dialogs/member-dialog/member-dialog.component";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MembershipService} from "./common/panels/membership-panel/membership.service";
import {AdminUserComponent} from "./system-settings/admin-users/admin-user.component";
import {AdminUserService} from "./system-settings/admin-users/admin-user.service";
import {UserProfilePanelComponent} from "./common/panels/user-settings-panel/user-profile-panel.component";
import {EditDomainComponent} from "./edit/edit-domain/edit-domain.component";
import {EditDomainService} from "./edit/edit-domain/edit-domain.service";
import {SmpFieldErrorComponent} from "./common/components/smp-field-error/smp-field-error.component";
import {DomainGroupComponent} from "./edit/edit-domain/domain-group-panel/domain-group.component";
import {GroupDialogComponent} from "./edit/edit-domain/domain-group-panel/group-dialog/group-dialog.component";
import {EditGroupComponent} from "./edit/edit-group/edit-group.component";
import {EditGroupService} from "./edit/edit-group/edit-group.service";
import {SmpLabelComponent} from "./common/components/smp-label/smp-label.component";
import {GroupResourcePanelComponent} from "./edit/edit-group/group-resource-panel/group-resource-panel.component";
import {ResourceDialogComponent} from "./edit/edit-group/group-resource-panel/resource-dialog/resource-dialog.component";
import {EditResourceComponent} from "./edit/edit-resources/edit-resource.component";
import {EditResourceService} from "./edit/edit-resources/edit-resource.service";
import {ResourceDetailsPanelComponent} from "./edit/edit-resources/resource-details-panel/resource-details-panel.component";
import {ResourceDocumentPanelComponent} from "./edit/edit-resources/resource-document-panel/resource-document-panel.component";
import { CodemirrorModule } from '@ctrl/ngx-codemirror';
import {DocumentWizardDialogComponent} from "./edit/edit-resources/document-wizard-dialog/document-wizard-dialog.component";
import {SubresourcePanelComponent} from "./edit/edit-resources/subresource-panel/subresource-panel.component";
import {SubresourceDialogComponent} from "./edit/edit-resources/subresource-panel/resource-dialog/subresource-dialog.component";
import {SubresourceDocumentPanelComponent} from "./edit/edit-resources/subresource-document-panel/subresource-document-panel.component";
import {SubresourceDocumentWizardComponent} from "./edit/edit-resources/subresource-document-wizard-dialog/subresource-document-wizard.component";
import {SmpWarningPanelComponent} from "./common/components/smp-warning-panel/smp-warning-panel.component";
import {ManageMembersDialogComponent} from "./common/dialogs/manage-members-dialog/manage-members-dialog.component";
import {HttpErrorHandlerService} from "./common/error/http-error-handler.service";
import {SmpTitledLabelComponent} from "./common/components/smp-titled-label/smp-titled-label.component";
import {NgOptimizedImage} from "@angular/common";


@NgModule({
  declarations: [
    AccessTokenPanelComponent,
    AdminDomainComponent,
    AdminKeystoreComponent,
    AdminTruststoreComponent,
    AdminUserComponent,
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
    CredentialDialogComponent,
    DataPanelComponent,
    DatePipe,
    DefaultPasswordDialogComponent,
    DialogComponent,
    DocumentWizardDialogComponent,
    DomainComponent,
    DomainDetailsDialogComponent,
    DomainGroupComponent,
    DomainPanelComponent,
    DomainResourceTypePanelComponent,
    DomainSelectorComponent,
    DomainSmlIntegrationPanelComponent,
    EditDomainComponent,
    EditGroupComponent,
    EditResourceComponent,
    ExpiredPasswordDialogComponent,
    ExtensionComponent,
    ExtensionPanelComponent,
    FooterComponent,
    GroupDialogComponent,
    GroupResourcePanelComponent,
    InformationDialogComponent,
    IsAuthorized,
    KeystoreEditDialogComponent,
    KeystoreImportDialogComponent,
    LoginComponent,
    ManageMembersDialogComponent,
    MemberDialogComponent,
    MembershipPanelComponent,
    NavTree,
    NavTreeMenu,
    ObjectPropertiesDialogComponent,
    PasswordChangeDialogComponent,
    PropertyComponent,
    PropertyDetailsDialogComponent,
    ResourceDetailsDialogComponent,
    ResourceDetailsPanelComponent,
    ResourceDialogComponent,
    ResourceDocumentPanelComponent,
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
    SmpFieldErrorComponent,
    SmpLabelComponent,
    SmpTitledLabelComponent,
    SmpWarningPanelComponent,
    SpacerComponent,
    SpinnerComponent,
    SubresourceDialogComponent,
    SubresourceDocumentPanelComponent,
    SubresourceDocumentWizardComponent,
    SubresourcePanelComponent,
    ToolbarComponent,
    UserAccessTokensComponent,
    UserCertificatePanelComponent,
    UserCertificatesComponent,
    UserProfileComponent,
    UserProfilePanelComponent,
  ],
              imports: [
                  BrowserAnimationsModule,
                  BrowserModule,
                  FlexLayoutModule,
                  FormsModule,
                  HttpClientModule,
                  HttpClientXsrfModule.withOptions( {
                                                        cookieName: 'XSRF-TOKEN',
                                                        headerName: 'X-XSRF-TOKEN'
                                                    } ),
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
                  MatMomentDateModule,
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
                  NgxMatDatetimePickerModule,
                  NgxMatMomentModule,
                  ReactiveFormsModule,
                  routing,
                  MatAutocompleteModule,
                  CodemirrorModule,
                  NgOptimizedImage,
              ],
  providers: [
    AdminDomainService,
    AdminKeystoreService,
    AdminTruststoreService,
    AdminUserService,
    AlertMessageService,
    AuthorizedGuard,
    CertificateService,
    DatePipe,
    DomainService,
    MembershipService,
    DownloadService,
    EditDomainService,
    EditGroupService,
    EditResourceService,
    HttpErrorHandlerService,
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
    UserDetailsService,
    UserService,
    {
      provide: ExtendedHttpClient,
      useFactory: extendedHttpClientCreator,
      deps: [HttpClient, HttpEventService, SecurityService]
    },
    //use DateAdapter for date formatting and NgxMatDateAdapter for date time picker
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS},
    {provide: NgxMatDateAdapter, useClass: NgxMatMomentAdapter, deps: [MAT_DATE_LOCALE, NGX_MAT_MOMENT_DATE_ADAPTER_OPTIONS]},
    {provide: NGX_MAT_DATE_FORMATS, useValue: NGX_MAT_MOMENT_FORMATS},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
