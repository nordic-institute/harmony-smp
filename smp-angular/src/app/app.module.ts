import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {FlexLayoutModule} from '@angular/flex-layout';
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatDialogModule} from "@angular/material/dialog";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from "@angular/material/list";
import {MatMenuModule} from "@angular/material/menu";
import {MatSelectModule} from "@angular/material/select";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatTabsModule} from "@angular/material/tabs";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatTooltipModule} from "@angular/material/tooltip";
import 'hammerjs';
import {NgxDatatableModule} from '@swimlane/ngx-datatable';
import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {HomeComponent} from './home/home.component';
import {AuthenticatedGuard} from './guards/authenticated.guard';
import {AuthorizedGuard} from './guards/authorized.guard';
import {routing} from './app.routes';
import {IsAuthorized} from './security/is-authorized.directive';
import {ExtendedHttpClient, extendedHttpClientCreator} from './http/extended-http-client';
import {HttpEventService} from './http/http-event.service';
import {SecurityService} from './security/security.service';
import {SecurityEventService} from './security/security-event.service';
import {DomainService} from './security/domain.service';
import {AlertMessageComponent} from './common/alert-message/alert-message.component';
import {AlertMessageService} from './common/alert-message/alert-message.service';

import {FooterComponent} from './footer/footer.component';
import {SmpInfoService} from './app-info/smp-info.service';
import {AuthorizedAdminGuard} from './guards/authorized-admin.guard';
import {ServiceGroupEditComponent} from './service-group-edit/service-group-edit.component';
import {ServiceGroupSearchComponent} from './service-group-search/service-group-search.component';
import {DomainComponent} from './domain/domain.component';
import {UserComponent} from './user/user.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RowLimiterComponent} from './common/row-limiter/row-limiter.component';
import {DatePipe} from './custom-date/date.pipe';
import {CapitalizeFirstPipe} from './common/capitalize-first.pipe';
import {DefaultPasswordDialogComponent} from './security/default-password-dialog/default-password-dialog.component';
import {ServiceGroupDetailsDialogComponent} from './service-group-edit/service-group-details-dialog/service-group-details-dialog.component';
import {CancelDialogComponent} from './common/dialogs/cancel-dialog/cancel-dialog.component';
import {DirtyGuard} from './common/dirty.guard';
import {SaveDialogComponent} from './common/dialogs/save-dialog/save-dialog.component';
import {ColumnPickerComponent} from './common/column-picker/column-picker.component';
import {PageHelperComponent} from './common/page-helper/page-helper.component';
import {SharedModule} from './common/module/shared.module';
import {ClearInvalidDirective} from './custom-date/clear-invalid.directive';
import {PageHeaderComponent} from './common/page-header/page-header.component';
import {DomainSelectorComponent} from './common/domain-selector/domain-selector.component';
import {SearchTableComponent} from './common/search-table/search-table.component';
import {ServiceGroupMetadataDialogComponent} from './service-group-edit/service-group-metadata-dialog/service-group-metadata-dialog.component';
import {DomainDetailsDialogComponent} from './domain/domain-details-dialog/domain-details-dialog.component';
import {UserDetailsDialogComponent} from './user/user-details-dialog/user-details-dialog.component';
import {DownloadService} from './download/download.service';
import {CertificateService} from './user/certificate.service';
import {GlobalLookups} from './common/global-lookups';
import {ServiceGroupExtensionWizardDialogComponent} from './service-group-edit/service-group-extension-wizard-dialog/service-group-extension-wizard-dialog.component';
import {ServiceMetadataWizardDialogComponent} from './service-group-edit/service-metadata-wizard-dialog/service-metadata-wizard-dialog.component';
import {ConfirmationDialogComponent} from './common/dialogs/confirmation-dialog/confirmation-dialog.component';
import {SpinnerComponent} from './common/spinner/spinner.component';
import {UserService} from './user/user.service';
import {UserDetailsService} from './user/user-details-dialog/user-details.service';
import {ExpiredPasswordDialogComponent} from './common/dialogs/expired-password-dialog/expired-password-dialog.component';
import {DialogComponent} from './common/dialogs/dialog/dialog.component';
import {KeystoreImportDialogComponent} from "./domain/keystore-import-dialog/keystore-import-dialog.component";
import {KeystoreEditDialogComponent} from "./domain/keystore-edit-dialog/keystore-edit-dialog.component";
import {CertificateDialogComponent} from "./common/dialogs/certificate-dialog/certificate-dialog.component";
import {TruststoreEditDialogComponent} from "./user/truststore-edit-dialog/truststore-edit-dialog.component";
import {InformationDialogComponent} from "./common/dialogs/information-dialog/information-dialog.component";
import {KeystoreService} from "./domain/keystore.service";
import {TruststoreService} from "./user/truststore.service";
import {SmlIntegrationService} from "./domain/sml-integration.service";
import {PasswordChangeDialogComponent} from "./common/dialogs/password-change-dialog/password-change-dialog.component";
import {AccessTokenGenerationDialogComponent} from "./common/dialogs/access-token-generation-dialog/access-token-generation-dialog.component";
import {AlertComponent} from "./alert/alert.component";
import {PropertyComponent} from "./property/property.component";
import {PropertyDetailsDialogComponent} from "./property/property-details-dialog/property-details-dialog.component";
import {MatCheckbox, MatCheckboxModule} from "@angular/material/checkbox";
import {AutoFocusDirective} from "./common/directive/autofocus/auto-focus.directive";
import {ObjectPropertiesDialogComponent} from "./common/dialogs/object-properties-dialog/object-properties-dialog.component";
import {MatTableModule} from "@angular/material/table";


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    ServiceGroupEditComponent,
    ServiceGroupSearchComponent,
    AlertComponent,
    PropertyComponent,
    PropertyDetailsDialogComponent,
    DomainComponent,
    DomainDetailsDialogComponent,
    UserComponent,
    AlertMessageComponent,
    FooterComponent,
    SpinnerComponent,
    IsAuthorized,
    SaveDialogComponent,
    ServiceGroupMetadataDialogComponent,
    CancelDialogComponent,
    ConfirmationDialogComponent,
    InformationDialogComponent,
    ObjectPropertiesDialogComponent,
    RowLimiterComponent,
    DatePipe,
    CapitalizeFirstPipe,
    DefaultPasswordDialogComponent,
    ServiceGroupDetailsDialogComponent,
    ServiceGroupExtensionWizardDialogComponent,
    ServiceMetadataWizardDialogComponent,
    ColumnPickerComponent,
    PageHelperComponent,
    ClearInvalidDirective,
    PageHeaderComponent,
    DomainSelectorComponent,
    SearchTableComponent,
    UserDetailsDialogComponent,
    ExpiredPasswordDialogComponent,
    PasswordChangeDialogComponent,
    AccessTokenGenerationDialogComponent,
    DialogComponent,
    KeystoreImportDialogComponent,
    KeystoreEditDialogComponent,
    CertificateDialogComponent,
    TruststoreEditDialogComponent,
    AutoFocusDirective,
  ],
  imports: [
    BrowserModule,
    FlexLayoutModule,
    HttpClientModule,
    HttpClientXsrfModule.withOptions({
      cookieName: 'XSRF-TOKEN',
      headerName: 'X-XSRF-TOKEN'
    }),
    BrowserAnimationsModule,
    FormsModule,
    NgxDatatableModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogModule,
    MatTooltipModule,
    MatToolbarModule,
    MatMenuModule,
    MatInputModule,
    MatIconModule,
    MatListModule,
    MatSidenavModule,
    MatSelectModule,
    MatTabsModule,
    MatSlideToggleModule,
    MatProgressSpinnerModule,
    routing,
    ReactiveFormsModule,
    SharedModule,
    MatExpansionModule,
    MatTableModule,
  ],
  providers: [
    AuthenticatedGuard,
    AuthorizedGuard,
    AuthorizedAdminGuard,
    DirtyGuard,
    HttpEventService,
    SecurityService,
    SecurityEventService,
    DomainService,
    SmpInfoService,
    AlertMessageService,
    DownloadService,
    CertificateService,
    KeystoreService,
    TruststoreService,
    SmlIntegrationService,
    GlobalLookups,
    DatePipe,
    UserService,
    UserDetailsService,
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
