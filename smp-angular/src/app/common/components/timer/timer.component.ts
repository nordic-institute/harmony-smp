import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from "@angular/core";
import {TranslateService} from "@ngx-translate/core";
import {lastValueFrom} from "rxjs";

@Component({
    selector: 'timer',
    templateUrl: './timer.component.html',
    standalone: false
})
export class TimerComponent implements OnInit, OnDestroy {

  @Input() durationInSeconds = 60;
  @Input() countingDown: boolean;
  @Output() finished = new EventEmitter<void>();

  timeLabel = '';
  private currentTime = 0;
  private intervalId: any;

  constructor(private translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.currentTime = this.countingDown ? this.durationInSeconds: 0;
    (async () => await this.updateTimeLabel()) ();
    this.startCounting();
  }

  startCounting(): void {
    this.intervalId = setInterval(() => {
      if (this.isTimeOut()) {
        this.clearTimer();
        this.finished.emit();
      }else {
        this.currentTime = this.countingDown ? this.currentTime - 1: this.currentTime + 1;
      }
    }, 1000);
  }

  get formattedTime(): string {
    const minutes = Math.floor(this.currentTime / 60).toString().padStart(2, '0');
    const seconds = (this.currentTime % 60).toString().padStart(2, '0');
    return `${minutes}:${seconds}`;
  }

  reset(): void {
    this.clearTimer();
    this.startCounting();
  }

  private clearTimer(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }

  async updateTimeLabel() {
    this.timeLabel = this.countingDown
      ? await lastValueFrom(this.translateService.get("timer.label.time.remaining"))
      : await lastValueFrom(this.translateService.get("timer.label.time.current"));
  }

  ngOnDestroy(): void {
    this.clearTimer();
  }

  private isTimeOut(): boolean {
    return (this.countingDown && this.currentTime == 0)
      || (!this.countingDown && this.currentTime == this.durationInSeconds);
  }

}
