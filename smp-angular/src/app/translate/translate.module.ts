import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClient, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from "../i18n/translate-http-loader";

@NgModule({ declarations: [],
    exports: [TranslateModule], imports: [CommonModule,
        TranslateModule.forRoot({
            // defaultLanguage: 'en', // Using this instead of the app component's setDefaultLanguage("en") call results in a cyclic dependency (HttpSessionInterceptor -> SecurityService -> ...)
            loader: {
                provide: TranslateLoader,
                useClass: TranslateHttpLoader,
                deps: [HttpClient]
            }
        })], providers: [provideHttpClient(withInterceptorsFromDi())] })
export class NgxTranslateModule { }
