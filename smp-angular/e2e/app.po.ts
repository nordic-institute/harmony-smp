import { browser, element, by } from 'protractor';

export class SmpAngular2WebPage {
  navigateTo() {
    return browser.get('/');
  }

  getParagraphText() {
    return element(by.css('app-root h1')).getText();
  }
}
