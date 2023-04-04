import {Injectable} from '@angular/core';
import {SecurityEventService} from "../../security/security-event.service";


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
  private static THEME_STORAGE_NAME = "smp-theme";
  private static DEFAULT_THEME_NAME = "default_theme";

  private _themes: ThemeItem[] = SMP_THEME_ITEMS;

  constructor(private securityEventService: SecurityEventService) {

    securityEventService.onLoginSuccessEvent().subscribe(user => {
        // set the last logged user as default theme
        if (!user) {
          return;
        }
        this.persistTheme(user.smpTheme)
      }
    );

  }

  get themes(): ThemeItem[] {
    return SMP_THEME_ITEMS;
  }

  /**
   * Set selected theme to body element
   * @param theme
   */
  setTheme(theme: string) {
    console.log("set theme" + theme)
    this.resetTheme();
    if (!!theme) {
      let body = document.getElementsByTagName('body')[0]
      body.classList.add(theme)
    }
  };

  /**
   * Method set the theme class to body and stores the theme to localStorage
   * @param theme
   */
  persistTheme(theme: string) {
    this.setTheme(theme);
    if (!!theme && theme != ThemeService.DEFAULT_THEME_NAME) {
      localStorage.setItem(ThemeService.THEME_STORAGE_NAME, theme);
    } else {
      localStorage.removeItem(ThemeService.THEME_STORAGE_NAME)
    }
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

  get currentTheme() {
    return localStorage.getItem(ThemeService.THEME_STORAGE_NAME);
  }
}
