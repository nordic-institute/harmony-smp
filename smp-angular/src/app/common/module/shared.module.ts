import {NgModule} from '@angular/core';

import {ClickStopPropagationDirective} from 'app/common/directive/attribute/click-stop-propagation.directive';

@NgModule({
  declarations: [
    ClickStopPropagationDirective
  ],
  exports: [
    ClickStopPropagationDirective
  ]
})
export class SharedModule {
}
