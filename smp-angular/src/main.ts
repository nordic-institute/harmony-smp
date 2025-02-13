import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {enableProdMode} from '@angular/core';
import {environment} from './environments/environment';
import {AppModule} from './app/app.module';
// add the following EU locales
import '@angular/common/locales/global/bg';
import '@angular/common/locales/global/cs';
import '@angular/common/locales/global/da';
import '@angular/common/locales/global/de';
import '@angular/common/locales/global/el';
import '@angular/common/locales/global/en';
import '@angular/common/locales/global/es';
import '@angular/common/locales/global/et';
import '@angular/common/locales/global/fi';
import '@angular/common/locales/global/fr';
import '@angular/common/locales/global/hr';
import '@angular/common/locales/global/hu';
import '@angular/common/locales/global/it';
import '@angular/common/locales/global/lt';
import '@angular/common/locales/global/lv';
import '@angular/common/locales/global/mt';
import '@angular/common/locales/global/nl';
import '@angular/common/locales/global/pl';
import '@angular/common/locales/global/pt';
import '@angular/common/locales/global/ro';
import '@angular/common/locales/global/sk';
import '@angular/common/locales/global/sl';
import '@angular/common/locales/global/sv';

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule);
