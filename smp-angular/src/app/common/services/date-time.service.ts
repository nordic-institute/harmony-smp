import {Injectable} from '@angular/core';
import {GlobalLookups} from "../global-lookups";
import {DatePipe} from "@angular/common";
import DateUtils from "../utils/date-utils";

/**
 * Service to handle date time formatting and parsing
 *
 * @since 5.1
 * @author Joze RIHTARSIC
 */
@Injectable()
export class DateTimeService {

  currentDateExample: Date = new Date();

  constructor(
    private lookups: GlobalLookups,
    private datePipe: DatePipe
  ) { }


  /**
   * Method returns the user date time format based on the current locale
   */
  public get userDateTimeFormat(): string {
    // get currently selected locale
    let locale = this.lookups.getCurrentLocale();
    return DateUtils.getDateTimeFormatForLocal(locale);
  }

  /**
   * Method returns the user date format based on the current locale
   */
  public get userDateFormat(): string {
    // get currently selected locale
    let locale = this.lookups.getCurrentLocale();
    return DateUtils.getDateFormatForLocal(locale);
  }

  /**
   * Method returns the formatted date with user locale settings
   * @param date - date to format
   * @param showSeconds - flag to show seconds in the formatted date time string
   * @param nullValue - value to return if date is null
   */
  public formatDateForUserLocal(date: Date,  nullValue: string = "---"): string {
    let locale = this.lookups.getCurrentLocale();
    return this.formatDateForLocal(date, locale, nullValue);
  }

  /**
   * Method returns the formatted date with user locale settings
   * @param date - date to format
   * @param showSeconds - flag to show seconds in the formatted date time string
   * @param nullValue - value to return if date is null
   */
  public formatDateTimeForUserLocal(date: Date, showSeconds: boolean = true, nullValue: string = "---"): string {
    let locale = this.lookups.getCurrentLocale();
    return this.formatDateTimeForLocal(date, locale, showSeconds, nullValue);
  }

  /**
   * Method returns the user date format based on the current locale
   * @param date - date to format
   * @param locale - locale to use for formatting
   * @param nullValue - value to return if date is null
   */
  public formatDateForLocal(date: Date, locale: string, nullValue: string = "---"): string {
    if (!date) {
      return nullValue;
    }
    let dateTimeFormat = DateUtils.getDateFormatForLocal(locale);
    return this.datePipe.transform(date, dateTimeFormat);
  }

  /**
   * Method returns the user date format based on the current locale
   * @param date - date to format
   * @param locale - locale to use for formatting
   * @param showSeconds - flag to show seconds in the formatted date time string
   * @param nullValue - value to return if date is null
   */
  public formatDateTimeForLocal(date: Date, locale: string, showSeconds: boolean = true, nullValue: string = "---"): string {
    if (!date) {
      return nullValue;
    }
    let dateTimeFormat = DateUtils.getDateTimeFormatForLocal(locale, showSeconds);
    return this.datePipe.transform(date, dateTimeFormat);
  }

  /**
   * Method returns a formatted example date time string for the current date
   * @param showSeconds - flag to show seconds in the formatted date time string
   */
   public formattedUserDateTimeExample(showSeconds: boolean = true): string {
    let locale = this.lookups.getCurrentLocale();
    return this.formatDateTimeForLocal(this.currentDateExample, locale, showSeconds);
  }

}
