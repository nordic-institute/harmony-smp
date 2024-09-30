import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from "../i18n/translate-http-loader";

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HttpClientModule,
    TranslateModule.forRoot({
      // defaultLanguage: 'en', // Using this instead of the app component's setDefaultLanguage("en") call results in a cyclic dependency (HttpSessionInterceptor -> SecurityService -> ...)
      loader: {
        provide: TranslateLoader,
        useClass: TranslateHttpLoader,
        deps: [HttpClient]
      }
    }),
  ],
  exports: [TranslateModule],
})
export class NgxTranslateModule { }
