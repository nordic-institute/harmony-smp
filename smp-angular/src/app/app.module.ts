import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {FlexLayoutModule} from '@angular/flex-layout';
import {
  MatButtonModule,
  MatCardModule,
  MatDatepickerModule,
  MatDialogModule,
  MatExpansionModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatSelectModule,
  MatSidenavModule,
  MatSlideToggleModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule,
} from '@angular/material';
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
import {AlertComponent} from './alert/alert.component';
import {AlertService} from './alert/alert.service';

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
import {CancelDialogComponent} from './common/cancel-dialog/cancel-dialog.component';
import {DirtyGuard} from './common/dirty.guard';
import {SaveDialogComponent} from './common/save-dialog/save-dialog.component';
import {ColumnPickerComponent} from './common/column-picker/column-picker.component';
import {PageHelperComponent} from './common/page-helper/page-helper.component';
import {SharedModule} from './common/module/shared.module';
import {ClearInvalidDirective} from './custom-date/clear-invalid.directive';
import {PageHeaderComponent} from './common/page-header/page-header.component';
import {DomainSelectorComponent} from './common/domain-selector/domain-selector.component';
import {AlertsComponent} from './alerts/alerts.component';

import {SearchTableComponent} from './common/search-table/search-table.component';
import {ServiceGroupMetadataDialogComponent} from './service-group-edit/service-group-metadata-dialog/service-group-metadata-dialog.component';
import {DomainDetailsDialogComponent} from './domain/domain-details-dialog/domain-details-dialog.component';
import {UserDetailsDialogComponent} from './user/user-details-dialog/user-details-dialog.component';
import {DownloadService} from './download/download.service';
import {CertificateService} from './user/certificate.service';
import {GlobalLookups} from './common/global-lookups';
import {ServiceGroupExtensionWizardDialogComponent} from './service-group-edit/service-group-extension-wizard-dialog/service-group-extension-wizard-dialog.component';
import {ServiceMetadataWizardDialogComponent} from './service-group-edit/service-metadata-wizard-dialog/service-metadata-wizard-dialog.component';
import {ConfirmationDialogComponent} from './common/confirmation-dialog/confirmation-dialog.component';
import {SpinnerComponent} from './common/spinner/spinner.component';
import {UserService} from './user/user.service';
import {UserDetailsService} from './user/user-details-dialog/user-details.service';
import { ExpiredPasswordDialogComponent } from './common/expired-password-dialog/expired-password-dialog.component';
import { DialogComponent } from './common/dialog/dialog.component';
import {KeystoreImportDialogComponent} from "./domain/keystore-import-dialog/keystore-import-dialog.component";
import {KeystoreEditDialogComponent} from "./domain/keystore-edit-dialog/keystore-edit-dialog.component";
import {KeystoreCertificateDialogComponent} from "./domain/keystore-certificate-dialog/keystore-certificate-dialog.component";
import {InformationDialogComponent} from "./common/information-dialog/information-dialog.component";
import {KeystoreService} from "./domain/keystore.service";
import {SmlIntegrationService} from "./domain/sml-integration.service";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    ServiceGroupEditComponent,
    ServiceGroupSearchComponent,
    DomainComponent,
    DomainDetailsDialogComponent,
    UserComponent,
    AlertComponent,
    FooterComponent,
    SpinnerComponent,
    IsAuthorized,
    SaveDialogComponent,
    ServiceGroupMetadataDialogComponent,
    CancelDialogComponent,
    ConfirmationDialogComponent,
    InformationDialogComponent,
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
    AlertsComponent,
    SearchTableComponent,
    UserDetailsDialogComponent,
    ExpiredPasswordDialogComponent,
    DialogComponent,
    KeystoreImportDialogComponent,
    KeystoreEditDialogComponent,
    KeystoreCertificateDialogComponent,
  ],
  entryComponents: [
    AppComponent,
    ServiceGroupMetadataDialogComponent,
    ServiceGroupDetailsDialogComponent,
    ServiceGroupExtensionWizardDialogComponent,
    ServiceMetadataWizardDialogComponent,
    DomainDetailsDialogComponent,
    UserDetailsDialogComponent,
    CancelDialogComponent,
    ConfirmationDialogComponent,
    InformationDialogComponent,
    SaveDialogComponent,
    DefaultPasswordDialogComponent,
    ExpiredPasswordDialogComponent,
    KeystoreImportDialogComponent,
    KeystoreEditDialogComponent,
    KeystoreCertificateDialogComponent,
  ],
  imports: [
    BrowserModule,
    FlexLayoutModule,
    HttpClientModule,
    BrowserAnimationsModule,
    FormsModule,
    NgxDatatableModule,
    MatButtonModule,
    MatCardModule,
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
    routing,
    ReactiveFormsModule,
    SharedModule,
    MatExpansionModule,
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
    AlertService,
    DownloadService,
    CertificateService,
    KeystoreService,
    SmlIntegrationService,
    GlobalLookups,
    DatePipe,
    UserService,
    UserDetailsService,
    {
      provide: ExtendedHttpClient,
      useFactory: extendedHttpClientCreator,
      deps: [HttpClient, HttpEventService, SecurityService]
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
