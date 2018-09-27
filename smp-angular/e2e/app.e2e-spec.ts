import { SmpAngular2WebPage } from './app.po';

describe('domibus-MSH-web App', function() {
  let page: SmpAngular2WebPage;

  beforeEach(() => {
    page = new SmpAngular2WebPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
