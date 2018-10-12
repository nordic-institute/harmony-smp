import {Injectable} from '@angular/core';

@Injectable()
export class DateFormatService {

  format(date: Date): string {
    return this.getFormattedDate(date) + "_" + this.getFormattedTime(date);
  }

  private getFormattedDate(date: Date): string {
    return ("0" + date.getDate()).slice(-2).toString()+"-"+
      ("0" + (date.getMonth() + 1)).slice(-2).toString()+"-"+
      date.getFullYear().toString();
  }

  private  getFormattedTime(date: Date): string {
    return ("0" + date.getHours()).slice(-2).toString()+"h"+
      ("0" + date.getMinutes()).slice(-2).toString()+"m"+
      ("0" + date.getSeconds()).slice(-2).toString();
  }
}
