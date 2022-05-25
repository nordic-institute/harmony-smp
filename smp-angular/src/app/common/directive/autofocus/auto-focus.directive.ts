import { Directive, ElementRef, OnInit } from '@angular/core';

@Directive({
  selector: '[auto-focus-directive]'
})
export class AutoFocusDirective implements OnInit {
  constructor(private elRef: ElementRef) { }
  ngOnInit(): void {
    this.elRef.nativeElement.focus();
  }
}
