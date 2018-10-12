import {Directive, ElementRef, Input, OnInit} from '@angular/core';
import {SecurityService} from './security.service';
import {Role} from './role.model';

@Directive({
    selector:'[isAuthorized]'
})
export class IsAuthorized implements OnInit {
    @Input('isAuthorized') role: Role;

    constructor(private _elementRef:ElementRef, private securityService:SecurityService) {
    }

    ngOnInit() {
      if(this.role) {
        this.securityService.isAuthorized([this.role]).subscribe((isAuthorized:boolean) => {
          if(!isAuthorized) {
            let el : HTMLElement = this._elementRef.nativeElement;
            el.parentNode.removeChild(el);
          }
        },
        (error:any) => {
          console.log("Error in IsAuthorized directive [" + error + "]");
        });
      }
    }
}
