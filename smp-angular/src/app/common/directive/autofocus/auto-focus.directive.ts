import {Directive, ElementRef, OnInit} from '@angular/core';

@Directive({
    selector: '[auto-focus-directive]',
    standalone: false
})
export class AutoFocusDirective implements OnInit {
  constructor(private elRef: ElementRef) { }
  ngOnInit(): void {
    this.elRef.nativeElement.focus();
  }
}
