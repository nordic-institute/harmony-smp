import {Component, ViewChild, ViewEncapsulation,} from '@angular/core';
import {
  BeforeLeaveGuard
} from "../../../../window/sidenav/navigation-on-leave-guard";
import {DocumentEditPanelComponent} from "../../../../common/panels/document-edit-panel/document-edit-panel.component";

@Component({
    templateUrl: './domain-document-template-edit-panel.component.html',
    styleUrls: ['./domain-document-template-edit-panel.component.scss'],
    encapsulation: ViewEncapsulation.None,
    standalone: false
})
export class DomainDocumentTemplateEditPanelComponent implements BeforeLeaveGuard {

  @ViewChild('domainDocumentTemplateEditor') documentEditor: DocumentEditPanelComponent;


  constructor() {
  }

  isDirty(): boolean {
    return false;
  }

}
