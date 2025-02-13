import {EventEmitter, Injectable} from '@angular/core';
import {SecurityEventService} from "../../security/security-event.service";
import {LocalStorageService} from "../services/local-storage.service";


/**
 * The smp navigation tree
 */

let SMP_THEME_ITEMS: ThemeItem[] = [
  {
    className: "default_theme",
    name: "SMP default theme",
  },
  {
    className: "blue_theme",
    name: "Blue theme",
  },
  {
    className: "indigo_pink_theme",
    name: "Indigo & Pink theme",
  },
  {
    className: "pink_blue-grey_theme",
    name: "Pink & Blue grey",
  },
  {
    className: "purple_green_theme",
    name: "Purple & Green theme",
  }
];

/**
 * Theme data. The Theme classes are defined in theme.scss file!.
 */
export interface ThemeItem {
  className: string;
  name: string;

}

/**
 * Service handles the SMP theme actions
 */
@Injectable()
export class ThemeService {

  selectedTheme: EventEmitter<string> = new EventEmitter<string>();
  private static THEME_STORAGE_NAME: string = "smp-theme";
  private static DEFAULT_THEME_NAME: string = "default_theme";

  private _themes: ThemeItem[] = SMP_THEME_ITEMS;

  constructor(private securityEventService: SecurityEventService,
              private localStorageService: LocalStorageService) {

    securityEventService.onLoginSuccessEvent().subscribe(user => {
        // set the last logged user as default theme
        if (!user) {
          return;
        }
        this.persistTheme(user.smpTheme)
      }
    );

  }

  getThemeChangedEventEmitter(): EventEmitter<string> {
    return this.selectedTheme;
  }

  get themes(): ThemeItem[] {
    return SMP_THEME_ITEMS;
  }

  /**
   * Set selected theme to body element
   * @param theme
   */
  setTheme(theme: string) {
    this.resetTheme();
    if (!!theme) {
      let body = document.getElementsByTagName('body')[0]
      body.classList.add(theme)
    }
    this.selectedTheme.emit(theme);
  };

  /**
   * Method set the theme class to body and stores the theme to localStorage
   * @param theme
   */
  persistTheme(theme: string) {
    this.setTheme(theme);
    this.localStorageService.saveUserTheme(theme);
  };

  /**
   * Method sets theme from local storage
   */
  updateThemeFromLocalStorage() {
    let theme = this.currentTheme;
    this.setTheme(theme);
  };

  /**
   * The method removes all theme classes from the body
   */
  public resetTheme() {
    let themeList: string[] = this._themes.map(node => node.className)
    let body = document.getElementsByTagName('body')[0]
    // clear themes from body class list
    body.classList.remove(...themeList);
  }

  get currentTheme(): string {
    return this.localStorageService.getUserTheme();
  }
}
