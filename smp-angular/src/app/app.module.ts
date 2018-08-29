import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Http, HttpModule, RequestOptions, XHRBackend} from "@angular/http";
import {
  MdButtonModule,
  MdDialogModule,
  MdIconModule,
  MdInputModule,
  MdListModule,
  MdMenuModule,
  MdSelectModule,
  MdSidenavModule,
  MdTooltipModule,
  MdExpansionModule
} from "@angular/material";
import "hammerjs";

import {NgxDatatableModule} from "@swimlane/ngx-datatable";
import {Md2Module, Md2SelectModule} from "md2";

import {AppComponent} from "./app.component";
import {LoginComponent} from "./login/login.component";
import {HomeComponent} from "./home/home.component";



import {AuthenticatedGuard} from "./guards/authenticated.guard";
import {AuthorizedGuard} from "./guards/authorized.guard";
import {routing} from "./app.routes";
import {IsAuthorized} from "./security/is-authorized.directive";
import {ExtendedHttpClient} from "./http/extended-http-client";
import {HttpEventService} from "./http/http.event.service";
import {SecurityService} from "./security/security.service";
import {SecurityEventService} from "./security/security.event.service";
import {DomainService} from "./security/domain.service";
import {AlertComponent} from "./alert/alert.component";
import {AlertService} from "./alert/alert.service";
import {ErrorLogComponent} from "./errorlog/errorlog.component";
import {FooterComponent} from "./footer/footer.component";
import {DomibusInfoService} from "./appinfo/domibusinfo.service";
import {AuthorizedAdminGuard} from "./guards/authorized-admin.guard";
import {MessageFilterComponent} from "./messagefilter/messagefilter.component";
import {ServiceGroupComponent} from "./servicegroup/servicegroup.component";
import {DomainComponent} from "./domain/domain.component";
import {UserComponent} from "./user/user.component";
import {TruststoreComponent} from "./truststore/truststore.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ServicegroupMetadatalistDialogComponent} from "./servicegroup/servicegroup-metadatalist-dialog/servicegroup-metadatalist-dialog.component";
import {RowLimiterComponent} from "./common/row-limiter/row-limiter.component";
import {DatePipe} from "./customDate/datePipe";
import {CapitalizeFirstPipe} from "./common/capitalizefirst.pipe";
import {DefaultPasswordDialogComponent} from "./security/default-password-dialog/default-password-dialog.component";
import {ServicegroupDetailsDialogComponent} from "./servicegroup/servicegroup-details-dialog/servicegroup-details-dialog.component";
import {ErrorlogDetailsComponent} from "./errorlog/errorlog-details/errorlog-details.component";
import {EditMessageFilterComponent} from "./messagefilter/editmessagefilter-form/editmessagefilter-form.component";
import {CancelDialogComponent} from "./common/cancel-dialog/cancel-dialog.component";
import {DirtyGuard} from "./common/dirty.guard";
import {SaveDialogComponent} from "./common/save-dialog/save-dialog.component";
import {TruststoreDialogComponent} from "./truststore/truststore-dialog/truststore-dialog.component";
import {TrustStoreUploadComponent} from "./truststore/truststore-upload/truststore-upload.component";
import {ColumnPickerComponent} from "./common/column-picker/column-picker.component";
import {PageHelperComponent} from "./common/page-helper/page-helper.component";
import {SharedModule} from "./common/module/SharedModule";;
import {ClearInvalidDirective} from "./customDate/clearInvalid.directive";
import {PageHeaderComponent} from "./common/page-header/page-header.component";
import {DomainSelectorComponent} from "./common/domain-selector/domain-selector.component";
import {AlertsComponent} from "./alerts/alerts.component";

import {SearchTableComponent} from "./common/searchtable/searchtable.component";
import {ServiceGroupExtensionDialogComponent} from "./servicegroup/servicegroup-extension-dialog/servicegroup-extension-dialog.component";
import {ServicegroupMetadataDialogComponent} from "./servicegroup/servicegroup-metadata-dialog/servicegroup-metadata-dialog.component";
import {DomainDetailsDialogComponent} from "./domain/domain-details-dialog/domain-details-dialog.component";
import {UserDetailsDialogComponent} from "./user/user-details-dialog/user-details-dialog.component";


export function extendedHttpClientFactory(xhrBackend: XHRBackend, requestOptions: RequestOptions, httpEventService: HttpEventService) {
  return new ExtendedHttpClient(xhrBackend, requestOptions, httpEventService);
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    MessageFilterComponent,
    ServiceGroupComponent,
    DomainComponent,
    DomainDetailsDialogComponent,
    UserComponent,
    ErrorLogComponent,
    AlertComponent,
    FooterComponent,
    IsAuthorized,
    TruststoreComponent,
    SaveDialogComponent,
    ServicegroupMetadatalistDialogComponent,
    ServicegroupMetadataDialogComponent,
    ServiceGroupExtensionDialogComponent,
    CancelDialogComponent,
    RowLimiterComponent,
    DatePipe,
    CapitalizeFirstPipe,
    DefaultPasswordDialogComponent,
    EditMessageFilterComponent,
    ServicegroupDetailsDialogComponent,
    ErrorlogDetailsComponent,
    TruststoreDialogComponent,
    TrustStoreUploadComponent,
    ColumnPickerComponent,
    TrustStoreUploadComponent,
    PageHelperComponent,
    ClearInvalidDirective,
    PageHeaderComponent,
    DomainSelectorComponent,
    AlertsComponent,
    SearchTableComponent,
    UserDetailsDialogComponent
  ],
  entryComponents: [
    AppComponent,
    ServicegroupMetadatalistDialogComponent,
    ServicegroupMetadataDialogComponent,
    ServicegroupDetailsDialogComponent,
    ServiceGroupExtensionDialogComponent,
    DomainDetailsDialogComponent,
    UserDetailsDialogComponent,
    CancelDialogComponent,
    SaveDialogComponent,
    DefaultPasswordDialogComponent,
    EditMessageFilterComponent,
    ErrorlogDetailsComponent,
    TruststoreDialogComponent,
    TrustStoreUploadComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpModule,
    NgxDatatableModule,
    MdButtonModule,
    MdDialogModule,
    MdTooltipModule,
    MdMenuModule,
    MdInputModule,
    MdIconModule,
    MdListModule,
    MdSidenavModule,
    MdSelectModule,
    routing,
    ReactiveFormsModule,
    Md2Module,
    Md2SelectModule,
    SharedModule,
    MdExpansionModule
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
    DomibusInfoService,
    AlertService,
    {
      provide: Http,
      useFactory: extendedHttpClientFactory,
      deps: [XHRBackend, RequestOptions, HttpEventService],
      multi: false
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
