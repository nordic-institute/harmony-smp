import {
  FormatWidth,
  getLocaleDateFormat,
  getLocaleDateTimeFormat,
  getLocaleTimeFormat
} from "@angular/common";
import StringUtils from "./string-utils";

/**
 * Date utils provide methods to work with dates
 * such as date comparison and date formatting
 */
export default class DateUtils {
  public static readonly DEFAULT_LOCALE: string = 'fr';
  // ISO8601 extended format + Z indicator for offset 0 (= XXXXX) example  -08:00
  // see details getLocaleDateTimeFormat/ getLocaleTimeFormat
  public static readonly DEFAULT_ZONE_FORMAT: string = ' ZZZZZ';

  /**
   * Method returns the user date time format based on the current locale
   * @param locale - locale to use for formatting
   * @param withSeconds - flag to include seconds in the formatted date time string
   * @param withZone - flag to include the time zone in the formatted date time string
   */
  static getDateTimeFormatForLocal(locale: string, withSeconds: boolean = true, withZone: boolean = false): string {
    locale = locale ? locale : this.DEFAULT_LOCALE;
    let format: string = getLocaleDateTimeFormat(locale, FormatWidth.Short);
    let fullTime = getLocaleTimeFormat(locale, withSeconds ? FormatWidth.Medium : FormatWidth.Short);
    let fullDate = getLocaleDateFormat(locale, FormatWidth.Short);
    let result = StringUtils.format(format, [fullTime, fullDate]) + (withZone ? this.DEFAULT_ZONE_FORMAT : '');
    return result;
  }

  /**
   * Method returns the user date format based on the current locale
   * @param locale  - locale to use for formatting
   * @param withZone - flag to include the time zone in the formatted date time string
   */
  static getDateFormatForLocal(locale: string, withZone: boolean = false): string {
    locale = locale ? locale : this.DEFAULT_LOCALE;
    return getLocaleDateFormat(locale, FormatWidth.Short) + (withZone ? this.DEFAULT_ZONE_FORMAT : '');
  }
}
